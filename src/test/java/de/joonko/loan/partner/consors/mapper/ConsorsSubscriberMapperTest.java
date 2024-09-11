package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.offer.domain.*;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.HousingSituation;
import de.joonko.loan.partner.consors.model.Profession;
import de.joonko.loan.partner.consors.model.RolePlaying;
import de.joonko.loan.partner.consors.model.Subscriber;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConsorsSubscriberMapperTest extends BaseMapperTest {

    @Autowired
    ConsorsSubscriberMapper mapper;

    @Autowired
    GetOffersConfigurations getOffersConfigurations;

    @Random
    private LoanDemand loanDemand;

    @Test
    void role_playing_is_always_MAIN() {
        loanDemand.getPersonalDetails()
                .setMainEarner(DomainDefault.MAIN_EARNER);
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertThat(subscriber.getRolePlaying()).isEqualTo(RolePlaying.MAIN);
    }

    @Test
    void contact_address_is_present() {
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertNotNull(subscriber.getContactAddress());
    }

    @Test
    void white_spaces_are_trimmed() {
        loanDemand.getPersonalDetails().setLastName(" last name ");
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertNotNull(subscriber.getLastName());
        assertThat(subscriber.getLastName()).isEqualTo("last name");
    }

    @Test
    void employment_details_are_present() {
        loanDemand.getEmploymentDetails().setEmploymentType(EmploymentType.REGULAR_EMPLOYED);
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertNotNull(subscriber.getEmploymentDetails());
        assertThat(subscriber.getEmploymentDetails().getProfession()).isEqualTo(Profession.REGULAR_EMPLOYED);
    }

    @Test
    void birth_date_is_mapped_to_date_of_birth() {
        loanDemand.getPersonalDetails()
                .setBirthDate(LocalDate.of(1980, 3, 1));
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertThat(subscriber.getDateOfBirth()).isEqualTo(LocalDate.of(1980, 3, 1));
    }

    @Test
    void birth_place_is_always_mapped_to_berlin() {
        loanDemand.getPersonalDetails().setPlaceOfBirth("SOMEWEHERE");
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertThat(subscriber.getPlaceOfBirth()).isEqualTo("SOMEWEHERE");
    }

    @Test
    void birth_place_should_normalize() {
        loanDemand.getPersonalDetails().setPlaceOfBirth("Berlin, Germany.");
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertThat(subscriber.getPlaceOfBirth()).isEqualTo("Berlin Germany");
    }

    @Test
    void consents_schufa_call_allowed_is_always_true() {
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertNotNull(subscriber.getConsents());
    }

    @Test
    @DisplayName("Should return RENTER when HousingType is RENT")
    void housingSituationRenter() {
        loanDemand.getPersonalDetails()
                .setHousingType(HousingType.RENT);
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertNotNull(subscriber.getHousingSituation());
        assertEquals(HousingSituation.RENTER, subscriber.getHousingSituation());
    }

    @Test
    @DisplayName("Should return OWNER_WITH_MORTGAGE when HousingType is OWNER_WITH_MORTGAGE")
    void housingSituationOwnerWithMortgage() {
        loanDemand.getPersonalDetails()
                .setHousingType(HousingType.OWNER);
        loanDemand.getPersonalDetails().getFinance().getExpenses().setMortgages(BigDecimal.ONE);
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertNotNull(subscriber.getHousingSituation());
        assertEquals(HousingSituation.OWNER_WITH_MORTGAGE, subscriber.getHousingSituation());
    }

    @Test
    @DisplayName("Should return OWNER_WITHOUT_MORTGAGE when HousingType is OWNER_WITHOUT_MORTGAGE")
    void housingSituationOwnerWithoutMortgage() {
        loanDemand.getPersonalDetails()
                .setHousingType(HousingType.OWNER);
        loanDemand.getPersonalDetails().getFinance().getExpenses().setMortgages(BigDecimal.ZERO);
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertNotNull(subscriber.getHousingSituation());
        assertEquals(HousingSituation.OWNER_WITHOUT_MORTGAGE, subscriber.getHousingSituation());
    }

    @DisplayName("Should convert gender")
    @Test
    void gender() {
        Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
        assertNotNull(subscriber.getGender());

    }

    @Nested
    class NationalityTest {
        @Test
        @DisplayName("Should Convert nationality to Germany")
        void shouldConvertNationality(@Random LoanDemand loanDemand) {
            loanDemand.getPersonalDetails()
                    .setNationality(Nationality.DE);
            Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
            assertEquals("276", subscriber.getNationality());
        }

        @Test
        @DisplayName("Should pad zeroes")
        void shouldConvertISONationality(@Random LoanDemand loanDemand) {
            loanDemand.getPersonalDetails()
                    .setNationality(Nationality.BA);
            Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
            assertEquals("070", subscriber.getNationality());
        }

        @ParameterizedTest(name = "Should map nationality [{arguments}]")
        @EnumSource(Nationality.class)
        void allNationalityStatus(Nationality nationality, @Random LoanDemand loanDemand) {
            Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
            assertNotNull(subscriber.getNationality());
        }
    }

    @Nested
    class PreviousAddress {
        @Test
        @DisplayName("should map previousAddress")
        void previousAddress() {
            Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
            assertNotNull(subscriber.getPreviousAddress());
            assertNotNull(subscriber.getPreviousAddress()
                    .getCity());
        }

        @Test
        @DisplayName("should ignore previousAddress if null")
        void previousAddressNull() {
            loanDemand.getContactData()
                    .setPreviousAddress(null);
            Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
            assertNull(subscriber.getPreviousAddress());
        }

        @Test
        @DisplayName("should map taxId correctly")
        void taxIdMapping() {
            loanDemand.getPersonalDetails()
                    .setTaxId("12345");
            Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
            assertEquals("12345", subscriber.getGermanTaxIdentifier());
        }

        @Test
        @DisplayName("should map taxId correctly")
        void taxIdWillBeNullWhenNotPresent() {
            loanDemand.getPersonalDetails()
                    .setTaxId(null);
            Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
            assertNull(subscriber.getGermanTaxIdentifier());
        }
    }

    @Nested
    class Limit {
        @Test
        @DisplayName("should set limit to n% more of asked amount rounded to lower 500")
        void limit() {
            LoanDemand loanDemand = new LoanDemand(null,
                    6000,
                    null,
                    null,
                    null,
                    PersonalDetails.builder().housingType(HousingType.RENT).finance(new Finance(Income.builder().build(), Expenses.builder().acknowledgedRent(BigDecimal.valueOf(1000)).loanInstalments(BigDecimal.ZERO).build(), BigDecimal.TEN)).build(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    List.of(),
                    null);
            Subscriber subscriber = mapper.toLoanProviderSubscriber(loanDemand);
            assertAll(
                    () -> assertEquals(6500, (int) subscriber.getFinancialLimit()),
                    () -> assertEquals(1000, subscriber.getExpense().getWarmRent())
            );
        }
    }
}
