
package de.joonko.loan.identification.model.webid.useractionrequest;

import lombok.Data;

@Data
public class Address {
    private String street;
    private String streetNo;
    private String addressLine1;
    private String addressLine2;
    private String region;
    private String zip;
    private String city;
    private String country;
}
