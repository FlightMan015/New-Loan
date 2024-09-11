package de.joonko.loan.exception;

public class SolarisAuthException extends RuntimeException {

    public SolarisAuthException(String message) {
        super(message);
    }

    public SolarisAuthException(String message, Throwable e) {
        super(message, e);
        e.printStackTrace();
    }
}
