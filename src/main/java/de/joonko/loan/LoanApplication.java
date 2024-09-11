package de.joonko.loan;


import de.joonko.loan.config.AuxmoneyConfig;
import de.joonko.loan.config.CustomerSupportConfig;
import de.joonko.loan.config.MongoDbConfig;
import de.joonko.loan.config.RetryConfigProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class})
@EnableWebFlux
@EnableMongoRepositories
@EnableConfigurationProperties({AuxmoneyConfig.class, RetryConfigProperties.class, MongoDbConfig.class, CustomerSupportConfig.class})
@EnableFeignClients
@EnableAspectJAutoProxy
public class LoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanApplication.class, args);
    }
}
