package de.joonko.loan.config.security;

import de.joonko.loan.filter.HTTPLoggingFilter;
import de.joonko.loan.filter.JWTEmailVerificationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityWebFilterChain apiHttpSecurity(ServerHttpSecurity http) {
        return http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/v1/**"))
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .addFilterAt(new JWTEmailVerificationFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .oauth2ResourceServer(resourceServerSpec -> resourceServerSpec.jwt(withDefaults()))
                .build();
    }

    @Bean
    SecurityWebFilterChain webHttpSecurity(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, ACTUATOR_WHITELIST).permitAll()
                        .pathMatchers(HttpMethod.POST, IDNOW_AUTH_WHITELIST).permitAll()
                        .pathMatchers(HttpMethod.POST, AION_AUTH_WHITELIST).permitAll()
                        .pathMatchers(HttpMethod.POST, POSTBANK_AUTH_WHITELIST).hasRole(SecurityRole.PB_USER.name())
                        .pathMatchers(HttpMethod.POST, ADMIN_WHITELIST).hasRole(SecurityRole.ADMIN.name())
                        .pathMatchers(HttpMethod.DELETE, ADMIN_WHITELIST).hasRole(SecurityRole.ADMIN.name())
                        .pathMatchers(HttpMethod.GET, INTERNAL_WHITELIST).hasRole(SecurityRole.INTERNAL.name())
                        .pathMatchers(HttpMethod.GET, REPORTING_WHITELIST).hasRole(SecurityRole.REPORTING.name())
                        .anyExchange().authenticated())
                .httpBasic().and()
                .csrf().disable()
                .formLogin().disable()
                .addFilterAt(new HTTPLoggingFilter(), SecurityWebFiltersOrder.LAST)
                .build();
    }

    private static final String[] REPORTING_WHITELIST = {
            "/loan/distribution-channel/**"
    };

    private static final String[] ADMIN_WHITELIST = {
            "/admin/**"
    };

    private static final String[] INTERNAL_WHITELIST = {
            "/internal/**"
    };

    private static final String[] ACTUATOR_WHITELIST = {
            "/actuator/health",
            "/actuator/info",
            "/actuator/prometheus"
    };

    //Will be removed after https://bonify.atlassian.net/browse/B2B-613
    private static final String[] IDNOW_AUTH_WHITELIST = {
            "/loan/id-now/joonkosolaris/identification-notification",
            "/loan/id-now/identification-notification"
    };

    private static final String[] AION_AUTH_WHITELIST = {
            "/loan/aion/webhook"
    };

    private static final String[] POSTBANK_AUTH_WHITELIST = {
            "/loan/postbank/offers-notification"
    };

}
