package com.ipsim.processor.neander.assembler;

import java.util.HashMap;

public class Opcode {
    private static final HashMap<String, Integer> OPCODES = new HashMap<>();

    static {
        OPCODES.put("NOP", 0x00);
        OPCODES.put("STA", 0x01);
        OPCODES.put("LDA", 0x02);
        OPCODES.put("ADD", 0x03);
        OPCODES.put("OR", 0x04);
        OPCODES.put("AND", 0x05);
        OPCODES.put("NOT", 0x06);
        OPCODES.put("JMP", 0x08);
        OPCODES.put("JN", 0x09);
        OPCODES.put("JZ", 0x0A);
        OPCODES.put("HLT", 0x0F);
    }
    public static Integer getOpcode(String mnemonic) {
        return OPCODES.get(mnemonic);
    }
}