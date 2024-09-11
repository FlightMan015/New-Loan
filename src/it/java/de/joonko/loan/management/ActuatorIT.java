package de.joonko.loan.management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;

@AutoConfigureWebTestClient
@ActiveProfiles("integration")
@SpringBootTest
class ActuatorIT {

    @Autowired
    private WebTestClient webClient;

    @Test
    void getServiceInfo() {
        webClient
                .get()
                .uri(URI.create("/actuator/info"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody();
    }

    @Test
    void getServiceStatusUp() {
        webClient
                .get()
                .uri(URI.create("/actuator/health"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void getServiceMetrics() {
        webClient
                .get()
                .uri(URI.create("/actuator/prometheus"))
                .exchange()
                .expectStatus()
                .isOk();
    }
}
