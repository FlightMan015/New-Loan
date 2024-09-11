package de.joonko.loan.integrations.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.fusionauth.client.FusionAuthClient;

@Configuration
public class FusionAuthConfig {

    @Value("${fusionAuth.api.key}")
    private String fusionAuthApiKey;
    @Value("${fusionAuth.baseUrl}")
    private String baseUrl;


    @Bean
    public FusionAuthClient fusionAuthClient() {
        return new FusionAuthClient(fusionAuthApiKey, baseUrl);
    }
}
