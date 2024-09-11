package de.joonko.loan.offer.api.mapper.testdata;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanOfferStoreTestData {

    public LoanOfferStore getDomainLoanOfferStore() {
        return LoanOfferStore.builder()
                .loanOfferId("loanOfferId")
                .userUUID("userId")
                .applicationId("applicationId")
                .parentApplicationId("parentApplicationId")
                .kycUrl("kycUrl")
                .offerStatus("offerStatus")
                .kycStatus("kycStatus")
                .kycProvider(IdentificationProvider.ID_NOW)
                .offer(LoanOffer.builder()
                        .amount(1000)
                        .durationInMonth(24)
                        .effectiveInterestRate(BigDecimal.TEN)
                        .nominalInterestRate(BigDecimal.ONE)
                        .monthlyRate(BigDecimal.TEN)
                        .totalPayment(BigDecimal.TEN)
                        .loanProvider(new LoanProvider("santander"))
                        .build())
                .isAccepted(false)
                .loanProviderReferenceNumber("loanProviderReferenceNumber")
                .deleted(false)
                .insertTS(LocalDateTime.now())
                .build();
    }
}
