package de.joonko.loan.config.converter;

import de.joonko.loan.reporting.api.model.StringToResponseFormatConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class RequestConverterConfig implements WebFluxConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToResponseFormatConverter());
    }
}
