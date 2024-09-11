package de.joonko.loan.identification.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetKycUrlResponse {
    private String kycUrl;
    private String loanProvider;
    private IdentificationProvider kycProvider;
}
