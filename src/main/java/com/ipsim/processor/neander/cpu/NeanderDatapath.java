package com.ipsim.processor.neander.cpu;

import com.ipsim.interfaces.DataPath;
import com.ipsim.components.Memory;
import com.ipsim.components.Register;

import java.util.HashMap;
import java.util.List;
public class NeanderDatapath extends DataPath {

    boolean programEnd = false;
    int programCounter = 0;

    public NeanderDatapath() {
        super();
        addMemory("dataProgram", new Memory(256, 8));
        addRegister("AC", new Register(8));
    }

    public void execute(String operation, List<Object> arguments) {
        if (!(arguments.get(0) instanceof Integer)) {
            throw new IllegalArgumentException(operation + " operation requires an integer argument");
        }
        if (arguments.size() != 1) {
            throw new IllegalArgumentException(operation + " operation requires 1 argument");
        }
        switch (operation) {
            case "STA":
                int staValue = loadRegister("AC");
                storeMemory("dataProgram", (Integer) arguments.get(0), staValue & 0xFF); // Aplica a máscara de 8 bits
                break;
            case "LDA":
                int ldaValue = loadMemory("dataProgram", (Integer) arguments.get(0));
                storeRegister("AC", ldaValue & 0xFF); // Aplica a máscara de 8 bits
                break;
            case "ADD":
                int addValue = loadRegister("AC") + loadMemory("dataProgram", (Integer) arguments.get(0));
                storeRegister("AC", addValue & 0xFF); // Aplica a máscara de 8 bits
                break;
            case "OR":
                int orValue = loadRegister("AC") | loadMemory("dataProgram", (Integer) arguments.get(0));
                storeRegister("AC", orValue & 0xFF); // Aplica a máscara de 8 bits
                break;
            case "AND":
                int andValue = loadRegister("AC") & loadMemory("dataProgram", (Integer) arguments.get(0));
                storeRegister("AC", andValue & 0xFF); // Aplica a máscara de 8 bits
                break;
            case "NOT":
                int notValue = ~loadRegister("AC") & 0xFF; // Aplica a máscara de 8 bits
                storeRegister("AC", notValue);
                break;
            case "JMP":
                programCounter = (Integer) arguments.get(0);
                break;
            case "JN":
                if (loadRegister("AC") < 0) {
                    programCounter = (Integer) arguments.get(0);
                }
                break;
            case "JZ":
                if ((loadRegister("AC")) == 0) { // Aplica a máscara de 8 bits antes de comparar
                    programCounter = (Integer) arguments.get(0);
                }
                break;
            case "HLT":
                programEnd = true;
                break;
            default:
                break;
        }
    }

    public boolean isProgramEnd() {
        return programEnd;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public Memory getMemory() {
        return memories.get("dataProgram");
    }

    public void incrementProgramCounter() {
        programCounter++;
    }

    public HashMap<String, Register> getRegisters() {
        return registers;
    }

    public void loadProgram(String binaryCode) {
        int address = 0;
        for (int i = 0; i < binaryCode.length(); i += 8) {
            if (i + 8 <= binaryCode.length() && address < memories.get("dataProgram").getSize()) {
                String byteString = binaryCode.substring(i, i + 8);
                int byteValue = Integer.parseInt(byteString, 2);
                storeMemory("dataProgram", address, byteValue);
                address++;
            } else if (address >= memories.get("dataProgram").getSize()) {
                throw new IllegalArgumentException("Memory Limit Exceeded Error:" + binaryCode.length() / 8 + " bytes");
            }
        }
    }
    
    public void init() {
        programEnd = false;
        programCounter = 0;
    }
    public void reset() {
        programEnd = false;
        programCounter = 0;
        // Reseta os registradores
        for (Register register : registers.values()) {
            register.write(0);
        }
        // Opcional: Limpa a memória se necessário
        for (int i = 0; i < memories.get("dataProgram").getSize(); i++) {
            memories.get("dataProgram").write(i, 0);
        }
    }
    
}