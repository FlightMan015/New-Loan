package de.joonko.loan.offer.api;

import lombok.Data;

@Data
public class CustomDacPersonalDetails {

    private String firstName;
    private String lastName;
    private String numberOfChildren;
    private String employerName;
    private Integer numberOfCreditCard;

}