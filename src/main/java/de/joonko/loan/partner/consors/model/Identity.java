package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Identity {
    private String issuingAuthority;
    private String placeOfIssue;
    private LocalDate validTill;
    private String residencePermitType;
    private String residencePermitDateOfIssue;
    private String residencePermitValidTill;


}
