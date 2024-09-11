package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.offer.api.*;
import de.joonko.loan.integrations.model.DistributionChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserPersonalDataMergerTest {

    private UserPersonalDataMerger merger;

    @BeforeEach
    void setUp() {
        merger = new UserPersonalDataMerger();
    }

    @Test
    void mergeFromProviders() {
        // given
        UserPersonalData withLowerPriority = getWithLowerPriorityProvider();
        UserPersonalData withHigherPriority = getWithHigherPriorityProvider();


        // when
        var merged = merger.merge(List.of(withHigherPriority, withLowerPriority));

        // then
        assertAll(
                () -> assertFalse(merged.getVerifiedViaBankAccount()),
                () -> assertEquals(DistributionChannel.BONIFY, merged.getDistributionChannel()),

                () -> assertEquals(Gender.MALE, merged.getPersonalDetails().getGender()),
                () -> assertEquals("Joonko", merged.getPersonalDetails().getFirstName()),
                () -> assertEquals("Finleap", merged.getPersonalDetails().getLastName()),
                () -> assertEquals(FamilyStatus.MARRIED, merged.getPersonalDetails().getFamilyStatus()),
                () -> assertEquals(LocalDate.now().minusYears(25).minusDays(150), merged.getPersonalDetails().getBirthDate()),
                () -> assertEquals(Nationality.DE, merged.getPersonalDetails().getNationality()),
                () -> assertEquals("SOMEWHERE", merged.getPersonalDetails().getPlaceOfBirth()),
                () -> assertEquals(0, merged.getPersonalDetails().getNumberOfChildren()),
                () -> assertEquals(HousingType.OWNER, merged.getPersonalDetails().getHousingType()),
                () -> assertEquals(1, merged.getPersonalDetails().getNumberOfCreditCard()),

                () -> assertEquals("Street Name", merged.getContactData().getStreetName()),
                () -> assertEquals("12", merged.getContactData().getHouseNumber()),
                () -> assertEquals("12345", merged.getContactData().getPostCode()),
                () -> assertEquals("City", merged.getContactData().getCity()),
                () -> assertEquals(ShortDate.builder().year(2010).month(1).build(), merged.getContactData().getLivingSince()),
                () -> assertEquals("someone@joonko.io", merged.getContactData().getEmail()),
                () -> assertEquals("491748273421011", merged.getContactData().getMobile()),

                () -> assertNull(merged.getEmploymentDetails()),

                () -> assertNull(merged.getIncome()),

                () -> assertNull(merged.getExpenses().getMortgages()),
                () -> assertEquals(0.0, merged.getExpenses().getVehicleInsurance()),

                () -> assertEquals(522, merged.getCreditDetails().getBonimaScore()),
                () -> assertEquals("B", merged.getCreditDetails().getEstimatedSchufaClass()),
                () -> assertEquals(0.25, merged.getCreditDetails().getProbabilityOfDefault()),

                () -> assertEquals(withLowerPriority.getTenantId(), merged.getTenantId())
        );
    }

    private UserPersonalData getWithLowerPriorityProvider() {
        return UserPersonalData.builder()
                .userUuid("userId")
                .bonifyUserId(123L)
                .distributionChannel(DistributionChannel.BONIFY)
                .personalDetails(PersonalDetails.builder()
                        .gender(Gender.MALE)
                        .lastName("Fin")
                        .familyStatus(FamilyStatus.MARRIED)
                        .birthDate(LocalDate.now()
                                .minusYears(25)
                                .minusDays(150))
                        .nationality(Nationality.DE)
                        .placeOfBirth("SOMEWHERE")
                        .numberOfChildren(0)
                        .housingType(HousingType.OWNER)
                        .numberOfCreditCard(1)
                        .build())
                .contactData(ContactData.builder()
                        .streetName("Street Name")
                        .houseNumber("12")
                        .postCode("12345")
                        .city("City")
                        .livingSince(ShortDate.builder()
                                .month(1)
                                .year(2010)
                                .build())
                        .email("someone@joonko.io")
                        .mobile("491748273421011")
                        .build())
                .expenses(Expenses.builder()
                        .vehicleInsurance(0.0)
                        .build())
                .creditDetails(CreditDetails.builder()
                        .bonimaScore(522)
                        .estimatedSchufaClass("B")
                        .probabilityOfDefault(0.25)
                        .build())
                .tenantId(UUID.randomUUID().toString())
                .build();
    }

    private UserPersonalData getWithHigherPriorityProvider() {
        return UserPersonalData.builder()
                .userUuid("userId")
                .bonifyUserId(123L)
                .personalDetails(PersonalDetails.builder()
                        .gender(Gender.MALE)
                        .firstName("Joonko")
                        .lastName("Finleap")
                        .familyStatus(FamilyStatus.MARRIED)
                        .birthDate(LocalDate.now()
                                .minusYears(25)
                                .minusDays(150))
                        .nationality(Nationality.DE)
                        .placeOfBirth("SOMEWHERE")
                        .numberOfChildren(0)
                        .numberOfCreditCard(1)
                        .build())
                .build();
    }
}
