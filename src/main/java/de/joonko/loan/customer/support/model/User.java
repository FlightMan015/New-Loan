package de.joonko.loan.customer.support.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String loanApplicationId;
    private Integer loanAsked;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String houseNumber;
    private String street;
    private String city;
    private String postalCode;
    private String payment_date;
}
