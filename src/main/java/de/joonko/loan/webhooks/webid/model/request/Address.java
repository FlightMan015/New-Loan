package de.joonko.loan.webhooks.webid.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
