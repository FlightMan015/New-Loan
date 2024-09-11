package de.joonko.loan.webclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import org.springframework.http.HttpHeaders;

public class LocalMockServerRunner {

    @Getter
    private final WireMockServer server;

    public LocalMockServerRunner() {
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();
        configureFor("localhost", server.port());
    }

    public void stop() {
        server.stop();
    }

    public void resetAll() {
        server.resetAll();
    }

    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:" + server.port())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public WebClient getWebClient(String urlPrefix) {
        return WebClient.builder()
                .baseUrl("http://localhost:" + server.port() + "/" + urlPrefix)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


}
