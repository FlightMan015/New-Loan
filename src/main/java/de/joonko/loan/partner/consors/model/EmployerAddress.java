package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EmployerAddress {
    private String employerName;
    private String employerStreet;
    private String employerZipcode;
    private String employerCity;
}
