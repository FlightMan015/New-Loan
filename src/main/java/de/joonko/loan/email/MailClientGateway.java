package de.joonko.loan.email;

import de.joonko.loan.config.MailConfig;
import de.joonko.loan.email.model.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
@RequiredArgsConstructor
public class MailClientGateway {

    @Autowired
    private RestTemplate restTemplate;
    private final MailConfig mailConfig;

    public void sendEmailWithAttachment(Email email) {
        HttpEntity<Email> emailRequest = new HttpEntity<>(email);
        restTemplate
                .exchange(mailConfig.getHost() + mailConfig.getEmailEndpoint(), HttpMethod.POST, emailRequest, Email.class);
    }

}
