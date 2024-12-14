package com.ipsim.processor.neander2.assembler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.ipsim.exceptions.SemanticException;
public class NeanderSemanticAnalyzer2 {

    private Map<String, Integer> symbolTable = new HashMap<>();

    public NeanderSemanticAnalyzer2(Map<String, Integer> symbolTable) {
        this.symbolTable = symbolTable;
    }
    
    /** 
     * The analyze method processes the tokens and checks for semantic errors
     * @param tokens
     * @throws SemanticException
     */
    public void analyze(List<NeanderLexicalAnalyzer2.Token> tokens) throws SemanticException {
        int address = 0;
        Iterator<NeanderLexicalAnalyzer2.Token> iterator = tokens.iterator();
        
        while (iterator.hasNext()) {
            NeanderLexicalAnalyzer2.Token token = iterator.next();
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
                case PSEUDO_INSTRUCTION:
                    if(!token.getValue().equalsIgnoreCase("ORG")) {
                        break;
                    }
                    if(!iterator.hasNext()) {
                        throw new SemanticException("Invalid ORG directive");
                    }
                    NeanderLexicalAnalyzer2.Token nextToken = iterator.next();
                    if(nextToken.getType() == NeanderLexicalAnalyzer2.TokenType.VALUE) {
                        address = convertAddress(nextToken.getValue());
                    } else {
                        throw new SemanticException("Invalid ORG directive");
                    }
                    break;
                case WHITESPACE:
                case UNKNOWN:
                case EOF:
                case COMMENT:
                    // No action needed for these token types
                    break;
            }
        }
    }

    /**
     * Get the symbol table
     * @return
     */
    public Map<String, Integer> getSymbolTable() {
        return symbolTable;
    }
    /**
     * Convert a value to binary
     * @param value
     * @return
     */
    public String convertToBinary(String value) {
        if (value.toLowerCase().endsWith("h")) {
            String hexValue = value.substring(0, value.length() - 1);
            int decimalValue = Integer.parseInt(hexValue, 16);
            return Integer.toBinaryString(decimalValue);
        }
        int decimalValue = Integer.parseInt(value);
        return Integer.toBinaryString(decimalValue);
    }
    /**
     * Convert a value to decimal
     * @param value
     * @return
     */
    public int convertToDecimal(String value) {
        if (value.toLowerCase().endsWith("h")) {
            String hexValue = value.substring(0, value.length() - 1);
            return Integer.parseInt(hexValue, 16);
        }
        return Integer.parseInt(value);
    }

    /**
     * Convert an address to decimal
     * @param value
     * @return
     */
    public Integer convertAddress(String value) {
        if (value.toLowerCase().endsWith("h")) {
            String hexValue = value.substring(0, value.length() - 1);
            return Integer.parseInt(hexValue, 16);
        }
        return Integer.parseInt(value);
    }
}
