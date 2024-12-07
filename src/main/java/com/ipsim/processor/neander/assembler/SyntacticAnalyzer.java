package com.ipsim.processor.neander.assembler;

import java.util.List;
import java.util.Iterator;

public class SyntacticAnalyzer {

    public void analyze(List<LexicalAnalyzer.Token> tokens) throws SyntacticException {
        Iterator<LexicalAnalyzer.Token> iterator = tokens.iterator();
        
        while (iterator.hasNext()) {
            LexicalAnalyzer.Token token = iterator.next();
            switch (token.getType()) {
                case INSTRUCTION:
                    processInstruction(token, iterator);
                    if(token.getValue().equalsIgnoreCase("HLT")) {
                        return;
                    }
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
                case WHITESPACE:
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
                   token.getValue().equalsIgnoreCase("NOT")
                   || token.getValue().equalsIgnoreCase("HLT")) {
            // NOP and NOT do not require a value or label after them
            return;
        } else {
            if (!iterator.hasNext() || !isValueOrVar(skipWhitespace(iterator))) {
                throw new SyntacticException("Expected value or var after instruction: " + token.getValue());
            }
        }
    }

    private void processPseudoInstruction(LexicalAnalyzer.Token token, Iterator<LexicalAnalyzer.Token> iterator) throws SyntacticException {
        if (!iterator.hasNext() || !isValue(skipWhitespace(iterator))) {
            throw new SyntacticException("Expected value after pseudo-instruction: " + token.getValue());
        }
    }

    private void processVar(LexicalAnalyzer.Token token, Iterator<LexicalAnalyzer.Token> iterator) throws SyntacticException {
        if (iterator.hasNext()) {
            LexicalAnalyzer.Token nextToken = skipWhitespace(iterator);
            if (nextToken.getType() != LexicalAnalyzer.TokenType.PSEUDO_INSTRUCTION) {
                throw new SyntacticException("Expected Pseudo-instruction after var: " + token.getValue());
            }
            // Process the value associated with the variable
            processValue(nextToken, iterator);
        }
    }

    private void processLabel(LexicalAnalyzer.Token token, Iterator<LexicalAnalyzer.Token> iterator) throws SyntacticException {
        if (iterator.hasNext()) {
            LexicalAnalyzer.Token nextToken = skipWhitespace(iterator);
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

    private LexicalAnalyzer.Token skipWhitespace(Iterator<LexicalAnalyzer.Token> iterator) {
        while (iterator.hasNext()) {
            LexicalAnalyzer.Token token = iterator.next();
            if (token.getType() != LexicalAnalyzer.TokenType.WHITESPACE) {
                return token;
            }
        }
        return null;
    }

    private boolean isValueOrVar(LexicalAnalyzer.Token token) {
        return token != null && (token.getType() == LexicalAnalyzer.TokenType.VALUE || token.getType() == LexicalAnalyzer.TokenType.VAR);
    }

    private boolean isValue(LexicalAnalyzer.Token token) {
        return token != null && token.getType() == LexicalAnalyzer.TokenType.VALUE;
    }

    public static class SyntacticException extends Exception {
        public SyntacticException(String message) {
            super(message);
        }
    }
}