package com.ipsim;

import javafx.util.Duration;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.io.File;

public class EditorController {   
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
    public void initialize() {
        textEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLineNumbers();
            scrollToEnd(); // Adiciona a chamada para rolar até o fim
        });
        updateLineNumbers(); // Inicializa os números das linhas
        scrollToEnd(); // Garante que o scroll comece no final
        generateAsmButtons(); // Gera os botões de montagem
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

    private File[] getAsmFiles() {
        File folder = new File("IPSim/src/main/java/com/ipsim/assembler/");

        if(folder.exists()) {
            File[] files = folder.listFiles();
            if(files == null) {
                throw new RuntimeException("A pasta 'assembler' está vazia.");
            }
            return files;
        }else
            throw new RuntimeException("A pasta 'assembler' não existe.");

    }
    private void generateAsmButtons() {
        File[] files = getAsmFiles();
        for(File file : files) {
            ToggleButton toggleButton = new ToggleButton(file.getName());
            toggleButton.setToggleGroup(toggleGroup);
            toggleButton.setOnAction(event -> {
                // Adicione a lógica que deseja executar quando o botão for clicado
                System.out.println("Botão " + file.getName() + " clicado");
            });
            toggleButtonContainer.getChildren().add(toggleButton); // Adiciona o ToggleButton ao contêiner
        }
    }
    @FXML
    private void compile() {
        // Lógica para compilar o código
        System.out.println("Compilando o código...");
    }

    @FXML
    private void run() {
        // Lógica para executar o código
        System.out.println("Executando o código...");
    }
}