package de.joonko.loan.partner.solaris.testdata;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.joonko.loan.partner.solaris.model.AmountValue;
import de.joonko.loan.partner.solaris.model.Offer;
import de.joonko.loan.partner.solaris.SolarisGetOfferResponseStore;
import de.joonko.loan.partner.solaris.model.SolarisGetOffersResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public class SolarisAcceptOfferGatewayTestData {
    public static SolarisGetOfferResponseStore getSolarisGetOfferResponseStore(String applicationId, Integer loanTerm, Integer loanAmount) {
        SolarisGetOffersResponse getOffersResponse = new SolarisGetOffersResponse();
        Offer offer = new Offer();
        offer.setLoanTerm(loanTerm);
        offer.setLoanAmount(AmountValue.builder().value(loanAmount).build());
        getOffersResponse.setOffer(offer);
        getOffersResponse.setPersonId("1234");
        getOffersResponse.setId("1234");
        return SolarisGetOfferResponseStore.builder()
                .applicationId(applicationId)
                .solarisGetOffersResponse(getOffersResponse)
                .build();
    }

    public static void mockConsumerLoanApplication(WireMockServer mockServer, String personId, String applicationId) {
        mockServer.stubFor(
                WireMock.get("/solaris/v1/persons/" + personId + "/consumer_loan_applications/" + applicationId)
                        .withHeader("Authorization", equalTo("Bearer accessToken"))
                        .willReturn(aResponse()
                                .withBody(de.joonko.loan.offer.api.SolarisResponses.GET_APPLICATION_STATUS_RESPONSE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }

    public static void mockGetOffersContractDocument(WireMockServer mockServer, String personId, String applicationId) {
        mockServer.stubFor(
                WireMock.get("/solaris/v1/persons/" + personId + "/consumer_loan_applications/" + applicationId + "/offers/contract")
                        .withHeader("Authorization", equalTo("Bearer accessToken"))
                        .willReturn(aResponse()
                                .withBody("This is test pdf dummy contract byte stream".getBytes())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
        mockServer.stubFor(
                WireMock.get("/solaris/v1/persons/" + personId + "/consumer_loan_applications/" + applicationId + "/offers/pre_contract")
                        .withHeader("Authorization", equalTo("Bearer accessToken"))
                        .willReturn(aResponse()
                                .withBody("This is test pdf dummy contract byte stream".getBytes())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }
}
