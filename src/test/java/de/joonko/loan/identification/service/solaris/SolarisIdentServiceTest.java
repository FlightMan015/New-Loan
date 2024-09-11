package de.joonko.loan.identification.service.solaris;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.common.partner.solaris.auth.AccessToken;
import de.joonko.loan.common.partner.solaris.auth.SolarisAuthService;
import de.joonko.loan.identification.AuxmoneyResponses;
import de.joonko.loan.partner.solaris.SolarisAcceptOfferResponseStore;
import de.joonko.loan.partner.solaris.SolarisStoreService;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
class SolarisIdentServiceTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private WireMockServer mockServer;
    @Autowired
    private SolarisIdentService solarisIdentService;

    @MockBean
    private SolarisAuthService solarisAuthService;


    @BeforeEach
    void clearMockServerExpectations() {
        mockServer.resetAll();
    }

    @Test
    void getIdentStatusTest() {
        // given
        var identificationId = "864567";
        var personId = "1234";
        mongoTemplate.insert(SolarisAcceptOfferResponseStore.builder()
                .identificationId(identificationId)
                .applicationId("1234")
                .personId(personId)
                .build()
        );
        when(solarisAuthService.getToken(anyString())).thenReturn(Mono.just(new AccessToken("accessToken", "bearer", 30000)));
        fakeGetSolarisIdentificationStatus(personId, identificationId);

        // when
        var identStatus = solarisIdentService.getIdentStatus(identificationId);

        // then
        StepVerifier.create(identStatus).expectNextCount(1).verifyComplete();
    }

    @Test
    void getIdentStatusErrorWithNoAcceptOffer() {
        // given
        var identificationId = "864567";
        // when
        var identStatus = solarisIdentService.getIdentStatus(identificationId);

        // then
        StepVerifier.create(identStatus).verifyErrorMessage("Can not find solaris accept offer " + identificationId);
    }

    private void fakeGetSolarisIdentificationStatus(String personId, String identificationId) {
        //https://acc.auxacc.de/solaris/v1/persons/1234/identifications/864567

        mockServer.stubFor(
                WireMock.get("/solaris/v1/persons/" + personId + "/identifications/" + identificationId)
                        .withHeader("Authorization", equalTo("Bearer accessToken"))
                        .willReturn(aResponse()
                                .withBody(de.joonko.loan.offer.api.SolarisResponses.GET_IDENTIFICATION_STATUS_RESPONSE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }
}