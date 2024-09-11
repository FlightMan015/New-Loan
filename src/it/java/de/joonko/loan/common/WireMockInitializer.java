package de.joonko.loan.common;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        WireMockServer mockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
        mockServer.start();

        applicationContext.getBeanFactory().registerSingleton("wireMockServer", mockServer);

        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                mockServer.stop();
            }
        });

        changeUrlPaths(mockServer, applicationContext);
    }

    private void changeUrlPaths(WireMockServer mockServer, ConfigurableApplicationContext applicationContext) {
        String mockServerBaseUrl = "http://localhost:" + mockServer.port();

        TestPropertyValues.of(
                        "fts.endPointUrl=" + mockServerBaseUrl + "/fts/",
                        "fusionAuth.baseUrl=" + mockServerBaseUrl + "/fusionAuth/",
                        "idnow.host=" + mockServerBaseUrl + "/idnow",
                        "auxmoney.host=" + mockServerBaseUrl + "/auxmoney",
                        "consors.host=" + mockServerBaseUrl + "/consors",
                        "aion.host=" + mockServerBaseUrl + "/aion/",
                        "postbank.host=" + mockServerBaseUrl + "/postbank/",
                        "solaris.host=" + mockServerBaseUrl + "/solaris",
                        "swk.host=" + mockServerBaseUrl
                        )
                .applyTo(applicationContext);
    }
}