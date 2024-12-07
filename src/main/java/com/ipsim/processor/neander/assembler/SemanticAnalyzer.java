package com.ipsim.processor.neander.assembler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.ipsim.processor.neander.assembler.LexicalAnalyzer.TokenType;

public class SemanticAnalyzer {

    private Map<String, Integer> symbolTable = new HashMap<>();

    public SemanticAnalyzer(Map<String, Integer> symbolTable) {
        this.symbolTable = symbolTable;
    }
    public void analyze(List<LexicalAnalyzer.Token> tokens) throws SemanticException {
        int address = 0;
        Iterator<LexicalAnalyzer.Token> iterator = tokens.iterator();
        
        while (iterator.hasNext()) {
            LexicalAnalyzer.Token token = iterator.next();
            // Process the token here
            switch (token.getType()) {
                case LABEL:
                    String label = token.getValue().replace(":", ""); // Remove ':' from the label
                    
                    if (!symbolTable.containsKey(label)) {
                        throw new SemanticException("Label is not defined: " + label);
                    }
                    symbolTable.put(label, address);                    
                    break;
                case INSTRUCTION:
                    address++;
                    break;
                case VAR:
                    address++;
                    break;
                case VALUE:
                    address++;
                    break;
                case WHITESPACE:
                case UNKNOWN:
                case EOF:
                case PSEUDO_INSTRUCTION:
                    if (token.getValue().equalsIgnoreCase("ORG")) {
                        if (iterator.hasNext()) {
                            LexicalAnalyzer.Token nextToken = iterator.next();
                            if (nextToken.getType() == TokenType.VALUE) {
                            } else {
                                throw new SemanticException("Invalid ORG directive");
                            }
                        } else {
                            throw new SemanticException("Invalid ORG directive");
                        }
                        
                    }
                case COMMENT:
                    // No action needed for these token types
                    break;
            }
        }
    }

    public Map<String, Integer> getSymbolTable() {
        return symbolTable;
    }

    public String convertToBinary(String value) {
        if (value.toLowerCase().endsWith("h")) {
            String hexValue = value.substring(0, value.length() - 1);
            int decimalValue = Integer.parseInt(hexValue, 16);
            return Integer.toBinaryString(decimalValue);
        }
        int decimalValue = Integer.parseInt(value);
        return Integer.toBinaryString(decimalValue);
    }

    public int convertToDecimal(String value) {
        if (value.toLowerCase().endsWith("h")) {
            String hexValue = value.substring(0, value.length() - 1);
            return Integer.parseInt(hexValue, 16);
        }
        return Integer.parseInt(value);
    }

    public Integer convertAddress(String value) {
        if (value.toLowerCase().endsWith("h")) {
            String hexValue = value.substring(0, value.length() - 1);
            return Integer.parseInt(hexValue, 16);
        }
        return Integer.parseInt(value);
    }

    public static class SemanticException extends Exception {
        public SemanticException(String message) {
            super(message);
        }
    }
}
