package de.joonko.loan.integrations.segment;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "segment")
@Data
@Validated
public class SegmentPropertiesConfig {

    @Value("${segment.personas.host}")
    @NotBlank
    private String personasHost;

    @Value("${segment.personas.spaceId}")
    @NotBlank
    private String personasSpaceId;

    @Value("${segment.personas.token}")
    @NotBlank
    private String personasToken;
}
