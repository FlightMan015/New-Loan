package de.joonko.loan.config;

import de.joonko.loan.filter.LogRequestFilter;
import de.joonko.loan.filter.LogResponseFilter;
import de.joonko.loan.integrations.segment.SegmentPropertiesConfig;
import de.joonko.loan.partner.aion.AionPropertiesConfig;
import de.joonko.loan.partner.consors.ConsorsPropertiesConfig;
import de.joonko.loan.partner.postbank.PostbankPropertiesConfig;
import de.joonko.loan.partner.solaris.SolarisPropertiesConfig;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import reactor.netty.channel.BootstrapHandlers;
import reactor.netty.http.client.HttpClient;

import java.util.Base64;

import reactor.netty.channel.BootstrapHandlers;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class LoanApplicationConfig {

    private final AuxmoneyConfig auxmoneyConfig;
    private final LogRequestFilter logRequest;
    private final LogResponseFilter logResponseFilter;
    private final ConsorsPropertiesConfig consorsPropertiesConfig;
    private final CustomerSupportConfig customerSupportConfig;
    private final SolarisPropertiesConfig solarisPropertiesConfig;
    private final SegmentPropertiesConfig segmentPropertiesConfig;
    private final SantanderConfig santanderConfig;
    private final AionPropertiesConfig aionPropertiesConfig;
    private final PostbankPropertiesConfig postbankPropertiesConfig;
    private final FtsConfig ftsConfig;


    private final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 10 * 10 * 30)).build();

    @Bean
    @Qualifier("auxmoneyWebClient")
    public WebClient auxmoneyWebClient() {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient ->
                        tcpClient.bootstrap(bootstrap ->
                                BootstrapHandlers.updateLogSupport(bootstrap, new HttpLJettyLoggingHandler(HttpClient.class))));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(auxmoneyConfig.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader("urlkey", auxmoneyConfig.getUrlkey())
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("Auxmoney"))
                .build();
    }

    @Bean
    @Qualifier("consorsWebClient")
    public WebClient consorsWebClient() {
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(consorsPropertiesConfig.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("Consors"))
                .build();
    }

    @Bean
    @Qualifier("aionWebClient")
    public WebClient aionWebClient() {

        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(aionPropertiesConfig.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Customer-Id", aionPropertiesConfig.getCustomerId())
                .defaultHeader("Api-Gateway-Key", aionPropertiesConfig.getGatewayKey())
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("Aion"))
                .build();
    }

    @Bean
    @Qualifier("postbankWebClient")
    public WebClient postbankWebClient() {
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(postbankPropertiesConfig.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "text/xml;charset=UTF-8")
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("Postbank"))
                .build();
    }

    @Bean
    @Qualifier("ftsWebClient")
    public WebClient ftsWebClient() {

        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(ftsConfig.getEndPointUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(("api" + ":" + ftsConfig.getApiKey()).getBytes()))
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("Fts"))
                .build();
    }

    @Bean
    @Qualifier("santanderWebIdCoreClient")
    public WebClient santanderWebIdCoreClient() {
        return WebClient.builder()
                .baseUrl(santanderConfig.getWebIdHost())
                .defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(santanderConfig.getWebIdAuthToken()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("Santander"))
                .build();
    }

    @Bean
    @Qualifier("segmentPersonasClient")
    public WebClient segmentPersonasClient() {
        DefaultUriBuilderFactory factoryWithDisabledEncoding = new DefaultUriBuilderFactory(segmentPropertiesConfig.getPersonasHost());
        factoryWithDisabledEncoding.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        return WebClient.builder()
                .baseUrl(segmentPropertiesConfig.getPersonasHost())
                .uriBuilderFactory(factoryWithDisabledEncoding)
                .filter(logRequest.logRequest())
                .exchangeStrategies(exchangeStrategies)
                .filter(logResponseFilter.logResponseStatus("SegmentPersonas"))
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setBasicAuth(segmentPropertiesConfig.getPersonasToken());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }

    @Bean
    @Qualifier("solarisWebClient")
    public WebClient solarisWebClient() {
        return WebClient.builder()
                //  .exchangeStrategies(exchangeStrategies)
                .baseUrl(solarisPropertiesConfig.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("Solaris"))
                .build();
    }

    @Bean
    @Qualifier("solarisWebClientBuilder")
    public WebClient.Builder solarisWebClientBuilder() {
        return WebClient.builder()
                .baseUrl(solarisPropertiesConfig.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("Solaris"));
    }

    @Bean
    @Qualifier("intercomWebClient")
    public WebClient intercomWebClient() {
        return WebClient.builder()
                .baseUrl(customerSupportConfig.getHost())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest.logRequest())
                .filter(logResponseFilter.logResponseStatus("IntercomWebClient"))
                .build();
    }
}
