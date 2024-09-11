package de.joonko.loan.partner.consors;

import org.springframework.http.HttpMethod;

import java.net.URI;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
class Link {

    private URI uri;
    private HttpMethod method;

    Link(String base, String href, String method) {
        this.uri = URI.create(base + href).normalize();
        this.method = HttpMethod.valueOf(method);
    }
}
