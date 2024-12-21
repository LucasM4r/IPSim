package com.ipsim.processor.neander.cpu;

import com.ipsim.interfaces.ControlPath;

public class NeanderControlpath implements ControlPath {
    
    public NeanderControlpath() {
    }

    
    /** 
     * O método returnOperation() recebe uma instrução de 4 bits 
     * e retorna a operação correspondente.
     * @param instruction
     * @return String
     */
    public String returnOperation(String instruction) {
        switch (instruction) {
            case "0001":
                return "STA";
            case "0010":
                return "LDA";
            case "0011":
                return "ADD";
            case "0100":
                return "OR";
            case "0101":
                return "AND";
            case "0110":
                return "NOT";
            case "1000":
                return "JMP";
            case "1001":
                return "JN";
            case "1010":
                return "JZ";
            case "1111":
                return "HLT";
            default:
                return "NOP";
        }
    }
}