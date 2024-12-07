package com.ipsim.components;
public abstract class Mux {

    public static long select(long[] inputs, int selector) {

        if (selector < 0 || selector >= inputs.length) {
            throw new IllegalArgumentException("Seletor fora do intervalo.");
        }
        return inputs[selector];
    }
}
