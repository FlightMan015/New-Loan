package de.joonko.loan.filter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import java.security.Principal;

import reactor.core.publisher.Mono;


public class JWTEmailVerificationFilter implements org.springframework.web.server.WebFilter {

    private static final String EMAIL_VERIFIED = "email_verified";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain webFilterChain) {
        return exchange.getPrincipal()
                .filterWhen(this::userIsVerifiedByEmail)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new AccessDeniedException("User has not verified their email yet"))
                ))
                .flatMap(principal -> webFilterChain.filter(exchange));
    }

    private Mono<Boolean> userIsVerifiedByEmail(final Principal principal) {
        return Mono.just(Boolean.TRUE.equals(((JwtAuthenticationToken) principal).getTokenAttributes().get(EMAIL_VERIFIED)));
    }
}
