package de.joonko.loan.filter;

import de.joonko.loan.common.AppConstants;
import de.joonko.loan.common.utils.CommonUtils;

import org.slf4j.MDC;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;


public class HTTPLoggingFilter implements org.springframework.web.server.WebFilter {

    private final String HTTP_KIBANA_TRACE_RESPONSE_HEADER_NAME = "kibana-track-id";
    private final String MDC_TOKEN_KEY = AppConstants.SLF4J_MDC_LOGGING_NAME;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final String token = CommonUtils.generateUUID("http");
        MDC.put(MDC_TOKEN_KEY, token);
        exchange.getResponse().getHeaders().add(HTTP_KIBANA_TRACE_RESPONSE_HEADER_NAME, token);
        return chain.filter(exchange).map(voidMono -> {
            MDC.remove(MDC_TOKEN_KEY);
            return voidMono;
        });


    }
}
