package de.joonko.loan.identification.testdata;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.identification.IDNowResponses;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.model.IdentificationProvider;

import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static de.joonko.loan.identification.service.idnow.testdata.IdNowResponses.get404IdentResponse;

public class IdentificationStatusITTestData {
    public static IdentificationLink getIdentificationLink(String applicationId, String externalId, String offerId, Bank bank) {
        return IdentificationLink.builder()
                .applicationId(applicationId)
                .offerId(offerId)
                .externalIdentId(externalId)
                .loanProvider(bank.toString())
                .identProvider(IdentificationProvider.ID_NOW)
                .kycUrl("https://go.test.idnow.de/TST-VFZES")
                .build();
    }

    public static LoanDemandStore getEditedLoanDemandStore(LoanDemandStore loanDemandStore, String applicationId) {
        loanDemandStore.setApplicationId(applicationId);
        loanDemandStore.setFirstName("9bfb1a841e46aad2a1ba488e3be72395");

        return loanDemandStore;
    }

    public static void fakeGetToken(WireMockServer mockServer, String accountId) {
        mockServer.stubFor(
                WireMock.post("/idnow/api/v1/" + accountId + "/login")
                        .willReturn(aResponse()
                                .withBody(IDNowResponses.GET_TOKEN_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }

    public static void fakeGetStatus(WireMockServer mockServer, String accountId, String id) {
        mockServer.stubFor(
                WireMock.get("/idnow/api/v1/" + accountId + "/identifications/" + id)
                        .willReturn(aResponse()
                                .withBody(IDNowResponses.GET_IDENTIFICATION_STATUS_RESPONSE)
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }

    public static void fakeNotFoundWhenGettingIdent(WireMockServer mockServer, String accountId, String identId) {
        mockServer.stubFor(
                WireMock.get("/idnow/api/v1/" + accountId + "/identifications/" + identId)
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get404IdentResponse())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.NOT_FOUND.value())
                        )
        );
    }
}
