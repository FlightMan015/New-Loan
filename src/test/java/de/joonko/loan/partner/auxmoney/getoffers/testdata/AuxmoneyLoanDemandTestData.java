package de.joonko.loan.partner.auxmoney.getoffers.testdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import de.joonko.loan.partner.auxmoney.model.ErrorResponse;
import de.joonko.loan.util.JsonUtil;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public class AuxmoneyLoanDemandTestData {
    public static void setGetAuxmoneyGetOfferExpectations(WireMockServer mockServer, AuxmoneySingleCallResponse auxmoneySingleCallResponse) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post("/auxmoney/distributionline/api/rest/partnerendpoints")
                        .withHeader("urlkey", equalTo("somekey"))
                        .willReturn(aResponse()
                                .withBody(JsonUtil.getObjectAsJsonString(auxmoneySingleCallResponse))
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }

    public static void setBadRequestResponseExpectations(WireMockServer mockServer, ErrorResponse errorResponse) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.post("/auxmoney/distributionline/api/rest/partnerendpoints")
                        .withHeader("urlkey", equalTo("somekey"))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                                .withBody(JsonUtil.getObjectAsJsonString(errorResponse))
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }
}
