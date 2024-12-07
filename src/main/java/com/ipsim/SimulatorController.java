package com.ipsim;

import com.ipsim.components.Register;
import com.ipsim.components.Memory;

import com.ipsim.interfaces.Processor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;
public class SimulatorController {

    private static Stage stage;
    private static Processor currentProcessor;
    private static HashMap<String, TextField> registerFields = new HashMap<>();
    private static List<TextField> memoryFields = new ArrayList<>();

    public void initialize(Processor currentProcessor, Stage stage) {
        SimulatorController.currentProcessor = currentProcessor;
        SimulatorController.stage = stage;
    }

    public static void setStage(Stage stage) {
        SimulatorController.stage = stage;
    }

    public static void setCurrentProcessor(Processor processor) {
        SimulatorController.currentProcessor = processor;
    }

    @FXML
    public void showRegisters() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ipsim/registers_page.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/ipsim/styles/Styles.css").toExternalForm());

            // Obtém os registradores do currentProcessor
            HashMap<String, Register> registers = currentProcessor.getDatapath().getRegisters();

            // Limpa o mapa de registradores
            registerFields.clear();

            // Adiciona os registradores ao layout
            VBox vbox = (VBox) root.lookup("#registersContainerVBox");
            if (vbox != null) {
                GridPane gridPane = (GridPane) vbox.lookup("#registersContainer");
                if (gridPane != null) {
                    gridPane.getChildren().clear();

                    Label registerHeader = new Label("Register");
                    registerHeader.getStyleClass().add("register-label");
                    gridPane.add(registerHeader, 0, 0);

                    Label valueHeader = new Label("Value");
                    valueHeader.getStyleClass().add("value-label");
                    gridPane.add(valueHeader, 1, 0);

                    int row = 1;
                    for (HashMap.Entry<String, Register> entry : registers.entrySet()) {
                        Label registerLabel = new Label(entry.getKey());
                        registerLabel.getStyleClass().add("register-label");
                        gridPane.add(registerLabel, 0, row);

                        TextField valueField = new TextField("0x" + entry.getValue().getHexValue());
                        valueField.getStyleClass().add("value-field");
                        gridPane.add(valueField, 1, row);

                        // Atualiza o mapa de registradores
                        registerFields.put(entry.getKey(), valueField);

                        row++;
                    }
                } else {
                    throw new NullPointerException("GridPane 'registersContainer' not found");
                }
            } else {
                throw new NullPointerException("VBox 'registersContainerVBox' not found");
            }

            stage.setScene(scene); // Define a nova cena
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void saveRegisters() {
        if (registerFields == null || registerFields.isEmpty()) {
            throw new NullPointerException("Register fields not found");
        }

        HashMap<String, Register> registers = currentProcessor.getDatapath().getRegisters();
        for (HashMap.Entry<String, TextField> entry : registerFields.entrySet()) {
            String registerName = entry.getKey();
            TextField valueField = entry.getValue();
            String value = valueField.getText();
            try {
                int registerValue;
                if (value.startsWith("0x")) {
                    // Valor em hexadecimal
                    value = value.substring(2);
                    registerValue = Integer.parseInt(value, 16);
                } else {
                    // Valor em decimal
                    registerValue = Integer.parseInt(value);
                }
                registers.get(registerName).write(registerValue);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Invalid value for register");
            }
        }
    }
    @FXML
    public void goBack() {
        if (stage == null) {
            throw new NullPointerException("Stage is null");
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ipsim/simulator.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/ipsim/styles/Styles.css").toExternalForm());

            stage.setScene(scene); // Define a nova cena
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showMemories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ipsim/memories_page.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/ipsim/styles/Styles.css").toExternalForm());
            HashMap<String, Memory> memories = currentProcessor.getDatapath().getMemories();

            VBox vbox = (VBox) root.lookup("#memoriesContainerVBox");
            if(vbox != null) {
                GridPane gridPane = (GridPane) vbox.lookup("#memoriesContainer");
                if(gridPane != null) {
                    gridPane.getChildren().clear();

                    Label memoriesHeader = new Label("Memory");
                    memoriesHeader.getStyleClass().add("memories-label");
                    gridPane.add(memoriesHeader, 0, 0);

                    Label valueHeader = new Label(" ");
                    valueHeader.getStyleClass().add("value-label");
                    gridPane.add(valueHeader, 1, 0);

                    int row = 1;
                    for(HashMap.Entry<String, Memory> entry : memories.entrySet()) {
                        Label memoryLabel = new Label(entry.getKey());
                        memoryLabel.getStyleClass().add("memory-label");

                        gridPane.add(memoryLabel, 0, row);

                        Button memoryButton = new Button("Show");
                        memoryButton.getStyleClass().add("memory-button");
                        memoryButton.setOnAction(e -> {
                            showMemory(entry.getKey());
                        });
                        gridPane.add(memoryButton, 1, row);
                        row++;
                    }
                }
            }
            stage.setScene(scene); // Define a nova cena
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private void showMemory(String memoryName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ipsim/memory_page.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/ipsim/styles/Styles.css").toExternalForm());
            Memory memory = currentProcessor.getDatapath().getMemories().get(memoryName);
            memoryFields.clear();
            VBox vbox = (VBox) root.lookup("#memoryContainerVBox");
            ScrollPane scrollPane = (ScrollPane) vbox.lookup("#memoryScrollPane");
            if (scrollPane != null) {
                GridPane gridPane = (GridPane) scrollPane.getContent();
                if (gridPane != null) {
                    gridPane.getChildren().clear();
                    Label memoryHeader = new Label("Address");
                    memoryHeader.getStyleClass().add("address-label");
                    gridPane.add(memoryHeader, 0, 0);
                    Label valueHeader = new Label("Value");
                    valueHeader.getStyleClass().add("value-label");
                    gridPane.add(valueHeader, 1, 0);
                    int row = 1;
                    for (int i = 0; i < memory.getSize(); i++) {
                        Label addressLabel = new Label("0x" + Integer.toHexString(i));
                        addressLabel.getStyleClass().add("address-label");
                        gridPane.add(addressLabel, 0, row);
                        TextField valueField = new TextField("0x" + Integer.toHexString(memory.read(i)));
                        valueField.getStyleClass().add("value-field");
                        gridPane.add(valueField, 1, row);
                        memoryFields.add(valueField);
                        row++;
                    }
                } else {
                    throw new NullPointerException("GridPane 'memoryContainer' not found");
                }
            } else {
                throw new NullPointerException("ScrollPane 'memoryScrollPane' not found");
            }
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveMemory() {
        Memory memory = currentProcessor.getDatapath().getMemories().get("dataProgram");
        if (memory == null) {
            throw new NullPointerException("Memory not found");
        }
        
        for (TextField valueField : memoryFields) {
            String value = valueField.getText();
            try {
                int memoryValue;
                if (value.startsWith("0x")) {
                    // Valor em hexadecimal
                    value = value.substring(2);
                    memoryValue = Integer.parseInt(value, 16);
                } else {
                    // Valor em decimal
                    memoryValue = Integer.parseInt(value);
                }
                int index = memoryFields.indexOf(valueField);
                if (index >= 0 && index < memory.getSize()) { // Verifique se o índice está dentro do intervalo
                    memory.write(index, memoryValue);
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Invalid value for memory");
            }
        }
    }
}