package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.ContactData;
import de.joonko.loan.offer.domain.Email;
import de.joonko.loan.offer.domain.ZipCode;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.ContactAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;


class ContactAddressMapperTest extends BaseMapperTest {

    @Autowired
    private ContactAddressMapper mapper;


    private ContactAddress contactAddress;

    @BeforeEach
    void setUp() {
        ContactData contactData = new ContactData(
                "city",
                "streetname",
                "22a",
                new ZipCode("12345"),
                LocalDate.of(2003, Month.MARCH, 17), null, new Email("someOne@joonko.io"), "491748273421011"
        );
        contactAddress = mapper.toLoanProviderSubscriber(contactData);
    }

    @Test
    void zipcode_code_maps_to_zipcode() {
        assertThat(contactAddress.getZipcode()).isEqualTo("12345");
    }

    @Test
    void email_string_maps_to_email() {
        assertThat(contactAddress.getEmail()).isEqualTo("someOne@joonko.io");
    }

    @Test
    void city_maps_to_city() {
        assertThat(contactAddress.getCity()).isEqualTo("city");
    }

    @Test
    void city_maps_to_normalized_and_trimmedcity() {
        // given
        ContactData contactData = new ContactData(
                "   k.öl,N   ",
                "streetname",
                "22a",
                new ZipCode("12345"),
                LocalDate.of(2003, Month.MARCH, 17), null, new Email("someOne@joonko.io"), "491748273421011"
        );

        // when
        final var result = mapper.toLoanProviderSubscriber(contactData);

        // then
        assertThat(result.getCity()).isEqualTo("kolN");
    }

    @Test
    void street_name_maps_to_street() {
        assertThat(contactAddress.getStreet()).isEqualTo("streetname");
    }

    @Test
    void telephone_maps_telephone_landline_to_null() {
        assertThat(contactAddress.getTelephoneLandline()).isNull();
    }

    @Test
    @DisplayName("Should append 0 to consors mobile")
    void mobile_maps_to_mobile() {
        assertThat(contactAddress.getTelephoneMobile()).isEqualTo("0491748273421011");
    }

    @Test
    @DisplayName("validFrom is mapped to YYYY-MM")
    void valid_from_is_mapped_to_YYYY_MM() {
        assertThat(contactAddress.getValidFrom()).isEqualTo("2003-03");
    }
}
