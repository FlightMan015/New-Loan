package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EmploymentDetails {

    private Profession profession; // required - not supported by us

    private EmployerAddress employerAddress; // required if e.g. SELF_EMPLOYED, REGULAR_EMPLOYED - not supported by us

    private String professionBeginDate; // required

    private String industry; // optional - required if SELF_EMPLOYED or MANAGING_DIRECTOR - not supported by us

    private String professionEndDate; // optional
}
