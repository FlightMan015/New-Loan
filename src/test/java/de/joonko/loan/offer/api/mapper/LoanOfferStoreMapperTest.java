package de.joonko.loan.offer.api.mapper;

import de.joonko.loan.offer.api.mapper.testdata.LoanOfferStoreTestData;
import de.joonko.loan.offer.api.model.LoanOfferStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanOfferStoreMapperTest {

    private LoanOfferStoreMapper mapper;
    private LoanOfferStoreTestData testData;

    @BeforeEach
    void setUp() {
        mapper = new LoanOfferStoreMapperImpl();
        testData = new LoanOfferStoreTestData();
    }

    @Test
    void map_loan_offer_store_from_domain_to_api_model() {
        // given
        de.joonko.loan.db.vo.LoanOfferStore domainLoanOfferStore = testData.getDomainLoanOfferStore();

        // when
        LoanOfferStore apiLoanOfferStore = mapper.map(domainLoanOfferStore);

        // then
        assertAll(
                () -> assertEquals(apiLoanOfferStore.getLoanOfferId(), domainLoanOfferStore.getLoanOfferId(), "loanOfferId not equal"),
                () -> assertEquals(apiLoanOfferStore.getUserUUID(), domainLoanOfferStore.getUserUUID(), "userUuid not equal"),
                () -> assertEquals(apiLoanOfferStore.getApplicationId(), domainLoanOfferStore.getApplicationId(), "applicationId not equal"),
                () -> assertEquals(apiLoanOfferStore.getKycUrl(), domainLoanOfferStore.getKycUrl(), "kycUrl not equal"),
                () -> assertEquals(apiLoanOfferStore.getOfferStatus(), domainLoanOfferStore.getOfferStatus(), "offerStatus not equal"),
                () -> assertEquals(apiLoanOfferStore.getKycStatus(), domainLoanOfferStore.getKycStatus(), "kycStatus not equal"),
                () -> assertEquals(apiLoanOfferStore.getKycProvider(), domainLoanOfferStore.getKycProvider(), "kycProvider not equal"),
                () -> assertTrue(apiLoanOfferStore.getIsRecommendation(), "isRecommendation not true"),
                () -> assertEquals(apiLoanOfferStore.getOffer().getAmount(), domainLoanOfferStore.getOffer().getAmount(), "offer amount not equal"),
                () -> assertEquals(apiLoanOfferStore.getIsAccepted(), domainLoanOfferStore.getIsAccepted(), "isAccepted not equal"),
                () -> assertEquals(apiLoanOfferStore.getLoanProviderReferenceNumber(), domainLoanOfferStore.getLoanProviderReferenceNumber(), "loanProviderReferenceNumber not equal"),
                () -> assertEquals(apiLoanOfferStore.getDeleted(), domainLoanOfferStore.getDeleted(), "deleted not equal"),
                () -> assertEquals(apiLoanOfferStore.getInsertTS(), domainLoanOfferStore.getInsertTS(), "insertTs not equal")
        );
    }
}
