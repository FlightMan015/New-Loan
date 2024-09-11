package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.EmploymentDetails;
import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;


class SantanderGetOfferCurrentEmploymentDetailRequestMapperTest extends BaseMapperTest {

    @Autowired
    SantanderGetOfferCurrentEmploymentDetailRequestMapper santanderGetOfferCurrentEmploymentDetailRequestMapper;


    @Nested
    class employmentType {
        @Test
        void regular_employee(@Random EmploymentDetails employmentDetails) {
            employmentDetails.setEmploymentType(EmploymentType.REGULAR_EMPLOYED);
            ScbCapsBcoWSStub.BeschaeftigungsverhaeltnisXO beschaeftigungsverhaeltnisXO = santanderGetOfferCurrentEmploymentDetailRequestMapper.toAktuellesBV(employmentDetails);
            assertEquals(beschaeftigungsverhaeltnisXO.getBerufsgruppe(), ScbCapsBcoWSStub.BerufType.ANGESTELLTER);
        }

        @Test
        void others(@Random EmploymentDetails employmentDetails) {
            employmentDetails.setEmploymentType(EmploymentType.OTHER);
            ScbCapsBcoWSStub.BeschaeftigungsverhaeltnisXO beschaeftigungsverhaeltnisXO = santanderGetOfferCurrentEmploymentDetailRequestMapper.toAktuellesBV(employmentDetails);
            assertEquals(beschaeftigungsverhaeltnisXO.getBerufsgruppe(), ScbCapsBcoWSStub.BerufType.UNBEKANNT);
        }
    }

    @Test
    void employerName(@Random EmploymentDetails employmentDetails) {
        // given
        // when
        ScbCapsBcoWSStub.BeschaeftigungsverhaeltnisXO result = santanderGetOfferCurrentEmploymentDetailRequestMapper.toAktuellesBV(employmentDetails);

        // then
        assertAll(
                () -> assertEquals(employmentDetails.getEmployerName(), result.getArbeitgeberName()),
                () -> assertEquals(employmentDetails.getHouseNumber(), result.getArbeitgeberHausnr()),
                () -> assertEquals(employmentDetails.getStreetName(), result.getArbeitgeberStrasse()),
                () -> assertEquals(employmentDetails.getZipCode().getCode(), result.getArbeitgeberPlz()),
                () -> assertEquals(employmentDetails.getCity(), result.getArbeitgeberOrt()),
                () -> assertNotNull(result.getBeschaeftigtSeit()),
                () -> assertNotNull(result.getBeschaeftigtBis()),
                () -> assertNotNull(result.getBefristetBis())
        );
    }
}