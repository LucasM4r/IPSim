package com.ipsim.processor.neander.assembler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CodeGenerator {

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

    public CodeGenerator(Map<String, Integer> symbolTable) {
        this.symbolTable = symbolTable;
        System.out.println(symbolTable);
    }

    public String generate(List<LexicalAnalyzer.Token> tokens) throws CodeGenerationException {
        StringBuilder binaryCode = new StringBuilder();
        Iterator<LexicalAnalyzer.Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            LexicalAnalyzer.Token token = iterator.next();
            System.out.println("Depuração"); // Depuração
            System.out.println("----------------------------------------"); // Depuração
            System.out.println("Tokens: " + token); // Depuração
            System.out.println(symbolTable);
            System.out.println("----------------------------------------"); // Depuração

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
                    System.out.println("Depuração"); // Depuração
                    System.out.println("VarName: " + varName); // Depuração
                    if (symbolTable.containsKey(varName)) {
                        System.out.println("----------------------------------------");
                        System.out.println(symbolTable.containsKey(varName));
                        int value = symbolTable.get(varName);
                        System.out.println("name " + varName +"Valor: " + value); // Depuração
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
                    if (iterator.hasNext()) {
                        if (token.getValue().equalsIgnoreCase("org")) {
                            token = iterator.next();
                            System.out.println("Depuração"); // Depuração
                            System.out.println("Token: " + token.getValue()); // Depuração
                            if (token.getType() == LexicalAnalyzer.TokenType.VALUE) {
                                int orgPosition = Integer.parseInt(token.getValue());
                                int currentPosition = binaryCode.length() / 8; // Cada instrução ocupa 8 bits
                                int zerosToInsert = orgPosition - currentPosition;
                                if (zerosToInsert > 0) {
                                    for (int i = 0; i < zerosToInsert; i++) {
                                        binaryCode.append("00000000"); // Adiciona 8 zeros para cada posição
                                        System.out.println("Adicionando 8 zeros para a posição " + (currentPosition + i)); // Depuração
                                    }
                                }
                            }
                        }else if(token.getValue().equalsIgnoreCase("db")) {

                            if(iterator.hasNext()) {
                                token = iterator.next();
                                if(token.getType() == LexicalAnalyzer.TokenType.VALUE) {
                                    int dbValue = Integer.parseInt(token.getValue());
                                    String dbBinaryValue = convertToBinary(dbValue);
                                    binaryCode.append(dbBinaryValue);
                                }
                            }
                        }
                        break;
                    }
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

    public static class CodeGenerationException extends Exception {
        public CodeGenerationException(String message) {
            super(message);
        }
    }

    private String extendBits(String binary, int length, boolean padRight) {
        if (binary.length() < length) {
            if (padRight) {
                return String.format("%-" + length + "s", binary).replace(' ', '0');
            } else {
                return String.format("%" + length + "s", binary).replace(' ', '0');
            }
        } else {
            return binary;
        }
    }

    private String extendInstruction(String binary) {
        return extendBits(binary, 8, true);
    }

    private String convertToBinary(int value) {
        String binaryString = Integer.toBinaryString(value);
        // Ensure the binary string is 8 bits long
        return extendBits(binaryString, 8, false);
    }
}