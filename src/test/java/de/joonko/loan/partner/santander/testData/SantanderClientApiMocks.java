package de.joonko.loan.partner.santander.testData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.joonko.loan.partner.santander.model.entry.ContractDomain;
import de.joonko.loan.partner.santander.model.entry.ContractEntryRequest;
import de.joonko.loan.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

@AllArgsConstructor
public class SantanderClientApiMocks {

    @Getter
    private final WireMockServer mockServer;

    public void fake200WhenCreatingContractEntry(String scbAntragId, String mdti, String actionId) throws JsonProcessingException {
        String request = JsonUtil.getObjectAsJsonString(ContractEntryRequest.builder()
                        .applicationNo(scbAntragId)
                        .transactionId(mdti)
                        .actionId(actionId)
                        .contractDomain(ContractDomain.DIRECT)
                .build());

        mockServer.stubFor(
                WireMock.post("/api/entry")
                        .withRequestBody(equalTo(request))
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake500WhenCreatingContractEntry() {
        mockServer.stubFor(
                WireMock.post("/api/entry")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );
    }

    public void fake400WhenCreatingContractEntry() {
        mockServer.stubFor(
                WireMock.post("/api/entry")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                                .withBody("Error creating User entry. User (transactionId=transactionId, actionId=actionId) already exists.")
                        )
        );
    }
}
