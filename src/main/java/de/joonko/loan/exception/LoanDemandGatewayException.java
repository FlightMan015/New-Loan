package de.joonko.loan.exception;

public class LoanDemandGatewayException extends RuntimeException {

    public LoanDemandGatewayException(String message) {
        super(message);
    }

    public LoanDemandGatewayException(String message, Throwable it) {
        super(message, it);
    }
}
