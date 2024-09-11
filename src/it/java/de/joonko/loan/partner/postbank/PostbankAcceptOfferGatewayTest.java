package de.joonko.loan.partner.postbank;

import de.joonko.loan.common.domain.Bank;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static de.joonko.loan.partner.postbank.testdata.PostbankAcceptOfferGatewayTestData.getLoanOfferStore;
import static de.joonko.loan.partner.postbank.testdata.PostbankAcceptOfferGatewayTestData.getPostbankLoanDemandStore;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class PostbankAcceptOfferGatewayTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PostbankAcceptOfferGateway postbankAcceptOfferGateway;

    @Test
    void getPostbank() {
        // given
        // when
        final var bank = postbankAcceptOfferGateway.getBank();

        // then
        assertEquals(Bank.POSTBANK, bank);
    }

    @Test
    void getLoanProviderReferenceNumberWhenExists() {
        // given
        var applicationId = "1235";
        var loanOfferId = "98713";
        var expectedLoanProviderOfferId = "7638576";
        mongoTemplate.insert(getLoanOfferStore(applicationId, loanOfferId));
        mongoTemplate.insert(getPostbankLoanDemandStore(applicationId, expectedLoanProviderOfferId));
        mongoTemplate.insert(getPostbankLoanDemandStore("applicationId-ignore", "8139639"));

        // when
        var actualLoanProviderReferenceNumber = postbankAcceptOfferGateway.getLoanProviderReferenceNumber(applicationId, loanOfferId);

        // then
        StepVerifier.create(actualLoanProviderReferenceNumber).consumeNextWith(actual ->
                assertEquals(expectedLoanProviderOfferId, actual)).verifyComplete();
    }

    @Test
    void getLoanProviderReferenceNumberWhenOfferDoesNotExist() {
        // given
        var applicationId = "1236";
        var loanOfferId = "98714";

        // when
        var actualLoanProviderReferenceNumber = postbankAcceptOfferGateway.getLoanProviderReferenceNumber(applicationId, loanOfferId);

        // then
        StepVerifier.create(actualLoanProviderReferenceNumber).verifyError();
    }

    @Test
    void getLoanProviderReferenceNumberWhenContractNumberMissing() {
        // given
        var applicationId = "1236";
        var loanOfferId = "98714";
        mongoTemplate.insert(getLoanOfferStore(applicationId, loanOfferId));
        mongoTemplate.insert(getPostbankLoanDemandStore(applicationId, null));

        // when
        var actualLoanProviderReferenceNumber = postbankAcceptOfferGateway.getLoanProviderReferenceNumber(applicationId, loanOfferId);

        // then
        StepVerifier.create(actualLoanProviderReferenceNumber).verifyError();
    }
}
