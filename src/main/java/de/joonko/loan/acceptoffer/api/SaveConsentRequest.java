package de.joonko.loan.acceptoffer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class SaveConsentRequest {
    @NotNull(message = "Application id must not be null")
    private String loanApplicationId;

    private Boolean termsAccepted;

    @AssertTrue(message = "termsAccepted must be true")
    public boolean isTermsAcceptedTrue() {
        return getTermsAccepted() != null && getTermsAccepted().booleanValue() == true;
    }
}
