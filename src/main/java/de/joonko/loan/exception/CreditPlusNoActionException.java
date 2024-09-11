package de.joonko.loan.exception;

//Not to be used anywhere except its only usage on CreditPlusContractService.java
public class CreditPlusNoActionException extends Exception{

    public CreditPlusNoActionException(String message) {
        super(message);
    }
}
