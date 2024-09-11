package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.*;
import de.joonko.loan.offer.api.model.UserPersonalDetails;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.user.service.persistence.domain.ConsentData;
import de.joonko.loan.user.service.persistence.domain.ConsentState;
import de.joonko.loan.user.service.persistence.domain.ConsentType;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(RandomBeansExtension.class)
class UserPersonalInformationMapperTest {

    private UserPersonalInformationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserPersonalInformationMapperImpl();
    }

    @Test
    void mapFromUserInput_mapsAllFieldsProperly(@Random String userId, @Random UserPersonalDetails userPersonalDetails) {
        // when
        final var mapped = mapper.mapFromUserInput(userId, userPersonalDetails);

        // then
        assertAll(
                () -> assertEquals(userId, mapped.getUserUuid()),
                () -> assertEquals(userPersonalDetails.getPersonalDetails(), mapped.getPersonalDetails()),
                () -> assertEquals(userPersonalDetails.getEmploymentDetails(), mapped.getEmploymentDetails()),
                () -> assertEquals(userPersonalDetails.getContactData(), mapped.getContactData()),
                () -> assertEquals(userPersonalDetails.getIncome(), mapped.getIncome()),
                () -> assertEquals(userPersonalDetails.getExpenses(), mapped.getExpenses()),
                () -> assertNull(mapped.getBonifyUserId()),
                () -> assertNull(mapped.getVerifiedViaBankAccount()),
                () -> assertNull(mapped.getDistributionChannel())
        );
    }

    @Test
    void should_map_user_personal_information() {
        // given
        UserPersonalDetails userPersonalDetails = UserPersonalDetails.builder()
                .personalDetails(PersonalDetails.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .gender(Gender.MALE)
                        .birthDate(LocalDate.now().minusYears(30))
                        .nationality(Nationality.DE)
                        .placeOfBirth("placeOfBirth")
                        .familyStatus(FamilyStatus.SINGLE)
                        .numberOfChildren(9)
                        .build())
                .contactData(ContactData.builder()
                        .email("email@com")
                        .city("addressCity")
                        .houseNumber("addressHouseNumber")
                        .streetName("addressStreet")
                        .postCode("addressZipCode")
                        .mobile("49123456789")
                        .build())
                .build();
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID("userId");

        // when
        UserPersonalInformationStore mappedUserPersonalInformationStore = mapper.map(userPersonalDetails, userPersonalInformationStore);

        // then
        assertAll(
                () -> assertEquals("userId", mappedUserPersonalInformationStore.getUserUUID()),
                () -> assertEquals("49123456789", mappedUserPersonalInformationStore.getMobilePhone()),
                () -> assertEquals("email@com", mappedUserPersonalInformationStore.getEmail()),
                () -> assertEquals("addressCity", mappedUserPersonalInformationStore.getAddressCity()),
                () -> assertEquals("placeOfBirth", mappedUserPersonalInformationStore.getPlaceOfBirth()),
                () -> assertEquals(Nationality.DE, mappedUserPersonalInformationStore.getNationality()),
                () -> assertEquals(FamilyStatus.SINGLE, mappedUserPersonalInformationStore.getFamilyStatus()),
                () -> assertEquals("addressHouseNumber", mappedUserPersonalInformationStore.getAddressHouseNumber()),
                () -> assertEquals(Gender.MALE, mappedUserPersonalInformationStore.getGender()),
                () -> assertEquals("addressZipCode", mappedUserPersonalInformationStore.getAddressZipCode()),
                () -> assertEquals("lastName", mappedUserPersonalInformationStore.getLastName()),
                () -> assertEquals("firstName", mappedUserPersonalInformationStore.getFirstName()),
                () -> assertEquals("addressStreet", mappedUserPersonalInformationStore.getAddressStreet()),
                () -> assertEquals(9, mappedUserPersonalInformationStore.getNumberOfChildren())
        );
    }

    @Test
    void merge_should_map_correct_numberOfChildren() {
        // given
        final var userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setNumberOfChildren(9);
        final var userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setPersonalDetails(PersonalDetails.builder()
                .numberOfChildren(7)
                .build());
        final var userTransactionalDataStore = new UserTransactionalDataStore();
        final var customDacPersonalDetails = new CustomDacPersonalDetails();
        customDacPersonalDetails.setNumberOfChildren("5");
        userTransactionalDataStore.setCustomDacPersonalDetails(customDacPersonalDetails);

        // when
        final var personalDetails = mapper.merge(userPersonalInformationStore, userAdditionalInformationStore, userTransactionalDataStore);

        // then
        assertEquals(9, personalDetails.getNumberOfChildren());
    }

    @Test
    void merge_should_map_correct_taxId(@Random UserPersonalInformationStore userPersonalInformationStore, @Random UserAdditionalInformationStore userAdditionalInformationStore, @Random UserTransactionalDataStore userTransactionalDataStore) {
        // given
        final var taxId = "12345678901";
        userAdditionalInformationStore.getPersonalDetails().setTaxId(taxId);

        // when
        final var personalDetails = mapper.merge(userPersonalInformationStore, userAdditionalInformationStore, userTransactionalDataStore);

        // then
        assertEquals(taxId, personalDetails.getTaxId());
    }

    @Test
    void merge_should_map_advertising_consent() {
        // given
        final var userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setConsentData(List.of(ConsentData.builder().consentState(ConsentState.ACCEPTED).consentType(ConsentType.EMAIL).build()));

        // when
        final var createIdentRequest = mapper.from(mock(UserPersonalInformationStore.class), mock(LoanOfferStore.class), Optional.of(userAdditionalInformationStore));

        // then
        assertThat(createIdentRequest.isAdvertisingConsent()).isEqualTo(true);
    }

    @Test
    void merge_should_map_advertising_consent_when_empty() {
        // given
        final var userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setConsentData(Lists.emptyList());

        // when
        final var createIdentRequest = mapper.from(mock(UserPersonalInformationStore.class), mock(LoanOfferStore.class), Optional.of(userAdditionalInformationStore));

        // then
        assertThat(createIdentRequest.isAdvertisingConsent()).isEqualTo(false);
    }

    @Test
    void merge_shouldMapPhoneNumberAndEmailCorrectly_whenPresentInUserPersonalInfo() {
        // given
        final var userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setMobilePhone("zzz");
        userPersonalInformationStore.setEmail("zzz");
        final var userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setContactData(ContactData.builder()
                .email("xxx")
                .mobile("xxx")
                .build());
        userAdditionalInformationStore.setConsentData(Lists.emptyList());

        // when
        final var createIdentRequest = mapper.from(userPersonalInformationStore, mock(LoanOfferStore.class), Optional.of(userAdditionalInformationStore));

        // then
        assertThat(createIdentRequest.getMobilePhone()).isEqualTo(userPersonalInformationStore.getMobilePhone());
        assertThat(createIdentRequest.getEmail()).isEqualTo(userPersonalInformationStore.getEmail());
    }

    @Test
    void merge_shouldMapPhoneNumberAndEmailCorrectly_whenNotPresentInUserPersonalInfo() {
        // given
        final var userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setMobilePhone(null);
        userPersonalInformationStore.setEmail(null);
        final var userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setContactData(ContactData.builder()
                .email("xxx")
                .mobile("xxx")
                .build());

        // when
        final var createIdentRequest = mapper.from(userPersonalInformationStore, mock(LoanOfferStore.class), Optional.of(userAdditionalInformationStore));

        // then
        assertThat(createIdentRequest.getMobilePhone()).isEqualTo(userAdditionalInformationStore.getContactData().getMobile());
        assertThat(createIdentRequest.getEmail()).isEqualTo(userAdditionalInformationStore.getContactData().getEmail());
    }

    @Test
    void merge_shouldLeavePhoneNumberAndEmailNull_whenNoInfoPresent() {
        // given
        final var userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setMobilePhone(null);
        userPersonalInformationStore.setEmail(null);

        // when
        final var createIdentRequest = mapper.from(userPersonalInformationStore, mock(LoanOfferStore.class), Optional.empty());

        // then
        assertThat(createIdentRequest.getMobilePhone()).isBlank();
        assertThat(createIdentRequest.getEmail()).isBlank();
    }

    @Test
    void merge_shouldLeavePhoneNumberAndEmailNull_whenNoInfoPresentInContactDetails() {
        // given
        final var userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setMobilePhone(null);
        userPersonalInformationStore.setEmail(null);
        final var userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setContactData(null);

        // when
        final var createIdentRequest = mapper.from(userPersonalInformationStore, mock(LoanOfferStore.class), Optional.of(userAdditionalInformationStore));

        // then
        assertThat(createIdentRequest.getMobilePhone()).isBlank();
        assertThat(createIdentRequest.getEmail()).isBlank();
    }
}
