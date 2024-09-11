package de.joonko.loan.partner.postbank.testdata;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.webhooks.postbank.model.ContractState;
import de.joonko.loan.webhooks.postbank.model.CreditResult;

import java.util.Set;

public class PostbankAcceptOfferGatewayTestData {

    public static PostbankLoanDemandStore getPostbankLoanDemandStore(String applicationId, String loanProviderReferenceId) {
        return PostbankLoanDemandStore.builder()
                .applicationId(applicationId)
                .contractNumber(loanProviderReferenceId)
                .creditResults(
                        Set.of(CreditResult.builder()
                                        .partnerContractNumber(applicationId)
                                        .contractState(ContractState.ONLINE_GENEHMIGT_24)
                                        .build(),
                                CreditResult.builder()
                                        .partnerContractNumber(applicationId)
                                        .contractState(ContractState.UNTERLAGEN_EINGEGANGEN_25)
                                        .build()
                        )
                ).build();
    }

    public static LoanOfferStore getLoanOfferStore(String applicationId, String loanOfferId) {
        return LoanOfferStore.builder()
                .loanOfferId(loanOfferId)
                .applicationId(applicationId)
                .build();
    }
}
