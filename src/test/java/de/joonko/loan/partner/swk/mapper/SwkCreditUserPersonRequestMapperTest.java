package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
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

class SwkCreditUserPersonRequestMapperTest extends BaseMapperTest {

    @Autowired
    SwkCreditUserPersonRequestMapper mapper;

    @Random
    LoanDemand loanDemand;

    @Test
    void toPerson() {
        CreditApplicationServiceStub.Person person = mapper.toPerson(loanDemand);
        assertNotNull(person);
    }

    @Test
    void toBirthDate() {
        CreditApplicationServiceStub.Person person = mapper.toPerson(loanDemand);
        LocalDate localDateBdate = loanDemand.getPersonalDetails().getBirthDate();
        Calendar calendarBdate = Calendar.getInstance();
        calendarBdate.clear();
        calendarBdate.set(localDateBdate.getYear(), localDateBdate.getMonthValue() - 1, localDateBdate.getDayOfMonth());
        assertEquals(person.getBirthDate(), calendarBdate);
    }

    @Test
    void toBirthPlace() {
        CreditApplicationServiceStub.Person person = mapper.toPerson(loanDemand);
        assertEquals(person.getBirthPlace(), loanDemand.getPersonalDetails().getPlaceOfBirth());
    }

    @Test
    void toEmail() {
        CreditApplicationServiceStub.Person person = mapper.toPerson(loanDemand);
        assertEquals(person.getEmail(), loanDemand.getContactData().getEmail().getEmailString());
    }

    @Test
    void toFirstNameAndLastName() {
        CreditApplicationServiceStub.Person person = mapper.toPerson(loanDemand);
        assertEquals(person.getFirstName(), loanDemand.getPersonalDetails().getFirstName());
        assertEquals(person.getLastName(), loanDemand.getPersonalDetails().getLastName());
    }

    @Test
    void toMobile() {
        CreditApplicationServiceStub.Person person = mapper.toPerson(loanDemand);
        assertEquals(person.getMobile(), loanDemand.getContactData().getMobile());

    }

    @Test
    void toPhone() {
        CreditApplicationServiceStub.Person person = mapper.toPerson(loanDemand);
        assertNull(person.getPhone());
    }

    @Test
    void toHomeAddress() {
        CreditApplicationServiceStub.Person person = mapper.toPerson(loanDemand);
        assertEquals(person.getHomeAddress().getStreet(), loanDemand.getContactData().getStreetName());
        assertEquals(person.getHomeAddress().getHousenumber(), loanDemand.getContactData().getStreetNumber());
        assertEquals(person.getHomeAddress().getCity(), loanDemand.getContactData().getCity());
        assertEquals(person.getHomeAddress().getZipcode(), loanDemand.getContactData().getZipCode().getCode());
    }


    @ParameterizedTest(name = "{0} maps to {1}")
    @MethodSource("genderProvider")
    void toGender(Gender gender, String swkMap) {
        loanDemand.getPersonalDetails().setGender(gender);
        CreditApplicationServiceStub.Person person = mapper.toPerson(loanDemand);
        assertEquals(swkMap, person.getGender());
    }

    static Stream<Arguments> genderProvider() {
        return Stream.of(
                arguments(Gender.MALE, "M"),
                arguments(Gender.FEMALE, "W")
        );
    }
}