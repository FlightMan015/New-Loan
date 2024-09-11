package de.joonko.loan.identification.exception;

public class IdentificationFailureException extends RuntimeException {

    public IdentificationFailureException(String massage, Throwable throwable) {
        super(massage, throwable);
    }

    public IdentificationFailureException(String massage) {
        super(massage);
    }
}
