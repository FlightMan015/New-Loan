package de.joonko.loan.partner.consors.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreviousAddress {
    private String street;
    private String zipcode;
    private String city;
}
