package de.joonko.loan.exception;

public class CsvGenerationFailedException extends RuntimeException {

    public CsvGenerationFailedException(final String message) {
        super(message);
    }
}
