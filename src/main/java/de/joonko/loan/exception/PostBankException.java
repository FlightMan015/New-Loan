package de.joonko.loan.exception;

public class PostBankException extends RuntimeException {

    public PostBankException(String message) {
        super(message);
    }

    public PostBankException(String message, Throwable e) {
        super(message, e);
    }
}
