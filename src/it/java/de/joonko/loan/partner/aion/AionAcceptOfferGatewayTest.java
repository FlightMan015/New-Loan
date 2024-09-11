package de.joonko.loan.partner.aion;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.partner.aion.model.*;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceRequest;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.joonko.loan.partner.aion.testdata.AionAcceptOfferGatewayTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
class AionAcceptOfferGatewayTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AionAcceptOfferGateway aionAcceptOfferGateway;

    @MockBean
    private AionClient aionClient;

    @Test
    void getAionBank() {
        // given
        // when
        Bank bank = aionAcceptOfferGateway.getBank();

        // then
        assertEquals(Bank.AION, bank);
    }

    @Test
    void getErrorWhenFailedGettingToken() {
        // given
        var applicationId = "8327561";
        var loanOfferId = "98561";
        when(aionClient.getToken(applicationId)).thenReturn(Mono.error(new RuntimeException()));

        // when
        var actualAcceptedOffer = aionAcceptOfferGateway.callApi(new AionAcceptOfferRequest(), applicationId, loanOfferId);

        // then
        StepVerifier.create(actualAcceptedOffer).verifyError();
    }

    @Test
    void getErrorWhenFailedGettingProcessId() {
        // given
        var applicationId = "8327562";
        var loanOfferId = "98562";
        when(aionClient.getToken(applicationId)).thenReturn(Mono.just(AionAuthToken.builder().build()));

        // when
        var actualAcceptedOffer = aionAcceptOfferGateway.callApi(new AionAcceptOfferRequest(), applicationId, loanOfferId);

        // then
        StepVerifier.create(actualAcceptedOffer).verifyError();
    }

    @Test
    void getErrorWhenFailedGettingLoanProviderOfferId() {
        // given
        var applicationId = "8327563";
        var loanOfferId = "98563";
        when(aionClient.getToken(applicationId)).thenReturn(Mono.just(AionAuthToken.builder().build()));
        mongoTemplate.insert(getCreditApplicationStore(applicationId, 260, "0d7cdb74-7c47-4d92-a69a-a32293cc03c3"));

        // when
        var actualAcceptedOffer = aionAcceptOfferGateway.callApi(new AionAcceptOfferRequest(), applicationId, loanOfferId);

        // then
        StepVerifier.create(actualAcceptedOffer).verifyError();
    }

    @Test
    void getErrorWhenFailedSendingOfferChoice() {
        // given
        var applicationId = "8327564";
        var loanOfferId = "98564";
        var authToken = AionAuthToken.builder().build();
        when(aionClient.getToken(applicationId)).thenReturn(Mono.just(authToken));
        when(aionClient.sendOfferChoice(eq(authToken), anyString(), any(OfferChoiceRequest.class))).thenReturn(Mono.error(new RuntimeException()));
        mongoTemplate.insert(getLoanOfferStore(loanOfferId, 179));
        mongoTemplate.insert(getCreditApplicationStore(applicationId, 179, "0d7cdb74-7c47-4d92-a69a-a32293cc03c3"));

        // when
        var actualAcceptedOffer = aionAcceptOfferGateway.callApi(new AionAcceptOfferRequest(), applicationId, loanOfferId);

        // then
        StepVerifier.create(actualAcceptedOffer).verifyError();
    }

    @Test
    void acceptOfferChoice() {
        // given
        var applicationId = "8327565";
        var loanOfferId = "98565";
        var authToken = AionAuthToken.builder().build();
        when(aionClient.getToken(applicationId)).thenReturn(Mono.just(authToken));
        when(aionClient.sendOfferChoice(eq(authToken), anyString(), any(OfferChoiceRequest.class))).thenReturn(Mono.just(getOfferChoiceResponse()));
        mongoTemplate.insert(getLoanOfferStore(loanOfferId, 180));
        mongoTemplate.insert(getCreditApplicationStore(applicationId, 180, "0d7cdb74-7c47-4d92-a69a-a32293cc03c3"));

        // when
        var actualAcceptedOffer = aionAcceptOfferGateway.callApi(new AionAcceptOfferRequest(), applicationId, loanOfferId);

        // then
        StepVerifier.create(actualAcceptedOffer).expectNextCount(1).verifyComplete();
    }

    @Test
    void getLoanProviderReferenceNumberWhenExists() {
        // given
        var applicationId = "1234";
        var loanOfferId = "98712";
        var expectedLoanProviderOfferId = "f8a2e7ba-25f0-426a-8725-7b7709575c59";
        mongoTemplate.insert(getLoanOfferStore(loanOfferId, 134));
        mongoTemplate.insert(getCreditApplicationStore(applicationId, 134, expectedLoanProviderOfferId));
        mongoTemplate.insert(getCreditApplicationStore(applicationId, 164, "0d7cdb74-7c47-4d92-a69a-a32293cc03c3"));

        // when
        var actualLoanProviderReferenceNumber = aionAcceptOfferGateway.getLoanProviderReferenceNumber(applicationId, loanOfferId);

        // then
        StepVerifier.create(actualLoanProviderReferenceNumber).consumeNextWith(actual ->
                assertEquals(expectedLoanProviderOfferId, actual)).verifyComplete();
    }

    @Test
    void getLoanProviderReferenceNumberAsExceptionWhenOfferDoesNotExist() {
        // given
        var applicationId = "123";
        var loanOfferId = "9872";

        // when
        var actualLoanProviderReferenceNumber = aionAcceptOfferGateway.getLoanProviderReferenceNumber(applicationId, loanOfferId);

        // then
        StepVerifier.create(actualLoanProviderReferenceNumber).verifyError();
    }

    @Test
    void getLoanProviderReferenceNumberAsNullWhenCreditApplicationDoesNotExist() {
        // given
        var applicationId = "123";
        var loanOfferId = "987";
        mongoTemplate.insert(getLoanOfferStore(loanOfferId, 134));
        mongoTemplate.insert(getCreditApplicationStore(applicationId, 164, "0d7cdb74-7c47-4d92-a69a-a32293cc03c3"));

        // when
        var actualLoanProviderReferenceNumber = aionAcceptOfferGateway.getLoanProviderReferenceNumber(applicationId, loanOfferId);

        // then
        StepVerifier.create(actualLoanProviderReferenceNumber).expectNextCount(0).verifyComplete();
    }
}
