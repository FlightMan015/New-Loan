package de.joonko.loan.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Slf4j
public class HttpUtil {

    private HttpUtil() {
    }

    public static String extractClientIPFromRequest(final ServerHttpRequest request) {
        return extractInetAddressFromRequest(request).map(InetAddress::getHostAddress).orElse("");
    }

    public static Optional<InetAddress> extractInetAddressFromRequest(final ServerHttpRequest request) {
        return ofNullable(request)
                .map(ServerHttpRequest::getRemoteAddress)
                .map(InetSocketAddress::getAddress);
    }

    public static String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
