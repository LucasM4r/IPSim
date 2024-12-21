package com.ipsim.exceptions;
/**
* The LexicalException class is a custom exception class for lexical errors
*/
public class LexicalException extends Exception {
    public LexicalException(String message) {
        super("Lexical Exception: " + message);
    }
}