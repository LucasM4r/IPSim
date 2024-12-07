package com.ipsim.processor.neander.assembler;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

public class SyntacticAnalyzer {
    public HashMap<String, Integer> symbolTable = new HashMap<String, Integer>();
    public void analyze(List<LexicalAnalyzer.Token> tokens) throws SyntacticException {
        Iterator<LexicalAnalyzer.Token> iterator = tokens.iterator();
        
        while (iterator.hasNext()) {
            LexicalAnalyzer.Token token = iterator.next();
            System.out.println("Token: " + token.getType() + " Value: " + token.getValue()); // Debug
            switch (token.getType()) {
                case INSTRUCTION:
                    processInstruction(token, iterator);
                    break;
                case PSEUDO_INSTRUCTION:
                    processPseudoInstruction(token, iterator);
                    break;
                case LABEL:
                    
                    // Labels can be standalone or followed by an instruction or pseudo-instruction
                    processLabel(token, iterator);
                    break;
                case VAR:
                    processVar(token, iterator);
                    break;
                case VALUE:
                    processValue(token, iterator);
                    break;
                case COMMENT:
                    // Ignore comments and whitespace
                    break;
                case EOF:
                    // Stop analyzing after EOF
                    return;
                default:
                    throw new SyntacticException("Unexpected token: " + token.getValue());
            }
        }
    }

    private void processInstruction(LexicalAnalyzer.Token token, Iterator<LexicalAnalyzer.Token> iterator) throws SyntacticException {

        if (token.getValue().equalsIgnoreCase("NOP") || 
                   token.getValue().equalsIgnoreCase("NOT") ||
            token.getValue().equalsIgnoreCase("HLT")) {
            // NOP and NOT do not require a value or label after them
            return;
        } 
        if (!iterator.hasNext() || !isValueOrVar(iterator.next())) {
            throw new SyntacticException("Expected value or var after instruction: " + token.getValue());
        }
    }

    private void processPseudoInstruction(LexicalAnalyzer.Token token, Iterator<LexicalAnalyzer.Token> iterator) throws SyntacticException {
        if (!iterator.hasNext() || !isValue(iterator.next())) {
            throw new SyntacticException("Expected value after pseudo-instruction: " + token.getValue());
        }
    }

    private void processVar(LexicalAnalyzer.Token token, Iterator<LexicalAnalyzer.Token> iterator) throws SyntacticException {
        if (iterator.hasNext()) {
            LexicalAnalyzer.Token nextToken = iterator.next();
            if (nextToken.getType() != LexicalAnalyzer.TokenType.PSEUDO_INSTRUCTION) {
                throw new SyntacticException("Expected Pseudo-instruction after var: " + token.getValue());
            }
            // Process the value associated with the variable
            processValue(nextToken, iterator);
        }
    }

    private void processLabel(LexicalAnalyzer.Token token, Iterator<LexicalAnalyzer.Token> iterator) throws SyntacticException {
        String symbol = token.getValue().replace(":", "");
        System.out.println("Colocando na tabela de simbolos: " + symbol);
        symbolTable.put(symbol, 0);
        if (iterator.hasNext()) {
            LexicalAnalyzer.Token nextToken = iterator.next();
            if (nextToken.getType() == LexicalAnalyzer.TokenType.INSTRUCTION) {
                processInstruction(nextToken, iterator);
            } else if (nextToken.getType() == LexicalAnalyzer.TokenType.PSEUDO_INSTRUCTION) {
                processPseudoInstruction(nextToken, iterator);
            } else if (nextToken.getType() == LexicalAnalyzer.TokenType.VALUE) {
                processValue(nextToken, iterator);
            } else if (nextToken.getType() == LexicalAnalyzer.TokenType.VAR) {
                processVar(nextToken, iterator);
            } else {
                throw new SyntacticException("Unexpected token after label: " + nextToken.getValue());
            }
        }
    }

    private void processValue(LexicalAnalyzer.Token token, Iterator<LexicalAnalyzer.Token> iterator) throws SyntacticException {
        // Implement processing for values
        // This could involve storing the value or performing some action with it
        if (!iterator.hasNext()) {
            return;
        }
    }
    

    private boolean isValueOrVar(LexicalAnalyzer.Token token) {
        return token != null && (token.getType() == LexicalAnalyzer.TokenType.VALUE || token.getType() == LexicalAnalyzer.TokenType.VAR);
    }

    private boolean isValue(LexicalAnalyzer.Token token) {
        return token != null && token.getType() == LexicalAnalyzer.TokenType.VALUE;
    }
    public HashMap<String, Integer> getSymbolTable() {
        return symbolTable;
    }
    public static class SyntacticException extends Exception {
        public SyntacticException(String message) {
            super(message);
        }
    }
    public static void main(String[] args) {
        String input = "ORG 100\n" +
            "LOOP: LDA 5\n" +
            "STA 20\n" +
            "ADD 30\n" +
            "JMP LOOP\n" +
            "HLT\n" +
            "a: db 5\n";
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        SyntacticAnalyzer syntacticAnalyzer = new SyntacticAnalyzer();
        try {
            List<LexicalAnalyzer.Token> tokens = lexicalAnalyzer.analyze(input);
            System.out.println("Tokens: " + tokens);
            syntacticAnalyzer.analyze(tokens);

        } catch (LexicalAnalyzer.LexicalException | SyntacticException e) {
            e.printStackTrace();
    }
}
}