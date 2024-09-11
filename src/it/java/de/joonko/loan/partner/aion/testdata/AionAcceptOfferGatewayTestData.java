package de.joonko.loan.partner.aion.testdata;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.partner.aion.model.BestOfferValue;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.partner.aion.model.OfferDetails;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class AionAcceptOfferGatewayTestData {

    public static OfferChoiceResponse getOfferChoiceResponse() {
        return OfferChoiceResponse.builder()
                .processId("cad3d15f-d579-4616-969e-da1fb7304f00")
                .representativeId("6cf3154a-e71c-4921-a899-593edc13d05c")
                .draftAgreement(List.of())
                .build();
    }

    public static LoanOfferStore getLoanOfferStore(String loanOfferId, int amount) {
        return LoanOfferStore.builder()
                .loanOfferId(loanOfferId)
                .offer(LoanOffer.builder()
                        .amount(amount)
                        .durationInMonth(12)
                        .monthlyRate(BigDecimal.ONE)
                        .totalPayment(BigDecimal.TEN)
                        .build())
                .build();
    }

    public static CreditApplicationResponseStore getCreditApplicationStore(String applicationId, int amount, String loanProviderOfferId) {
        return CreditApplicationResponseStore.builder()
                .processId(UUID.randomUUID().toString())
                .applicationId(applicationId)
                .representativeId("e7a88a8c-c865-41db-8ec1-59b4584e818e")
                .offersProvided(List.of(
                        BestOfferValue.builder().offerDetails(OfferDetails.builder()
                                .id(loanProviderOfferId)
                                .amount(BigDecimal.valueOf(amount))
                                .maturity(12)
                                .monthlyInstalmentAmount(BigDecimal.ONE)
                                .totalRepaymentAmount(BigDecimal.TEN)
                                .build()).build()
                ))
                .build();
    }
}
