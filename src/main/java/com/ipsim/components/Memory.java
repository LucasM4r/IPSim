package com.ipsim.components;
import java.util.ArrayList;
import java.util.List;


public class Memory {

    private List<Integer> memory;
    private int size;
    private int wordSize;

    public Memory(int size, int wordSize) {
        memory = new ArrayList<>(size);
        this.size = size;
        this.wordSize = wordSize;

        for (int i = 0; i < size; i++) {
            memory.add(0);
        }
    }

    
    /** 
     * @param address
     * @param value
     */
    public void write(int address, int value) {
        if (address < 0 || address >= size) {
            throw new IllegalArgumentException("Endereço fora do intervalo da memória.");
        }

        Integer maxValue = (1 << wordSize) - 1;
        if (value < 0 || value > maxValue) {
            throw new IllegalArgumentException("Valor fora do intervalo permitido para " + wordSize + " bits.");
        }

        memory.set(address, value);
    }

    public int read(int address) {
        if (address < 0 || address >= size) {
            throw new IllegalArgumentException("Endereço fora do intervalo da memória.");
        }
        return memory.get(address);
    }
    public int getSize() {
        return size;
    }
    public int getWordSize() {
        return wordSize;
    }
    public List<Integer> getMemory() {
        return memory;
    }
    public void setMemory(List<Integer> memory) {
        this.memory = memory;
    }
    public List<String> getHexValue() {
        List<String> hexValue = new ArrayList<>();
        for (Integer value : memory) {
            hexValue.add(Integer.toHexString(value));
        }
        return hexValue;
    }
}
