package de.joonko.loan.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AbandonedUsersConfig {

    @Value("${user.abandoned.days-ago:1}")
    private int daysAgo;
}
