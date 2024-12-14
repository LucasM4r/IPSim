package com.ipsim.processor.neander2.assembler;

import com.ipsim.exceptions.CodeGenerationException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class NeanderCodeGenerator2 {

    private static final Map<String, String> INSTRUCTION_MAP = new HashMap<>();
    private Map<String, Integer> symbolTable;

    static {
        INSTRUCTION_MAP.put("NOP", "0000");
        INSTRUCTION_MAP.put("STA", "0001");
        INSTRUCTION_MAP.put("LDA", "0010");
        INSTRUCTION_MAP.put("ADD", "0011");
        INSTRUCTION_MAP.put("OR", "0100");
        INSTRUCTION_MAP.put("AND", "0101");
        INSTRUCTION_MAP.put("NOT", "0110");
        INSTRUCTION_MAP.put("JMP", "1000");
        INSTRUCTION_MAP.put("JN", "1001");
        INSTRUCTION_MAP.put("JZ", "1010");
        INSTRUCTION_MAP.put("HLT", "1111");
    }

    /**
     * The NeanderCodeGenerator constructor initializes the symbol table
     * @param symbolTable
     */
    public NeanderCodeGenerator2(Map<String, Integer> symbolTable) {
        this.symbolTable = symbolTable;
        System.out.println(symbolTable);
    }

    
    /** 
     * The generate method processes the tokens and generates the binary code
     * @param tokens
     * @return String
     * @throws CodeGenerationException
     */
    public String generate(List<NeanderLexicalAnalyzer2.Token> tokens) throws CodeGenerationException {
        StringBuilder binaryCode = new StringBuilder();
        Iterator<NeanderLexicalAnalyzer2.Token> iterator = tokens.iterator();

        while (iterator.hasNext()) {

            NeanderLexicalAnalyzer2.Token token = iterator.next();
            switch (token.getType()) {
                case INSTRUCTION:
                    String instruction = INSTRUCTION_MAP.get(token.getValue().toUpperCase());
                    binaryCode.append(extendInstruction(instruction));
                    break;
                case LABEL:
                    // Ignore labels
                    break;
                case VAR:
                    String varName = token.getValue();

                    if (symbolTable.containsKey(varName)) {

                        int value = symbolTable.get(varName);
                        String binaryValue = convertToBinary(value);
                        binaryCode.append(binaryValue);
                    } else {
                        throw new CodeGenerationException("Undefined variable: " + varName);
                    }
                    break;
                case VALUE:
                    Integer value = Integer.parseInt(token.getValue());
                    String binaryValue = convertToBinary(value);
                    binaryCode.append(binaryValue);                    
                    break;
                case PSEUDO_INSTRUCTION:
                    if(!iterator.hasNext()) {
                        break;
                    }
                    if (token.getValue().equalsIgnoreCase("org")) {
                        token = iterator.next();
                        if(token.getType() != NeanderLexicalAnalyzer2.TokenType.VALUE) {
                            break;
                        }
                        int orgPosition = Integer.parseInt(token.getValue());
                        int currentPosition = binaryCode.length() / 8;
                        int zerosToInsert = Math.max(orgPosition - currentPosition, 0);
                        for (int i = 0; i < zerosToInsert; i++) {
                            binaryCode.append("00000000"); // Add 8 zeros until the specified position
                            
                            
                        }
                    }else if(token.getValue().equalsIgnoreCase("db")) {

                        if(!iterator.hasNext()) {
                            break;
                        }
                        token = iterator.next();
                        if(token.getType() != NeanderLexicalAnalyzer2.TokenType.VALUE) {
                            break;
                        }
                        int dbValue = Integer.parseInt(token.getValue());
                        String dbBinaryValue = convertToBinary(dbValue);
                        binaryCode.append(dbBinaryValue);
                        
                    }
                    break;
                case COMMENT:
                case WHITESPACE:
                    // Ignore labels, comments, and whitespace
                    break;
                case EOF:
                    // Stop generating code after EOF
                    return binaryCode.toString();
                default:
                    throw new CodeGenerationException("Unexpected token: " + token.getValue());
            }
        }

        return binaryCode.toString();
    }
    /**
     * Extend the bits of a binary string to a specified length
     * @param binary
     * @param length
     * @param padRight
     * @return
     */
    private String extendBits(String binary, int length, boolean padRight) {
        if(!(binary.length()<length)) {
            return binary;
        }
        if (padRight) {
            return String.format("%-" + length + "s", binary).replace(' ', '0');
        }
        return String.format("%" + length + "s", binary).replace(' ', '0');
    }

    /**
     * The extendInstruction method extends the bits of an instruction to 8 bits
     * @param binary
     * @return
     */
    private String extendInstruction(String binary) {
        return extendBits(binary, 8, true);
    }

    /**
     * The convertToBinary method converts an integer value to a binary string
     * @param value
     * @return
     */
    private String convertToBinary(int value) {
        String binaryString = Integer.toBinaryString(value);
        // Ensure the binary string is 8 bits long
        return extendBits(binaryString, 8, false);
    }
}