package de.joonko.loan.offer.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviousAddress {
    private String street;
    private String postCode;
    private String city;
    private Nationality country;
    private String houseNumber;
    private LocalDate livingSince;
}
