package com.daemonide.expensetracker.exception;

public class NoSuchFinancialAccountExistsException extends RuntimeException {
    public NoSuchFinancialAccountExistsException(String message) {
        super(message);
    }
}
