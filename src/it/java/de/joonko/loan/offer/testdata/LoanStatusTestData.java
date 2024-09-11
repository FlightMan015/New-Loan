package de.joonko.loan.offer.testdata;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;

import java.util.List;

public class LoanStatusTestData {
    public static List<LoanOfferStore> getLoanOffersWithValidStatus() {
        return List.of(
                LoanOfferStore.builder().loanOfferId("1").offerStatus("PENDING").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder().loanOfferId("2").offerStatus("REVIEW_PENDING").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build()
        );
    }

    public static List<LoanOfferStore> getLoanOffersWithInvalidStatus() {
        return List.of(
                LoanOfferStore.builder().loanOfferId("3").offerStatus("WAITING_FOR_DOCUMENT").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder().loanOfferId("4").offerStatus("ABORTED").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build()
        );
    }

}
