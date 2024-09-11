package de.joonko.loan.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class UsersFtsDeletionConfig {

    @Value("${user.fts.deletion.days-ago:60}")
    private int daysAgo;
}
