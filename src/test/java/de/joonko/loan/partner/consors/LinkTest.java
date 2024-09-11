package de.joonko.loan.partner.consors;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class LinkTest {

    @Nested
    class Method {
        @Test
        void get() {
            Link link = new Link("name", "/path", "GET");
            assertThat(link.getMethod()).isEqualTo(HttpMethod.GET);
        }

        @Test
        void post() {
            Link link = new Link("name", "/path", "POST");
            assertThat(link.getMethod()).isEqualTo(HttpMethod.POST);
        }

        @Test
        void put() {
            Link link = new Link("name", "/path", "PUT");
            assertThat(link.getMethod()).isEqualTo(HttpMethod.PUT);
        }

        @Test
        void delete() {
            Link link = new Link("name", "/path", "DELETE");
            assertThat(link.getMethod()).isEqualTo(HttpMethod.DELETE);
        }

        @Test
        void patch() {
            Link link = new Link("name", "/path", "PATCH");
            assertThat(link.getMethod()).isEqualTo(HttpMethod.PATCH);
        }
    }

    @Nested
    class Uri {
        @Test
        void concats_base_with_href() {
            Link link = new Link("https://api.example/base", "/path", "PATCH");
            assertThat(link.getUri()).isEqualTo(URI.create("https://api.example/base/path"));
        }

        @Test
        void is_normalized() {
            Link link = new Link("https://api.example/base/", "/path", "PATCH");
            assertThat(link.getUri()).isEqualTo(URI.create("https://api.example/base/path"));
        }
    }


}