/**
 * The EditorController class is the controller class for the editor view
 * @author Lucas Marchesan da Silva
 * @version 1.0
 */
package com.ipsim;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ipsim.components.Memory;
import com.ipsim.components.Register;
import com.ipsim.exceptions.CodeGenerationException;
import com.ipsim.exceptions.LexicalException;
import com.ipsim.exceptions.SemanticException;
import com.ipsim.exceptions.SyntacticException;
import com.ipsim.interfaces.Processor;
import com.ipsim.processor.neander.cpu.NeanderProcessor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
public class EditorController {   
    
    private File currentFile;

    @FXML
    private TextArea textEditor;
    @FXML
    private VBox lineNumberFlow;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ToggleGroup toggleGroup = new ToggleGroup(); // Button group for processor selection
    @FXML
    private HBox toggleButtonContainer;
    @FXML
    private ToggleGroup simulatorToggleGroup = new ToggleGroup(); // Button group for memory selection
    @FXML 
    private TextArea errorConsole;

    @FXML
    private HBox simulatorButtonContainer;

    @FXML
    private GridPane registerContainer;
    @FXML
    private HBox memoriesContainer;
    private List<Processor> cpusArray = new ArrayList<>();
    private Processor currentProcessor;

    private Map<String, TextField> registerFields = new HashMap<>();
    private static List<TextField> memoryFields = new ArrayList<>();
    public void initialize() {
        textEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers();
            scrollToEnd(); // Update line numbers and scroll to the end
        });
        updateLineNumbers();
        scrollToEnd();
        cpusArray.add((Processor)new NeanderProcessor());
        generateAsmButtons();
        showRegisters();
        showMemory();
    }


    /**
     * The getContent method returns the content of the text editor
     * @return
     */
    public String getContent() {
        return textEditor.getText();
    }
    /**
     * The setContent method sets the content of the text editor
     * @param content
     */
    public void setContent(String content) {
        textEditor.setText(content);
    }

    /**
     * The updateLineNumbers method updates the line numbers in the editor
     */
    private void updateLineNumbers() {
        String text = getContent();
        int lineCount = text.split("\n", -1).length;
        
        int totalLines = lineCount;
        double scrollValue = scrollPane.getVvalue();
        double visibleHeight = scrollPane.getViewportBounds().getHeight();
        double lineHeight = textEditor.getFont().getSize();
        // Calculate the number of lines that can be displayed in the editor
        int linesPerPage = (int) (visibleHeight / lineHeight);

        // Calculate the line number where the editor should start
        int startLine = (int) (scrollValue * (totalLines - linesPerPage));
        // Ensure that the start line is at least 1
        startLine = Math.max(startLine, 1);
        // Calculate the line number where the editor should end
        int endLine = Math.min(startLine + linesPerPage, totalLines);
        lineNumberFlow.getChildren().clear();
        // Add line numbers to the editor  
        for (int i = startLine; i <= endLine; i++) {
            Label lineNumberLabel = new Label(String.valueOf(i));
            lineNumberLabel.getStyleClass().add("line-number");
            lineNumberFlow.getChildren().add(lineNumberLabel);
        }
    }
    /**
     * The scrollToEnd method scrolls to the 
     * new position of the text editor after updating the line numbers
     */
    private void scrollToEnd() {
        Platform.runLater(() -> {
            // Calculate the caret position as a percentage of the total length
            double caretPosition = textEditor.getCaretPosition();
            double max = textEditor.getLength();
            double value = caretPosition / max;
            scrollPane.setVvalue(value);
        });
    }


    /**
     * The generateAsmButtons method generates buttons for the processors
     */
    private void generateAsmButtons() {
        System.out.println("Gerando botões para processadores...");
        if (cpusArray.isEmpty()) {
            System.out.println("cpusArray está vazio.");
        }
        for (Processor processor : cpusArray) {
            System.out.println("Criando botão para: " + processor.getName());
            ToggleButton button = new ToggleButton(processor.getName());
            toggleButtonContainer.getChildren().add(button);
            button.setToggleGroup(toggleGroup);
            if(currentProcessor == null && !cpusArray.isEmpty()) {
                currentProcessor = cpusArray.get(0);
                button.setSelected(true);
                System.out.println("Nenhum processador selecionado. Selecionando o primeiro processador da lista: " + currentProcessor.getName());
            }
            button.setOnAction(e -> {
                currentProcessor = processor;
                System.out.println("Processador selecionado: " + processor.getName());
            });
        }
    }

    @FXML
    /**
     * The compile method compiles the code in the text editor
     */
    private void compile() {
        errorConsole.clear(); // Clear the error console
        if (currentFile == null && !textEditor.getText().isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            // Set the file extension filter to .asm files 
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Files", "*.asm"));
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                try {
                    // Write the content of the text editor to the file and compile it
                    Files.write(file.toPath(), textEditor.getText().getBytes());
                    currentFile = file;
                    compileAndSaveBinary(currentFile);
                } catch (IOException | LexicalException | SyntacticException | SemanticException | CodeGenerationException e) {
                    errorConsole.appendText("Compilation failed: " + e.getMessage() + "\n");
                }
            } else {
                errorConsole.appendText("No file is currently open and text editor is empty.\n");
            }
        } else {
            try {
                Files.write(Paths.get(currentFile.getAbsolutePath()), textEditor.getText().getBytes());
                compileAndSaveBinary(currentFile);

            } catch (IOException | LexicalException | SyntacticException | SemanticException | CodeGenerationException e) {
                errorConsole.appendText("Compilation failed: " + e.getMessage() + "\n");
            }
        }
    }

    /**
     * The compileAndSaveBinary method compiles the code and saves the binary file
     * @param file
     * @throws IOException
     * @throws LexicalException
     * @throws SyntacticException
     * @throws SemanticException
     * @throws CodeGenerationException
     */
    private void compileAndSaveBinary(File file) throws IOException, LexicalException, SyntacticException, SemanticException, CodeGenerationException {
        // Compile the code and get the binary code
        String binaryCode = currentProcessor.compile(file);
        // Open a dialog to save the binary file
        FileChooser fileChooser = new FileChooser();
        // Set the file extension filter to .bin files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Files", "*.bin"));
        File binaryFile = fileChooser.showSaveDialog(new Stage());
        if (binaryFile != null) {
            Files.write(binaryFile.toPath(), binaryCode.getBytes());
            System.out.println("Binary code saved to: " + binaryFile.getAbsolutePath());
        }
    }

    @FXML
    /**
     * The run method runs the code in the text editor
     */
    private void run() {
        try {
            if (currentFile != null) {
                Files.write(currentFile.toPath(), textEditor.getText().getBytes());
            }
            currentProcessor.compile(currentFile);
            currentProcessor.executeProgram();
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            errorConsole.appendText("Failed to execute program: " + e.getMessage() + "\n");
        }
    }
    @FXML
    private void runStep() {

        currentProcessor.executeStep();
        updateUI();
    }
    @FXML
    /**
     * The openFile method opens a file
     */
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Files", "*.asm"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                textEditor.setText(content);
                currentFile = file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    /**
     * The createNewFile method creates a new file
     */
    private void createNewFile() {
        if (!textEditor.getText().isEmpty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Save File");
            alert.setHeaderText("You have unsaved changes.");
            alert.setContentText("Do you want to save the current file before creating a new one?");

            ButtonType buttonTypeSave = new ButtonType("Save");
            ButtonType buttonTypeDontSave = new ButtonType("Don't Save");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());

            alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeDontSave, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeSave) {
                if (!saveFile()) {
                    return;
                }
            } else if (result.get() == buttonTypeCancel) {
                return;
            }
        }
        textEditor.clear();
        currentFile = null;
    }

    @FXML
    /**
     * The saveFile method saves the current file
     * @return
     */
    private boolean saveFile() {
        if (currentFile != null) {
            try {
                Files.write(Paths.get(currentFile.getAbsolutePath()), textEditor.getText().getBytes());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return saveAsFile();
        }
    }

    @FXML
    /**
     * The saveAsFile method saves the current file as a new file
     * @return
     */
    private boolean saveAsFile() {
        FileChooser fileChooser = new FileChooser(); 
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Files", "*.asm"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                Files.write(Paths.get(file.getAbsolutePath()), textEditor.getText().getBytes());
                currentFile = file;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void generateSimulatorMemoryButtons() {
        
        Map<String, Memory> memories = currentProcessor.getDatapath().getMemories();
        for(Map.Entry<String, Memory> memory : memories.entrySet()) {
            System.out.println("Criando botão para memória: " + memory.getKey());
            ToggleButton button = new ToggleButton(memory.getKey());
            button.setToggleGroup(simulatorToggleGroup);
            button.getStyleClass().add("button");
            button.setOnAction(e -> {
                showMemory();
            });
            simulatorButtonContainer.getChildren().add(button);
            
        }
    }
    @FXML
    private void showRegisters() {
        registerFields.clear();
        Map<String, Register> registers = currentProcessor.getDatapath().getRegisters();
        int row = 0;
        registerContainer.getChildren().clear();


        for(Map.Entry<String, Register> register : registers.entrySet()) {
            if(registerContainer != null) {
                Label registerLabel = new Label(register.getKey());
                registerLabel.getStyleClass().add("register-label");
                registerContainer.add(registerLabel, 0, row);

                TextField valueField = new TextField("0x" + register.getValue().getHexValue());
                valueField.getStyleClass().add("value-field");
                registerContainer.add(valueField, 1, row);

                registerFields.put(register.getKey(), valueField);
                row++;
            }
        }
    }
    private void saveRegisters() {
        if (registerFields == null || registerFields.isEmpty()) {
            throw new NullPointerException("Register fields not found");
        }

        // Get the registers from the processor
        Map<String, Register> registers = currentProcessor.getDatapath().getRegisters();

        // Iterate over the register fields and update the register values
        for (Map.Entry<String, TextField> entry : registerFields.entrySet()) {
            String registerName = entry.getKey();
            TextField valueField = entry.getValue();
            String value = valueField.getText();
            try {
                int registerValue;
                if (value.startsWith("0x")) {
                    // Hexadecimal value
                    value = value.substring(2);
                    registerValue = Integer.parseInt(value, 16);
                } else {
                    // Decimal value
                    registerValue = Integer.parseInt(value);
                }
                registers.get(registerName).write(registerValue);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Invalid value for register: " + registerName);
            }
        }
    }
    @FXML
    public void showMemory() {
        memoryFields.clear();
        Map<String, Memory> memories = currentProcessor.getDatapath().getMemories();
        int i = 0;
        for(Map.Entry<String, Memory> entry : memories.entrySet()) {

            GridPane gridPane = new GridPane();
            gridPane.getStyleClass().add("memory-grid" + i);
            memoriesContainer.getChildren().add(gridPane);

            Label memoryNameHeader = new Label(entry.getKey());
            memoryNameHeader.getStyleClass().add("memory-name-header");
            gridPane.add(memoryNameHeader, 0, 0, 2, 1); // Span across two columns
            Label memoryHeader = new Label("Address");
            memoryHeader.getStyleClass().add("address-label");
            gridPane.add(memoryHeader, 0, 1);
            Label valueHeader = new Label("Value");
            valueHeader.getStyleClass().add("value-label");
            gridPane.add(valueHeader, 1, 1);
            Memory memory = entry.getValue();
            i++;
            for(int j=2; j<memory.getSize(); j++) {
                Label addressLabel = new Label("0x" + Integer.toHexString(j));
                addressLabel.getStyleClass().add("address-label");
                gridPane.add(addressLabel, 0, j);

                TextField valueField = new TextField("0x" + Integer.toHexString(memory.read(j)));
                valueField.getStyleClass().add("value-field");
                gridPane.add(valueField, 1, j);
                memoryFields.add(valueField);
            }
            ScrollPane memoryScrollPane = new ScrollPane(gridPane);
            memoryScrollPane.setFitToWidth(true);
            memoryScrollPane.setFitToHeight(true);
            memoriesContainer.getChildren().add(memoryScrollPane);
        }
    }
    public void updateUI() {
        Map<String, Register> registers = currentProcessor.getDatapath().getRegisters();
        for (Map.Entry<String, Register> entry : registers.entrySet()) {
            TextField valueField = registerFields.get(entry.getKey());
            if (valueField != null) {
                valueField.setText("0x" + entry.getValue().getHexValue());
            }
        }
    
        Map<String, Memory> memories = currentProcessor.getDatapath().getMemories();
        for (Memory memory : memories.values()) {
            for (int i = 0; i < memory.getSize(); i++) {
                if (i < memoryFields.size()) {
                    TextField valueField = memoryFields.get(i);
                    if (valueField != null) {
                        valueField.setText("0x" + Integer.toHexString(memory.read(i)));
                    }
                }
            }
        }
    }
}