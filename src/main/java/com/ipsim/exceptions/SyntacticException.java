package com.ipsim.exceptions;
/**
* The SyntacticException class is a custom exception class for syntax errors
*/
public class SyntacticException extends Exception {
    public SyntacticException(String message) {
        super(message);
    }
}