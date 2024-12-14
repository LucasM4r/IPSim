package com.ipsim.processor.neander.assembler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import com.ipsim.processor.neander.assembler.NeanderLexicalAnalyzer.TokenType;
import com.ipsim.exceptions.SemanticException;
public class NeanderSemanticAnalyzer {

    private Map<String, Integer> symbolTable = new HashMap<>();

    /**
     * The NeanderSemanticAnalyzer constructor initializes the symbol table
     * @param symbolTable
     */
    public NeanderSemanticAnalyzer(Map<String, Integer> symbolTable) {
        this.symbolTable = symbolTable;
    }
    
    /** 
     * The analyze method processes the tokens and checks for semantic errors
     * @param tokens
     * @throws SemanticException
     */
    public void analyze(List<NeanderLexicalAnalyzer.Token> tokens) throws SemanticException {
        int address = 0;
        Iterator<NeanderLexicalAnalyzer.Token> iterator = tokens.iterator();
        
        while (iterator.hasNext()) {
            NeanderLexicalAnalyzer.Token token = iterator.next();
            // Process the token based on its type
            switch (token.getType()) {
                case LABEL:
                    String label = token.getValue().replace(":", ""); // Remove ':' from the label
                    
                    // Check if the label is already defined
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
                        throw new SemanticException("ORG directive missing address");
                    }
                    NeanderLexicalAnalyzer.Token nextToken = iterator.next();
                    if(nextToken.getType() == TokenType.VALUE) {
                        address = convertAddress(nextToken.getValue());
                    }
                    break;
                case WHITESPACE:
                case UNKNOWN:
                case EOF:
                case COMMENT:
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
