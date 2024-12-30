package com.ipsim.components;
public class Register {

    private int value;
    private int numBits;

    public Register(int numBits) {
        this.numBits = numBits;
        this.value = 0;
    }

    
    /** 
     * @param value
     */
    public void write(int value) {

        checkValue(value);
        this.value = value;
        
    }
    public int load() {
        return value;
    }
    private void checkValue(int value) {
        int maxValue = (1 << numBits) - 1;
        if (value < 0 || value > maxValue) {
            throw new IllegalArgumentException("Valor fora do intervalo para " + numBits + " bits.");
        }
    }

    public int getValue() {
        return value;
    }
    public String getHexValue() {
        return Integer.toHexString(value);
    }
    public int getNumBits() {
        return numBits;
    }
}