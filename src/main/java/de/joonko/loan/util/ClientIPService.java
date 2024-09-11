package de.joonko.loan.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;

import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class ClientIPService {

    private ClientIPService() {
    }

    private static final String CLIENT_IP_TO_COUNTRY_MAPPING_LOCATION = "location/GeoLite2-Country.mmdb";

    public static String getCountry(final InetAddress ipAddress) {
        if (ipAddress == null) {
            return "";
        }
        try {
            final var inputStream = new ClassPathResource(CLIENT_IP_TO_COUNTRY_MAPPING_LOCATION).getInputStream();
            final var dbReader = new DatabaseReader.Builder(inputStream).build();
            final var response = dbReader.country(ipAddress);
            return ofNullable(response)
                    .map(CountryResponse::getCountry)
                    .map(Country::getIsoCode)
                    .orElse("");
        } catch (IOException | GeoIp2Exception e) {
            log.warn("Could not map clientIp to country for IP - {}, exception cause - {}, exception message - {}", ipAddress.getHostAddress(), e.getCause(), e.getMessage());
        }
        return "";
    }

}
