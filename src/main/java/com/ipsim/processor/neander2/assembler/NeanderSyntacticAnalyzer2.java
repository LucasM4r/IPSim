package com.ipsim.processor.neander2.assembler;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

import com.ipsim.exceptions.SyntacticException;
public class NeanderSyntacticAnalyzer2 {
    public HashMap<String, Integer> symbolTable = new HashMap<String, Integer>();
    
    /** 
     * The analyze method processes the tokens and checks for syntax errors
     * @param tokens
     * @throws SyntacticException
     */
    public void analyze(List<NeanderLexicalAnalyzer2.Token> tokens) throws SyntacticException {
        Iterator<NeanderLexicalAnalyzer2.Token> iterator = tokens.iterator();
        
        while (iterator.hasNext()) {
            NeanderLexicalAnalyzer2.Token token = iterator.next();
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
                    // Ignore comments
                    break;
                case EOF:
                    // Stop analyzing after EOF
                    return;
                default:
                    throw new SyntacticException("Unexpected token: " + token.getValue());
            }
        }
    }
    /**
     * Process the instruction token and check for syntax errors
     * @param token
     * @param iterator
     * @throws SyntacticException
     */
    private void processInstruction(NeanderLexicalAnalyzer2.Token token, Iterator<NeanderLexicalAnalyzer2.Token> iterator) throws SyntacticException {

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
    /**
     * Process the pseudo-instruction token and check for syntax errors
     * @param token
     * @param iterator
     * @throws SyntacticException
     */
    private void processPseudoInstruction(NeanderLexicalAnalyzer2.Token token, Iterator<NeanderLexicalAnalyzer2.Token> iterator) throws SyntacticException {
        if (!iterator.hasNext() || !isValue(iterator.next())) {
            throw new SyntacticException("Expected value after pseudo-instruction: " + token.getValue());
        }
    }
    /**
     * Process the var token and check for syntax errors
     * @param token
     * @param iterator
     * @throws SyntacticException
     */
    private void processVar(NeanderLexicalAnalyzer2.Token token, Iterator<NeanderLexicalAnalyzer2.Token> iterator) throws SyntacticException {
        if (iterator.hasNext()) {
            NeanderLexicalAnalyzer2.Token nextToken = iterator.next();
            if (nextToken.getType() != NeanderLexicalAnalyzer2.TokenType.PSEUDO_INSTRUCTION) {
                throw new SyntacticException("Expected Pseudo-instruction after var: " + token.getValue());
            }
            // Process the value associated with the variable
            processValue(nextToken, iterator);
        }
    }
    /**
     * Process the label token,check for syntax errors and store the label in the symbol table
     * @param token
     * @param iterator
     * @throws SyntacticException
     */
    private void processLabel(NeanderLexicalAnalyzer2.Token token, Iterator<NeanderLexicalAnalyzer2.Token> iterator) throws SyntacticException {
        String symbol = token.getValue().replace(":", "");
        symbolTable.put(symbol, 0);
        if (iterator.hasNext()) {
            NeanderLexicalAnalyzer2.Token nextToken = iterator.next();
            if (nextToken.getType() == NeanderLexicalAnalyzer2.TokenType.INSTRUCTION) {
                processInstruction(nextToken, iterator);
            } else if (nextToken.getType() == NeanderLexicalAnalyzer2.TokenType.PSEUDO_INSTRUCTION) {
                processPseudoInstruction(nextToken, iterator);
            } else if (nextToken.getType() == NeanderLexicalAnalyzer2.TokenType.VALUE) {
                processValue(nextToken, iterator);
            } else if (nextToken.getType() == NeanderLexicalAnalyzer2.TokenType.VAR) {
                processVar(nextToken, iterator);
            } else {
                throw new SyntacticException("Unexpected token after label: " + nextToken.getValue());
            }
        }
    }
    /**
     * Process the value token and check for syntax errors
     * @param token
     * @param iterator
     * @throws SyntacticException
     */
    private void processValue(NeanderLexicalAnalyzer2.Token token, Iterator<NeanderLexicalAnalyzer2.Token> iterator) throws SyntacticException {
        // Implement processing for values
        // This could involve storing the value or performing some action with it
        if (!iterator.hasNext()) {
            return;
        }
    }
    

    /**
     * Check if a given token is a value or a variable
     * @param token
     * @return
     */
    private boolean isValueOrVar(NeanderLexicalAnalyzer2.Token token) {
        return token != null && (token.getType() == NeanderLexicalAnalyzer2.TokenType.VALUE || token.getType() == NeanderLexicalAnalyzer2.TokenType.VAR);
    }
    /**
     * Check if a given token is a value
     * @param token
     * @return
     */
    private boolean isValue(NeanderLexicalAnalyzer2.Token token) {
        return token != null && token.getType() == NeanderLexicalAnalyzer2.TokenType.VALUE;
    }
    /**
     * Get the symbol table
     * @return
     */
    public HashMap<String, Integer> getSymbolTable() {
        return symbolTable;
    }
}