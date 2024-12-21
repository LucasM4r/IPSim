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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import com.ipsim.exceptions.LexicalException;
import com.ipsim.exceptions.SyntacticException;
import com.ipsim.exceptions.SemanticException;
import com.ipsim.exceptions.CodeGenerationException;
import com.ipsim.interfaces.Processor;
import com.ipsim.processor.neander.cpu.NeanderProcessor;
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
    private TextArea errorConsole;

    private List<Processor> cpusArray = new ArrayList<>();
    private Processor currentProcessor;

    public void initialize() {
        textEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers();
            scrollToEnd(); // Update line numbers and scroll to the end
        });
        updateLineNumbers();
        scrollToEnd();
        cpusArray.add((Processor)new NeanderProcessor());
        generateAsmButtons();
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
     * The show method shows the second stage
     */
    private void show() {
        // Lógica para executar o código
        System.out.println("Executando o código...");
        try {
            createSecondStage();
            
        } catch (Exception e) {
            e.printStackTrace();
            errorConsole.appendText("Failed to create second stage: " + e.getMessage() + "\n");
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
        } catch (Exception e) {
            e.printStackTrace();
            errorConsole.appendText("Failed to execute program: " + e.getMessage() + "\n");
        }
    }
    @FXML
    private void runStep() {

        currentProcessor.executeStep();
        SimulatorController.updateUI();
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
    /**
     * The createSecondStage method creates the second stage
     * @throws Exception
     */
    private void createSecondStage() throws Exception {
        Stage secondStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ipsim/simulator.fxml"));
        secondStage.setTitle("Simulator");
        HBox root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/ipsim/styles/Styles.css").toExternalForm());
        secondStage.setScene(scene);
        secondStage.setMinWidth(500);
        secondStage.setMinHeight(300);
        secondStage.show();
    
        SimulatorController.setStage(secondStage);
        SimulatorController.setCurrentProcessor(currentProcessor);
    }
}