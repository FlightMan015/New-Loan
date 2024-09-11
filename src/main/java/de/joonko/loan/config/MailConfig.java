package de.joonko.loan.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
@Slf4j
@ConfigurationProperties(prefix = "app.mail")
@RequiredArgsConstructor
@Getter
public class MailConfig {

    @Value("${app.mail.host}")
    private String host;

    @Value("${app.mail.sendemail}")
    private String emailEndpoint;

    @Value("${app.mail.fromaddress}")
    private String fromAddress;

    @Value("${app.mail.ccAddress}")
    private String ccAddress;


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
