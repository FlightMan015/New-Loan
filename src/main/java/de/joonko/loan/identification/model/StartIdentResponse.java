package de.joonko.loan.identification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class StartIdentResponse {
    private String kycUrl;
    private IdentificationProvider kycProvider;
}
