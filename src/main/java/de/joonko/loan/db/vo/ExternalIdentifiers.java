package de.joonko.loan.db.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalIdentifiers {

    private String auxmoneyExternalIdentifier;
    private String swkExternalIdentifier;
    private String consorsExternalIdentifier;


    public static ExternalIdentifiers fromLoanApplicationId(String loanApplication) {
        return ExternalIdentifiers
                .builder()
                .auxmoneyExternalIdentifier(auxmoneyExternalIdentifierFromApplicationId(loanApplication))
                .consorsExternalIdentifier(consorsExternalIdentifierExternalIdentifierFromApplicationId(loanApplication))
                .swkExternalIdentifier(swkExternalIdentifierFromApplicationId(loanApplication))
                .build();
    }

    public static String auxmoneyExternalIdentifierFromApplicationId(String loanApplication) {
        return loanApplication;
    }

    public static String swkExternalIdentifierFromApplicationId(String loanApplication) {
        return loanApplication.substring(0, 23);
    }

    public static String consorsExternalIdentifierExternalIdentifierFromApplicationId(String loanApplication) {
        return loanApplication.substring(0, 20);
    }

}
