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

    public void addRegister(String name, Register register) {
        registers.put(name, register);
    }
    public void addMemory(String name, Memory memory) {
        memories.put(name, memory);
    }

    public void storeMemory(String name, int address, int value) {
        Memory memory = memories.get(name);
        memory.write(address, value);
    }
    public int loadMemory(String name, int address) {
        Memory memory = memories.get(name);
        return memory.read(address);
    }

    public void storeRegister(String name, int value) {
        Register register = registers.get(name);
        register.write(value);
    }
    public int loadRegister(String name) {
        Register register = registers.get(name);
        return register.load();
    }
    public HashMap<String, Register> getRegisters() {
        return registers;
    }
    public HashMap<String, Memory> getMemories() {
        return memories;
    }
    public abstract void execute(String operation, List<Object> arguments);
    
}