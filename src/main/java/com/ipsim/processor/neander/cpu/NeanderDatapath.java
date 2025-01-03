package com.ipsim.processor.neander.cpu;

import java.util.HashMap;
import java.util.List;

import com.ipsim.components.Memory;
import com.ipsim.components.Register;
import com.ipsim.interfaces.DataPath;
public class NeanderDatapath extends DataPath {

    boolean programEnd = false;
    int programCounter = 0;

    /**
     * The constructor initializes the datapath
     */
    public NeanderDatapath() {
        super();
        addMemory("dataProgram", new Memory(256, 8));
        addRegister("AC", new Register(8));
        addRegister("Z", new Register(1));
        addRegister("N", new Register(1));
    }

    
    /** 
     * The execute method executes an operation in the datapath.
     * @param operation
     * @param arguments
     */
    @Override
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
                storeRegister("AC", ldaValue & 0xFF); // Apply the 8-bit mask
                updateFlags();
                break;
            case "ADD":
                int addValue = loadRegister("AC") + loadMemory("dataProgram", (Integer) arguments.get(0));
                storeRegister("AC", addValue & 0xFF); // Apply the 8-bit mask
                updateFlags();
                break;
            case "OR":
                int orValue = loadRegister("AC") | loadMemory("dataProgram", (Integer) arguments.get(0));
                storeRegister("AC", orValue & 0xFF);  // Apply the 8-bit mask
                updateFlags();
                break;
            case "AND":
                int andValue = loadRegister("AC") & loadMemory("dataProgram", (Integer) arguments.get(0));
                storeRegister("AC", andValue & 0xFF); // Apply the 8-bit mask
                updateFlags();
                break;
            case "NOT":
                int notValue = ~loadRegister("AC") & 0xFF;  // Apply the 8-bit mask
                storeRegister("AC", notValue);
                updateFlags();
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
                if ((loadRegister("AC")) == 0) {
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
    /**
     * The isProgramEnd method checks if the program has ended
     * @return boolean
     */
    public boolean isProgramEnd() {
        return programEnd;
    }
    /**
     * The method getProgramCounter() returns the program counter.
     * @return boolean
     */ 
    public int getProgramCounter() {
        return programCounter;
    }
    /**
     * The getMemory method returns the datapath data memory.
     * @return Memory
     */
    public Memory getMemory() {
        return memories.get("dataProgram");
    }
    /**
     * The incrementProgramCounter method increments the program counter.
     */
    public void incrementProgramCounter() {
        programCounter++;
    }
    /**
     * The getRegisters() method returns the datapath registers.
     * @return HashMap<String, Register>
     */
    public HashMap<String, Register> getRegisters() {
        return registers;
    }

    /**
     * The loadProgram method loads a program into the datapath.
     * @param binaryCode
     * @throws IllegalArgumentException
     */
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
    /**
     * The init method initializes the datapath.
     */
    public void init() {
        programEnd = false;
        programCounter = 0;
    }
    /**
     * The reset method resets the datapath.
     */
    public void reset() {
        programEnd = false;
        programCounter = 0;
        // Reset Registers
        for (Register register : registers.values()) {
            register.write(0);
        }
        // Reset Memory
        for (int i = 0; i < memories.get("dataProgram").getSize(); i++) {
            memories.get("dataProgram").write(i, 0);
        }
    }
    /** 
     * The updateFlags method updates the flags in the datapath.
    */
    public void updateFlags() {
        int acValue = loadRegister("AC");
        storeRegister("Z", (acValue == 0) ? 1 : 0);

        int bitMask = 1 << (registers.get("AC").getNumBits() - 1);
        storeRegister("N", (acValue & bitMask) != 0 ? 1 : 0);
    }
}