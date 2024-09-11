package de.joonko.loan.identification.model;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InitiateIdentificationRequest {

    @NotBlank(message = "applicationId must not be null")
    private String applicationId;

    @NotBlank(message = "loanOfferId must not be null")
    private String loanOfferId;


}
