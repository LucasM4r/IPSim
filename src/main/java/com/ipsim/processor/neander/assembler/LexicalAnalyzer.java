package com.ipsim.processor.neander.assembler;

import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {
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

    public static class Token {
        private TokenType type;
        private String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        public TokenType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
    public List<Token> analyze(String input) {
        List<Token> tokens = new ArrayList<>();
        String[] lines = input.split("\n");

        for (String line : lines) {
            String[] parts = line.split("\\s+");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part.trim().isEmpty()) {
                    continue; // Ignore empty parts
                }
                if (isInstruction(part)) {
                    tokens.add(new Token(TokenType.INSTRUCTION, part));
                } else if (isPseudoInstruction(part)) {
                    tokens.add(new Token(TokenType.PSEUDO_INSTRUCTION, part));
                } else if (isValue(part)) {
                    tokens.add(new Token(TokenType.VALUE, part));
                } else if (isLabelVar(part)) {
                    // Check the next token to determine if it's a label or a variable
                    tokens.add(new Token(TokenType.LABEL, part));
                } else if (isComment(part)) {
                    tokens.add(new Token(TokenType.COMMENT, part));
                    break; // Ignore the rest of the line after a comment
                } else if(isVar(part)) {
                    tokens.add(new Token(TokenType.VAR, part));
                }else {
                    throw new LexicalException("Unknown token: " + part);
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private boolean isInstruction(String part) {
        for (String instruction : INSTRUCTIONS) {
            if (instruction.equalsIgnoreCase(part)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPseudoInstruction(String part) {
        for (String pseudoInstruction : PSEUDO_INSTRUCTIONS) {
            if (pseudoInstruction.equalsIgnoreCase(part)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValue(String part) {
        return part.matches("\\d+h?") || part.matches("[0-9a-fA-F]+h");
    }
    private boolean isLabelVar(String part) {
        return part.matches("[a-zA-Z_][a-zA-Z0-9_]*:");
    }
    private boolean isComment(String part) {
        return part.startsWith(";");
    }
    private boolean isVar(String part) {
        return part.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }
    public static class LexicalException extends RuntimeException {
        public LexicalException(String message) {
            super(message);
        }
    }
}