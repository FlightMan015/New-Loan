package de.joonko.loan.integrations.segment;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static de.joonko.loan.integrations.segment.testdata.SegmentResponses.*;

@AllArgsConstructor
public class SegmentClientMocks {

    @Getter
    private final WireMockServer mockServer;

    public void fake200WhenGettingUserTraits(String spaceId, String id) {
        mockServer.stubFor(
                WireMock.get("/v1/spaces/" + spaceId + "/collections/users/profiles/" + id + "/traits?limit=200")
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get200UserTraits())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.OK.value())
                        )
        );
    }

    public void fake401WhenGettingUserTraits(String spaceId, String id) {
        mockServer.stubFor(
                WireMock.get("/v1/spaces/" + spaceId + "/collections/users/profiles/" + id + "/traits?limit=200")
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get401UserTraits())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.UNAUTHORIZED.value())
                        )
        );
    }

    public void fake404WhenGettingUserTraits(String spaceId, String id) {
        mockServer.stubFor(
                WireMock.get("/v1/spaces/" + spaceId + "/collections/users/profiles/" + id + "/traits?limit=200")
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get404UserTraits())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.NOT_FOUND.value())
                        )
        );
    }

    public void fake500WhenGettingUserTraits(String spaceId, String id) {
        mockServer.stubFor(
                WireMock.get("/v1/spaces/" + spaceId + "/collections/users/profiles/" + id + "/traits?limit=200")
                        .withHeader("Content-Type", matching(ContentType.APPLICATION_JSON.getMimeType()))
                        .willReturn(aResponse()
                                .withBody(get500UserTraits())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        )
        );
    }
}
