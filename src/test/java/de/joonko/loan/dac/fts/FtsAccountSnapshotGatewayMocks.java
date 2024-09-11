package de.joonko.loan.dac.fts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.joonko.loan.dac.fts.model.Account;
import de.joonko.loan.dac.fts.model.Balance;
import de.joonko.loan.dac.fts.model.FtsRawData;
import de.joonko.loan.dac.fts.model.Turnover;
import de.joonko.loan.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.entity.ContentType;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@AllArgsConstructor
public class FtsAccountSnapshotGatewayMocks {

    @Getter
    private final WireMockServer mockServer;

    public void fake200WhenAskingForFtsData(String transactionId) throws JsonProcessingException {
        final var ftsRawData = FtsRawData.builder()
                .account(Account.builder().build())
                .balance(Balance.builder().build())
                .turnovers(List.of(Turnover.builder().build()))
                .build();

        mockServer.stubFor(
                WireMock.get("/fts/" + transactionId + "/accountSnapshot?format=json")
                        .willReturn(aResponse()
                                .withBody(JsonUtil.getObjectAsJsonString(ftsRawData))
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                        )
        );
    }
}
