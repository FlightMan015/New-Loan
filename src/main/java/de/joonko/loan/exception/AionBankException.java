package de.joonko.loan.exception;

public class AionBankException extends RuntimeException {

    public AionBankException(String message) {
        super(message);
    }

    public AionBankException(String message, Throwable e) {
        super(message, e);
    }
}
