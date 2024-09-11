package de.joonko.loan.partner.postbank;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static de.joonko.loan.partner.postbank.testData.PostbankResponses.*;

@AllArgsConstructor
public class PostbankClientMocks {

    @Getter
    private final WireMockServer mockServer;

    public void fake200WhenAskingForLoan(String applicationId) {
        mockServer.stubFor(
                WireMock.post("/postbank/kreditantrag")
                        .willReturn(aResponse()
                                .withBody(get200ForLoanDemand(applicationId))
                                .withHeader("Content-Type", "text/xml;charset=UTF-8")
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake200WithDacErrorWhenAskingForLoan(String applicationId) {
        mockServer.stubFor(
                WireMock.post("/postbank/kreditantrag")
                        .willReturn(aResponse()
                                .withBody(get200WithDacError(applicationId))
                                .withHeader("Content-Type", "text/xml;charset=UTF-8")
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake200WithInvalidIbanWhenAskingForLoan(String applicationId) {
        mockServer.stubFor(
                WireMock.post("/postbank/kreditantrag")
                        .willReturn(aResponse()
                                .withBody(get200WithInvalidIban(applicationId))
                                .withHeader("Content-Type", "text/xml;charset=UTF-8")
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake400WhenAskingForLoan() {
        mockServer.stubFor(
                WireMock.post("/postbank/kreditantrag")
                        .willReturn(aResponse()
                                .withBody(get400ForLoanDemand())
                                .withHeader("Content-Type", "text/xml;charset=UTF-8")
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                        )
        );
    }
}
