package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactAddress {
    private String zipcode;
    private String city;
    private String street;
    private String telephoneMobile;
    private String telephoneLandline;
    private String validFrom;
    private String email;
}
