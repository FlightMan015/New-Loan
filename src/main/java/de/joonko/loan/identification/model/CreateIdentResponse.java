package de.joonko.loan.identification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CreateIdentResponse {
    private String kycUrl;
    private IdentificationProvider kycProvider;
    private Documents documents;
}
