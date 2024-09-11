package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.EmploymentDetails;
import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SwkCreditUserEmploymentInformationRequestMapperTest extends BaseMapperTest {

    @Autowired
    SwkCreditUserEmploymentInformationRequestMapper mapper;

    @Random
    EmploymentDetails employmentDetails;


    @DisplayName("All occupation group mapped correctly to the SWK")
    @ParameterizedTest(name = "{0} maps to {1}")
    @MethodSource("occupationGroupProvider")
    void occupationGroup(EmploymentType employmentType, int swkMap) {
        employmentDetails.setEmploymentType(employmentType);
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertEquals(swkMap, employmentInformation.getOccupationGroup());
        assertEquals(employmentType, employmentDetails.getEmploymentType());
    }

    static Stream<Arguments> occupationGroupProvider() {
        return Stream.of(
                arguments(EmploymentType.REGULAR_EMPLOYED, 3),
                arguments(EmploymentType.OTHER, 21)
        );
    }

    @Test
    void employedSinceDate() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertEquals(employmentInformation.getEmployedSinceDate(), localDateToCalendar(employmentDetails.getEmploymentSince()));
    }

    @Test
    void temporaryUntil() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertEquals(employmentInformation.getTemporaryUntil(), localDateToCalendar(employmentDetails.getProfessionEndDate()));
    }

    @Test
    void temporaryFalse() {
        employmentDetails.setProfessionEndDate(null);
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertFalse(employmentInformation.getTemporary());
    }

    @Test
    void temporaryTrue() {
        employmentDetails.setProfessionEndDate(LocalDate.now());
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertTrue(employmentInformation.getTemporary());
    }

    @Test
    void employerName() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertEquals(employmentInformation.getEmployerName(), employmentDetails.getEmployerName());
    }

    @Test
    void employerAddressStreet() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertEquals(employmentInformation.getEmployerAddress().getStreet(), employmentDetails.getStreetName());
    }

    @Test
    void employerAddressHousenumber() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertEquals(employmentDetails.getHouseNumber(), employmentInformation.getEmployerAddress().getHousenumber());
    }

    @Test
    void employerAddressZipCode() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertEquals(employmentInformation.getEmployerAddress().getZipcode(), employmentDetails.getZipCode().getCode());
    }

    @Test
    void employerAddressCity() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertEquals(employmentInformation.getEmployerAddress().getCity(), employmentDetails.getCity());
    }

    @Test
    void employerChangePlanned() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertFalse(employmentInformation.getEmployerChangePlanned());
    }

    @Test
    void employerPhone() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertNull(employmentInformation.getEmployerPhone());
    }

    @Test
    void inProbationaryPeriod() {
        CreditApplicationServiceStub.EmploymentInformation employmentInformation = mapper.toEmploymentInformation(employmentDetails);
        assertFalse(employmentInformation.getInProbationaryPeriod());
    }

    public Calendar localDateToCalendar(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        return calendar;
    }
}