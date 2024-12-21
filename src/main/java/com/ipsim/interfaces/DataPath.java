package com.ipsim.interfaces;
import java.util.HashMap;
import java.util.List;

import com.ipsim.components.Memory;
import com.ipsim.components.Register;
public abstract class DataPath {
    protected HashMap<String, Register> registers;
    protected HashMap<String, Memory> memories;
    public DataPath() {
        registers = new HashMap<String, Register>();
        memories = new HashMap<String, Memory>();

    }

    
    /** 
     * The addRegister method adds a register to the datapath
     * @param name
     * @param register
     */
    public void addRegister(String name, Register register) {
        registers.put(name, register);
    }
    /**
     * The addMemory method adds a memory to the datapath
     * @param name
     * @param memory
     */
    public void addMemory(String name, Memory memory) {
        memories.put(name, memory);
    }

    /**
     * The storeMemory method stores a value in a memory address
     * @param name
     * @param address
     * @param value
     */
    public void storeMemory(String name, int address, int value) {
        Memory memory = memories.get(name);
        memory.write(address, value);
    }
    /**
     * The loadMemory method loads a value from a memory address
     * @param name
     * @param address
     * @return
     */
    public int loadMemory(String name, int address) {
        Memory memory = memories.get(name);
        return memory.read(address);
    }

   /**
    * The storeRegister method stores a value in a register
    * @param name
    * @param value
    */ 
    public void storeRegister(String name, int value) {
        Register register = registers.get(name);
        register.write(value);
    }

    /**
     * The loadRegister method loads a value from a register
     * @param name
     * @return
     */
    public int loadRegister(String name) {
        Register register = registers.get(name);
        return register.load();
    }

    /**
     * The getRegisters method returns the registers of the datapath
     * @return
     */
    public HashMap<String, Register> getRegisters() {
        return registers;
    }
    /**
     * The getMemories method returns the memories of the datapath
     * @return
     */
    public HashMap<String, Memory> getMemories() {
        return memories;
    }

    /**
     * The execute method executes an operation with the given arguments
     * @param operation
     * @param arguments
     */
    public abstract void execute(String operation, List<Object> arguments);
    
}