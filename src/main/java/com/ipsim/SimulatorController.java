/**
 * The SimulatorController class is used to control the simulation tab.
 * @author Lucas Marchesan da Silva
 * @version 1.0
 */
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
import java.util.Map;
import java.util.ArrayList;
public class SimulatorController {

    private static Stage stage;
    private static Processor currentProcessor;
    private static HashMap<String, TextField> registerFields = new HashMap<>();
    private static List<TextField> memoryFields = new ArrayList<>();

    /**
     * The method setStage() is used to set the stage of the simulation tab.
     * @param stage
     */
    public static void setStage(Stage stage) {
        SimulatorController.stage = stage;
    }
    /**
     * The setCurrentProcessor() method is used to set the current processor.
     * @param processor
     */
    public static void setCurrentProcessor(Processor processor) {
        SimulatorController.currentProcessor = processor;
    }

    @FXML
    /**
     * The method showRegisters() is used to display the 
     * processor registers when modifying the scene.
     * @throws IOException
     */
    public void showRegisters() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ipsim/registers_page.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/ipsim/styles/Styles.css").toExternalForm());
            HashMap<String, Register> registers = currentProcessor.getDatapath().getRegisters();

            // Clear the register map
            registerFields.clear();

            // Add the registers to the layout
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
                    // Iterate over the registers and add them to the layout
                    for (HashMap.Entry<String, Register> entry : registers.entrySet()) {
                        Label registerLabel = new Label(entry.getKey());
                        registerLabel.getStyleClass().add("register-label");
                        gridPane.add(registerLabel, 0, row);

                        TextField valueField = new TextField("0x" + entry.getValue().getHexValue());
                        valueField.getStyleClass().add("value-field");
                        gridPane.add(valueField, 1, row);

                        // Update the register map
                        registerFields.put(entry.getKey(), valueField);

                        row++;
                    }
                }
            }
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    /**
     * The method saveRegisters() is used to save 
     * the values of the registers in the processor.
     * no processador.
     * @throws NumberFormatException
     */
    public void saveRegisters() {
        if (registerFields == null || registerFields.isEmpty()) {
            throw new NullPointerException("Register fields not found");
        }

        // Get the registers from the processor
        HashMap<String, Register> registers = currentProcessor.getDatapath().getRegisters();
        // Iterate over the register fields and update the register values        
        for (HashMap.Entry<String, TextField> entry : registerFields.entrySet()) {
            String registerName = entry.getKey();
            TextField valueField = entry.getValue();
            String value = valueField.getText();
            try {
                int registerValue;
                if (value.endsWith("0x")) {
                    // Hexadecimal value
                    value = value.substring(2);
                    registerValue = Integer.parseInt(value, 16);
                } else {
                    // Decimal value
                    registerValue = Integer.parseInt(value);
                }
                registers.get(registerName).write(registerValue);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Invalid value for register");
            }
        }
    }
    @FXML
    /**
     * The method goBack() is used to return to the previous scene.
     * 
     */
    public void goBack() {
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
    /**
     * The method showMemories() is used to display the memory list.
     * @throws IOException
     */
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
                    // Add the memories to the layout
                    Label memoriesHeader = new Label("Memory");
                    memoriesHeader.getStyleClass().add("memories-label");
                    gridPane.add(memoriesHeader, 0, 0);

                    Label valueHeader = new Label(" ");
                    valueHeader.getStyleClass().add("value-label");
                    gridPane.add(valueHeader, 1, 0);

                    int row = 1;
                    // Iterate over the memories and add them to the layout
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
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * The method showMemory() is used to display the selected memory.
     * @param memoryName
     */
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
                    // Add the memory to the layout
                    Label memoryHeader = new Label("Address");
                    memoryHeader.getStyleClass().add("address-label");
                    gridPane.add(memoryHeader, 0, 0);
                    Label valueHeader = new Label("Value");
                    valueHeader.getStyleClass().add("value-label");
                    gridPane.add(valueHeader, 1, 0);
                    int row = 1;
                    // Iterate over the memory and add the values to the layout
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
                }
            }
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    /**
     * The method saveMemory() is used to save the memory values.
     * @throws NumberFormatException
     */
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
                    // Hexadecimal value
                    value = value.substring(2);
                    memoryValue = Integer.parseInt(value, 16);
                } else {
                    // Decimal value
                    memoryValue = Integer.parseInt(value);
                }
                int index = memoryFields.indexOf(valueField);
                if (index >= 0 && index < memory.getSize()) {
                    // verify if the index is valid and write the value
                    memory.write(index, memoryValue);
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Invalid value for memory");
            }
        }
    }
    @FXML
    public static void updateUI() {

        HashMap<String, Register> registers = currentProcessor.getDatapath().getRegisters();
        for (HashMap.Entry<String, Register> entry : registers.entrySet()) {
            TextField valueField = registerFields.get(entry.getKey());
            if (valueField != null) {
                valueField.setText("0x" + entry.getValue().getHexValue());
            }
        }

        Map<String, Memory> memories = currentProcessor.getDatapath().getMemories();
        for(Memory memory : memories.values()) {
            for (int i = 0; i < memory.getSize(); i++) {
                TextField valueField = memoryFields.get(i);
                if (valueField != null) {
                    valueField.setText("0x" + Integer.toHexString(memory.read(i)));
                }
            }
        }
    }
}