package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.BeforeEach;
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

class CreditPlusDebtorPersonalDataMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusDebtorPersonalDataMapper mapper;

    @Random
    LoanDemand loanDemand;

    EfinComparerServiceStub.PersonalData personalData = new EfinComparerServiceStub.PersonalData();


    @BeforeEach
    void setUp() {
        personalData = mapper.toPersonalData(loanDemand);
    }

    @Test
    void toPersonalData() {
        assertNotNull(personalData);
    }


    @ParameterizedTest(name = "{0} maps to {1}")
    @MethodSource("toSalutationProvider")
    void toSalutation(Gender gender, String creditPlusGender) {
        loanDemand.getPersonalDetails().setGender(gender);
        personalData = mapper.toPersonalData(loanDemand);

        assertEquals(personalData.getSalutation(), Integer.valueOf(creditPlusGender));
    }

    static Stream<Arguments> toSalutationProvider() {
        return Stream.of(
                arguments(Gender.MALE, CreditPlusDefaults.MALE),
                arguments(Gender.FEMALE, CreditPlusDefaults.FEMALE)
        );
    }

    @DisplayName("All family status mapped correctly to the Credit+")
    @ParameterizedTest(name = "{0} maps to {1}")
    @MethodSource("toMaritalStateProvider")
    void toMaritalState(FamilyStatus status, int creditPlusMap) {
        loanDemand.getPersonalDetails().setFamilyStatus(status);
        personalData = mapper.toPersonalData(loanDemand);
        assertEquals(creditPlusMap, personalData.getMaritalState());
        assertEquals(status, loanDemand.getPersonalDetails().getFamilyStatus());
    }

    static Stream<Arguments> toMaritalStateProvider() {
        return Stream.of(
                arguments(FamilyStatus.SINGLE, 1),
                arguments(FamilyStatus.LIVING_IN_LONGTERM_RELATIONSHIP, 1),
                arguments(FamilyStatus.MARRIED, 2),
                arguments(FamilyStatus.DIVORCED, 3),
                arguments(FamilyStatus.WIDOWED, 4),
                arguments(FamilyStatus.LIVING_SEPARATELY, 5)
        );
    }

    @Test
    void toForeName() {
        assertEquals(personalData.getForeName(), loanDemand.getPersonalDetails().getFirstName());
    }

    @Test
    void toLastName() {
        assertEquals(personalData.getLastName(), loanDemand.getPersonalDetails().getLastName());
    }

    @Test
    void toPlaceOfBirth() {
        assertEquals(personalData.getPlaceOfBirth(), loanDemand.getPersonalDetails().getPlaceOfBirth());
    }

    @Test
    void toDateOfBirth() {
        assertEquals(personalData.getDateOfBirth(), localDateToCalendar(loanDemand.getPersonalDetails().getBirthDate()));
    }

    public Calendar localDateToCalendar(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        return calendar;
    }

    @Test
    void toCountryOfBirth() {
        assertNull(personalData.getCountryOfBirth());
    }

    @Test
    void toStreet() {
        assertEquals(personalData.getStreet(), loanDemand.getContactData().getStreetName());
    }

    @Test
    void toPostalCode() {
        assertEquals(personalData.getPostalCode(), loanDemand.getContactData().getZipCode().getCode());
    }

    @Test
    void toCity() {
        assertEquals(personalData.getCity(), loanDemand.getContactData().getCity());
    }

    @Test
    void tolastRemoval() {
        assertEquals(personalData.getLastRemoval(), localDateToCalendar(loanDemand.getContactData().getLivingSince()));
    }

    @Test
    void toNumberOfChildren() {
        assertEquals(personalData.getNumberOfChildren(), loanDemand.getPersonalDetails().getNumberOfChildren());
    }

    @Test
    void toEmail() {
        assertEquals(personalData.getEmail(), loanDemand.getContactData().getEmail().getEmailString());
    }

    @Test
    void toIdentificationType() {
        assertNull(personalData.getIdentificationType());
    }

    @Test
    void toNationality() {
        assertEquals(personalData.getNationality(), loanDemand.getPersonalDetails().getNationality().getCountryCode().getAlpha2());
    }

    @Test
    void toPreCity() {
        assertEquals(personalData.getPreCity(), loanDemand.getContactData().getPreviousAddress().getCity());
    }

    @Test
    void toPrePostalCode() {
        assertEquals(personalData.getPrePostalCode(), loanDemand.getContactData().getPreviousAddress().getPostCode());
    }

    @Test
    void toPreStreet() {
        assertEquals(personalData.getPreStreet(), loanDemand.getContactData().getPreviousAddress().getStreet());
    }

    @Test
    void toPreCountry() {
        assertEquals(personalData.getPreCountry(), loanDemand.getContactData().getPreviousAddress().getCountry().getCountryCode().getAlpha2());
    }

    @Test
    void toPhoneMobileArea() {
        loanDemand.getContactData().setMobile("012345678901");
        personalData = mapper.toPersonalData(loanDemand);
        assertEquals(personalData.getPhoneMobile().getAreaCode(), "0".concat(loanDemand.getContactData().getMobile().substring(2, 5)));
    }

    @Test
    void toPhoneMobileLocal() {
        loanDemand.getContactData().setMobile("012345678901");
        personalData = mapper.toPersonalData(loanDemand);
        assertEquals(personalData.getPhoneMobile().getLocalNumber(), loanDemand.getContactData().getMobile().substring(5));
    }
}