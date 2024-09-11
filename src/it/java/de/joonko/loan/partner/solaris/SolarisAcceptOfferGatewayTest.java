package de.joonko.loan.partner.solaris;

import com.github.tomakehurst.wiremock.WireMockServer;
import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.common.partner.solaris.auth.AccessToken;
import de.joonko.loan.common.partner.solaris.auth.SolarisAuthService;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.aion.AionAcceptOfferGateway;
import de.joonko.loan.partner.aion.model.AionAcceptOfferRequest;
import de.joonko.loan.partner.solaris.model.SolarisAcceptOfferRequest;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.joonko.loan.partner.solaris.testdata.SolarisAcceptOfferGatewayTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
@ContextConfiguration(initializers = WireMockInitializer.class)
class SolarisAcceptOfferGatewayTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WireMockServer mockServer;

    @Autowired
    private SolarisAcceptOfferGateway solarisAcceptOfferGateway;

    @MockBean
    private SolarisAuthService solarisAuthService;

    @BeforeEach
    void setUp() {
        mockServer.resetAll();
        mongoTemplate.dropCollection("solarisOffer");
    }

    @Test
    void acceptOfferEmptyCase() {
        // given
        var applicationId = "8327565";
        var loanOfferId = "98565";
        var loanDuration = LoanDuration.SIX;
        var loanAsked = 1500;
        var solarisAcceptOfferRequest = SolarisAcceptOfferRequest.builder()
                .duration(loanDuration)
                .loanAsked(loanAsked)
                .build();
        when(solarisAuthService.getToken(applicationId)).thenReturn(Mono.just(new AccessToken("accessToken", "bearer", 30000)));

        // when
        var actualAcceptedOffer = solarisAcceptOfferGateway.callApi(solarisAcceptOfferRequest, applicationId, loanOfferId);

        // then
        StepVerifier.create(actualAcceptedOffer)
                .expectNextCount(0)
                .verifyErrorMessage("Can not find get offer response");
    }

    @Test
    void acceptOfferChoice() {
        // given
        var applicationId = "8327565";
        var loanOfferId = "98565";
        var loanDuration = LoanDuration.SIX;
        var loanAsked = 1500;
        var solarisAcceptOfferRequest = SolarisAcceptOfferRequest.builder()
                .duration(loanDuration)
                .loanAsked(loanAsked)
                .build();
        var solarisGetOfferResponseStore = getSolarisGetOfferResponseStore(applicationId, loanDuration.getValue(), loanAsked * 100);
        when(solarisAuthService.getToken(applicationId)).thenReturn(Mono.just(new AccessToken("accessToken", "bearer", 30000)));
        mongoTemplate.insert(solarisGetOfferResponseStore);
        mockConsumerLoanApplication(mockServer, solarisGetOfferResponseStore.getSolarisGetOffersResponse().getPersonId(), solarisGetOfferResponseStore.getSolarisGetOffersResponse().getId());
        mockGetOffersContractDocument(mockServer, solarisGetOfferResponseStore.getSolarisGetOffersResponse().getPersonId(), solarisGetOfferResponseStore.getSolarisGetOffersResponse().getId());

        // when
        var actualAcceptedOffer = solarisAcceptOfferGateway.callApi(solarisAcceptOfferRequest, applicationId, loanOfferId);

        // then
        StepVerifier.create(actualAcceptedOffer).expectNextCount(1).verifyComplete();
    }
}