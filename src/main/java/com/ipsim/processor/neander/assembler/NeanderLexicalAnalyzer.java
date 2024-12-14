package com.ipsim.processor.neander.assembler;

import java.util.ArrayList;
import java.util.List;

import com.ipsim.exceptions.LexicalException;
public class NeanderLexicalAnalyzer {
    private static final String[] PSEUDO_INSTRUCTIONS = {
        "org", "db"
    };
    private static final String[] INSTRUCTIONS = {
        "NOP", "STA", "LDA", "ADD", "OR", "AND", "NOT", "JMP", "JN", "JZ", "HLT"
    };
    public enum TokenType {
        INSTRUCTION, 
        VALUE, 
        LABEL, 
        COMMENT, 
        WHITESPACE, 
        UNKNOWN, 
        PSEUDO_INSTRUCTION, 
        VAR, 
        EOF
    }

    /**
     * The Token class represents a token with a type and a value
     */
    public static class Token {
        private TokenType type;
        private String value;

        /**
         * The Token constructor initializes the token with a type and a value
         * @param type
         * @param value
         */
        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        /**
         * The getType method returns the type of the token
         * @return
         */
        public TokenType getType() {
            return type;
        }
        /**
         * The getValue method returns the value of the token
         * @return
         */
        public String getValue() {
            return value;
        }

        @Override
        /**
         * The toString method returns a string representation of the token
         */
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
    
    /** 
     * The analyze method tokenizes the input string and returns a list of tokens
     * @param input
     * @return List<Token>
     */
    public List<Token> analyze(String input) throws LexicalException {
        List<Token> tokens = new ArrayList<>();
        String[] lines = input.split("\n");

        // Process each line
        for (String line : lines) {
            String[] parts = line.split("\\s+");
            // Process each part of the line
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part.trim().isEmpty()) {
                    continue; // Ignore empty parts
                }
                if (isInstruction(part)) {
                    // Check if the part is an instruction
                    tokens.add(new Token(TokenType.INSTRUCTION, part));
                } else if (isPseudoInstruction(part)) {
                    // Check if the part is a pseudo-instruction
                    tokens.add(new Token(TokenType.PSEUDO_INSTRUCTION, part));
                } else if (isValue(part)) {
                    // Check if the part is a value
                    tokens.add(new Token(TokenType.VALUE, part));
                } else if (isLabelVar(part)) {
                    // Check the next token to determine if it's a label or a variable
                    tokens.add(new Token(TokenType.LABEL, part));
                } else if (isComment(part)) {
                    // Check if the part is a comment
                    tokens.add(new Token(TokenType.COMMENT, part));
                    break; // Ignore the rest of the line after a comment
                } else if(isVar(part)) {
                    // Check if the part is a variable
                    tokens.add(new Token(TokenType.VAR, part));
                }else {
                    throw new LexicalException("Unknown token: " + part);
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }
    /**
     * The isInstruction method checks if a given string is an instruction
     * @param part
     * @return
     */
    private boolean isInstruction(String part) {
        for (String instruction : INSTRUCTIONS) {
            if (instruction.equalsIgnoreCase(part)) {
                return true;
            }
        }
        return false;
    }
    /**
     * The isPseudoInstruction method checks if a given string is a pseudo-instruction
     * @param part
     * @return
     */
    private boolean isPseudoInstruction(String part) {
        for (String pseudoInstruction : PSEUDO_INSTRUCTIONS) {
            if (pseudoInstruction.equalsIgnoreCase(part)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The isValue method checks if a given string is a value
     * @param part
     * @return
     */
    private boolean isValue(String part) {
        return part.matches("\\d+h?") || part.matches("[0-9a-fA-F]+h");
    }
    /**
     * The isLabelVar method checks if a given string is a label or a variable
     * @param part
     * @return
     */
    private boolean isLabelVar(String part) {
        return part.matches("[a-zA-Z_][a-zA-Z0-9_]*:");
    }
    /**
     * The isComment method checks if a given string is a comment
     * @param part
     * @return
     */
    private boolean isComment(String part) {
        return part.startsWith(";");
    }
    /**
     * The isVar method checks if a given string is a variable
     * @param part
     * @return
     */
    private boolean isVar(String part) {
        return part.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }
}