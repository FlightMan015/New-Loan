package de.joonko.loan.util;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtension.class)
class ClientIPServiceTest {

    @SneakyThrows
    @Test
    void getCountry_success() {
        // given
        String ip = "3.124.121.47";
        InetAddress ipAddress = InetAddress.getByName(ip);

        // when
        String country = ClientIPService.getCountry(ipAddress);

        // then
        assertEquals("DE", country);
    }

    @SneakyThrows
    @Test
    void getCountry_noIpFound() {
        // given
        String ip = "0.0.0.0";
        InetAddress ipAddress = InetAddress.getByName(ip);

        // when
        String country = ClientIPService.getCountry(ipAddress);

        // then
        assertEquals("", country);
    }

    @SneakyThrows
    @Test
    void getCountry_ipNullCase() {
        // when
        String country = ClientIPService.getCountry(null);

        // then
        assertEquals("", country);
    }

}