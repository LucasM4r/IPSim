package com.ipsim;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.util.Duration;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

import com.ipsim.processor.neander.assembler.LexicalAnalyzer;
import com.ipsim.processor.neander.assembler.SyntacticAnalyzer;
import com.ipsim.processor.neander.assembler.LexicalAnalyzer.LexicalException;
import com.ipsim.processor.neander.assembler.SemanticAnalyzer;
import com.ipsim.processor.neander.assembler.CodeGenerator;

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
    private ToggleGroup toggleGroup = new ToggleGroup(); // Grupo de botões para permitir seleção única
    @FXML
    private HBox toggleButtonContainer;
    @FXML 
    private TextArea errorConsole;

    private List<Processor> cpusArray = new ArrayList<>();
    private Processor currentProcessor;

    public void initialize() {
        textEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers();
            scrollToEnd(); // Adiciona a chamada para rolar até o fim
        });
        updateLineNumbers(); // Inicializa os números das linhas
        scrollToEnd(); // Garante que o scroll comece no final
        cpusArray.add((Processor)new NeanderProcessor());
        generateAsmButtons();
    }


    public String getContent() {
        return textEditor.getText();
    }
    
    public void setContent(String content) {
        textEditor.setText(content);
    }

    private void updateLineNumbers() {
        String text = getContent();
        int lineCount = text.split("\n", -1).length;
        
        lineNumberFlow.getChildren().clear(); // Limpa os números das linhas antigos

        for (int i = 1; i <= lineCount; i++) {
            Label lineNumberLabel = new Label(i + ""); // Cria um novo Label para cada número de linha
            lineNumberLabel.getStyleClass().add("line-number"); // Adiciona a classe CSS

            lineNumberFlow.getChildren().add(lineNumberLabel);
        }
    }



    private void scrollToEnd() {
        Platform.runLater(() -> {
            Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(5), 
                ae -> scrollPane.setVvalue(1.0)
            ));
            timeline.play();
        });
    }


    private void generateAsmButtons() {

        for (Processor processor : cpusArray) {

            ToggleButton button = new ToggleButton(processor.getName());
            toggleButtonContainer.getChildren().add(button);
            button.setToggleGroup(toggleGroup);
            if(currentProcessor == null && !cpusArray.isEmpty()) {
                currentProcessor = cpusArray.get(0);
                button.setSelected(true);
            }
            button.setOnAction(e -> {
                currentProcessor = processor;
            });
        }
    }

    @FXML
    private void compile() {
        errorConsole.clear(); // Limpar o console de erros antes de compilar
        if (currentFile == null) {
            if (!textEditor.getText().isEmpty()) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Files", "*.asm"));
                File file = fileChooser.showSaveDialog(new Stage());
                if (file != null) {
                    try {
                        Files.write(file.toPath(), textEditor.getText().getBytes());
                        currentFile = file;
                        compileAndSaveBinary(currentFile);
                    } catch (IOException | LexicalAnalyzer.LexicalException | SyntacticAnalyzer.SyntacticException | SemanticAnalyzer.SemanticException | CodeGenerator.CodeGenerationException e) {
                        errorConsole.appendText("Compilation failed: " + e.getMessage() + "\n");
                    }
                }
            } else {
                errorConsole.appendText("No file is currently open and text editor is empty.\n");
            }
        } else {
            try {
                Files.write(Paths.get(currentFile.getAbsolutePath()), textEditor.getText().getBytes());
                compileAndSaveBinary(currentFile);

            } catch (IOException | LexicalException | SyntacticAnalyzer.SyntacticException | SemanticAnalyzer.SemanticException | CodeGenerator.CodeGenerationException e) {
                errorConsole.appendText("Compilation failed: " + e.getMessage() + "\n");
            }
        }
    }


    private void compileAndSaveBinary(File file) throws IOException, LexicalException, SyntacticAnalyzer.SyntacticException, SemanticAnalyzer.SemanticException, CodeGenerator.CodeGenerationException {
        // Ler o conteúdo do arquivo e converter para String
        String binaryCode = currentProcessor.compile(file);
        // Abrir diálogo "Salvar como" para salvar o arquivo binário
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary Files", "*.bin"));
        File binaryFile = fileChooser.showSaveDialog(new Stage());
        if (binaryFile != null) {
            Files.write(binaryFile.toPath(), binaryCode.getBytes());
        }
    }

    @FXML
    private void show() {
        // Lógica para executar o código
        try {
            createSecondStage();
            
        } catch (Exception e) {
            e.printStackTrace();
            errorConsole.appendText("Failed to create second stage: " + e.getMessage() + "\n");
        }
    }
    @FXML
    private void run() {
        // Lógica para executar o código
        try {
            currentProcessor.compile(currentFile);
            currentProcessor.executeProgram();
        } catch (Exception e) {
            e.printStackTrace();
            errorConsole.appendText("Failed to execute program: " + e.getMessage() + "\n");
        }
    }

    @FXML
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
                    return; // Se o arquivo não foi salvo, cancela a operação de criar um novo arquivo
                }
            } else if (result.get() == buttonTypeCancel) {
                return; // Cancela a operação de criar um novo arquivo
            }
        }
        textEditor.clear();
        currentFile = null; // Reseta o arquivo atual
    }

    @FXML
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
    private boolean saveAsFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Files", "*.asm"));
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                Files.write(Paths.get(file.getAbsolutePath()), textEditor.getText().getBytes());
                currentFile = file; // Atualiza o arquivo atual
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
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