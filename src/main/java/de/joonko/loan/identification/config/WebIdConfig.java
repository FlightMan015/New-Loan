package de.joonko.loan.identification.config;

import de.joonko.loan.config.HttpLJettyLoggingHandler;
import de.joonko.loan.filter.LogRequestFilter;
import de.joonko.loan.filter.LogResponseFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.channel.BootstrapHandlers;
import reactor.netty.http.client.HttpClient;

@Configuration
@AllArgsConstructor
public class WebIdConfig {
    private final WebIdPropConfig webIdPropConfig;
    private final LogRequestFilter logRequest;
    private final LogResponseFilter logResponseFilter;

    @Bean
    @Qualifier("WebIdWebClient")
    public WebClient WebIdWebClient() {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient ->
                        tcpClient.bootstrap(bootstrap ->
                                BootstrapHandlers.updateLogSupport(bootstrap, new HttpLJettyLoggingHandler(HttpClient.class))));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(webIdPropConfig.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeaders(headers -> headers.setBasicAuth(webIdPropConfig.getBasicAuthUsername(), webIdPropConfig.getBasicAuthPassword()))
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("WebId"))
                .build();
    }
}
