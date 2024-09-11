package de.joonko.loan.identification.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetIdentStatusResponse {
    private String status;
    private String firstName;
    private String loanProvider;
    private IdentificationProvider kycProvider;
}
