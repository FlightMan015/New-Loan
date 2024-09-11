package de.joonko.loan.exception;

public class ConsorsBankException extends RuntimeException {

    public ConsorsBankException(String message) {
        super(message);
    }

    public ConsorsBankException(String message, Throwable e) {
        super(message, e);
    }
}
