package de.joonko.loan.identification.config;

import de.joonko.loan.config.HttpLJettyLoggingHandler;
import de.joonko.loan.filter.LogRequestFilter;
import de.joonko.loan.filter.LogResponseFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.channel.BootstrapHandlers;
import reactor.netty.http.client.HttpClient;

@Configuration
@AllArgsConstructor
@EnableMongoAuditing
public class IdentificationConfig {
    private final IdentificationPropConfig idNowConfig;
    private final LogRequestFilter logRequest;
    private final LogResponseFilter logResponseFilter;

    @Bean
    @Qualifier("idNowWebClient")
    public WebClient idNowWebClient() {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient ->
                        tcpClient.bootstrap(bootstrap ->
                                BootstrapHandlers.updateLogSupport(bootstrap, new HttpLJettyLoggingHandler(HttpClient.class))));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(idNowConfig.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("idNow"))
                .build();
    }
}
