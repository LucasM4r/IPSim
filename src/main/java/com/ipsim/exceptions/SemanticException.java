package com.ipsim.exceptions;
/**
* The SemanticException class is a custom exception class for semantic errors
*/
public class SemanticException extends Exception {
    public SemanticException(String message) {
        super("Semantic Exception: " + message);
    }
}