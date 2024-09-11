package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import de.joonko.loan.user.service.persistence.domain.ConsentData;
import de.joonko.loan.user.service.persistence.domain.ConsentState;
import de.joonko.loan.user.service.persistence.domain.ConsentType;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static de.joonko.loan.common.utils.PhoneNumberUtil.extractPrefixFromPhoneNumber;
import static org.junit.jupiter.api.Assertions.*;


class SantanderGetOfferPersonalDetailRequestMapperTest extends BaseMapperTest {

    @Autowired
    private SantanderGetOfferPersonalDetailRequestMapper mapper;

    @Test
    void mapAllPersonalDetails(@Random LoanDemand loanDemand) {
        // given
        // when
        ScbCapsBcoWSStub.DarlehnsnehmerXO actual = mapper.toDarlehnsnehmer(loanDemand);

        // then
        assertAll(
                () -> assertEquals(loanDemand.getContactData().getPreviousAddress().getHouseNumber(), actual.getVorherigeAdresse().getHausnr(), "failed mapping previousAddress.houseNumber"),
                () -> assertEquals(loanDemand.getContactData().getPreviousAddress().getPostCode(), actual.getVorherigeAdresse().getPlz(), "failed mapping previousAddress.postCode"),
                () -> assertEquals(loanDemand.getContactData().getPreviousAddress().getCity(), actual.getVorherigeAdresse().getOrt(), "failed mapping previousAddress.city"),
                () -> assertEquals(loanDemand.getContactData().getPreviousAddress().getStreet(), actual.getVorherigeAdresse().getStrasse(), "failed mapping previousAddress.street"),
                () -> assertEquals(loanDemand.getContactData().getPreviousAddress().getCountry().name(), actual.getVorherigeAdresse().getLand().getLandType(), "failed mapping previousAddress.country"),

                () -> assertEquals(loanDemand.getContactData().getStreetName(), actual.getAktuelleAdresse().getStrasse(), "failed mapping streetName"),
                () -> assertEquals(loanDemand.getContactData().getStreetNumber(), actual.getAktuelleAdresse().getHausnr(), "failed mapping streetNumber"),
                () -> assertEquals(loanDemand.getContactData().getZipCode().getCode(), actual.getAktuelleAdresse().getPlz(), "failed mapping zipCode"),
                () -> assertEquals(loanDemand.getContactData().getCity(), actual.getAktuelleAdresse().getOrt(), "failed mapping city"),
                () -> assertEquals("DE", actual.getAktuelleAdresse().getLand().getLandType(), "failed mapping country"),
                () -> assertNotNull(actual.getAktuelleAdresse().getWohnhaftSeit(), "failed mapping livingSince"),

                () -> assertEquals(extractPrefixFromPhoneNumber(loanDemand.getContactData().getMobile()), actual.getTelefon(), "failed mapping mobile"),
                () -> assertEquals(extractPrefixFromPhoneNumber(loanDemand.getContactData().getMobile()), actual.getMobil(), "failed mapping mobile2"),
                () -> assertEquals(loanDemand.getContactData().getEmail().getEmailString(), actual.getEmail(), "failed mapping email"),

                () -> assertEquals(loanDemand.getPersonalDetails().getNumberOfChildren(), actual.getKinderImHaushalt().intValue(), "failed mapping numberOfChildren to kinderImHaushalt"),
                () -> assertEquals(loanDemand.getPersonalDetails().getNumberOfChildren(), actual.getAnzKindergeldber().intValue(), "failed mapping numberOfChildren to anzKindergeldber"),

                () -> assertNotNull(actual.getAnrede().getValue(), "failed mapping gender"),
                () -> assertEquals(loanDemand.getPersonalDetails().getLastName(), actual.getName(), "failed mapping lastName"),
                () -> assertEquals(loanDemand.getPersonalDetails().getFirstName(), actual.getVorname(), "failed mapping firstName"),
                () -> assertNotNull(actual.getGeburtsdatum(), "failed mapping birthdate"),
                () -> assertEquals(loanDemand.getPersonalDetails().getPlaceOfBirth(), actual.getGeburtsort(), "failed mapping placeOfBirth"),
                () -> assertEquals(loanDemand.getPersonalDetails().getNationality().name(), actual.getStaatsangehoerigkeit().getLandType(), "failed mapping nationality"),
                () -> assertNotNull(actual.getFamilienstand().getValue(), "failed mapping familyStatus"),

                () -> assertNotNull(actual.getAktuellesBV(), "failed mapping employmentDetails"),

                () -> assertEquals(loanDemand.getConsents().stream().anyMatch(consent -> consent.getConsentState().equals(ConsentState.ACCEPTED)), actual.getWerbezustimmung(), "failed mapping false state"),

                () -> assertNotNull(actual.getEinnahmenAusgaben(), "failed mapping finance"),

                () -> assertEquals(loanDemand.getDigitalAccountStatements().getIban(), actual.getBankverbindung().getIban(), "failed mapping iban"),
                () -> assertEquals(ScbCapsBcoWSStub.KontoinhaberType.DN1, actual.getBankverbindung().getKontoinhaber(), "failed mapping accountType")
        );
    }

    @Test
    void doNotMapPreviousAddressFields(@Random(excludes = {"contactData.previousAddress"}) LoanDemand loanDemand) {
        // given
        // when
        ScbCapsBcoWSStub.DarlehnsnehmerXO actual = mapper.toDarlehnsnehmer(loanDemand);

        // then
        assertAll(
                () -> assertNull(actual.getVorherigeAdresse().getHausnr(), "failed and mapped previousAddress.houseNumber"),
                () -> assertNull(actual.getVorherigeAdresse().getPlz(), "failed and mapped previousAddress.postCode"),
                () -> assertNull(actual.getVorherigeAdresse().getOrt(), "failed and mapped previousAddress.city"),
                () -> assertNull(actual.getVorherigeAdresse().getStrasse(), "failed and mapped previousAddress.street"),
                () -> assertNull(actual.getVorherigeAdresse().getLand(), "failed and mapped previousAddress.country")
        );
    }

    @ParameterizedTest
    @EnumSource(value = ConsentState.class)
    void advertisement_consents_should_return_correct_values(final ConsentState consentState, final @Random LoanDemand loanDemand) {
        final var consentData = List.of(ConsentData.builder()
                        .consentState(consentState)
                        .consentType(ConsentType.LETTER)
                        .build(),
                ConsentData.builder()
                        .consentState(ConsentState.REVOKED)
                        .consentType(ConsentType.EMAIL)
                        .build()
                );
        loanDemand.setConsents(consentData);

        boolean consentGiven = mapper.toAdvertisementConsentGiven(loanDemand);

        assertEquals(consentState.equals(ConsentState.ACCEPTED), consentGiven);
    }

    @ParameterizedTest
    @EnumSource(value = ConsentState.class)
    void advertisement_email_consents_should_return_correct_values(final ConsentState consentState,  final @Random LoanDemand loanDemand) {
        final var consentData = List.of(ConsentData.builder()
                        .consentState(consentState)
                        .consentType(ConsentType.EMAIL)
                        .build(),
                ConsentData.builder()
                        .consentState(ConsentState.REVOKED)
                        .consentType(ConsentType.SMS)
                        .build()
        );
        loanDemand.setConsents(consentData);

        boolean consentGiven = mapper.toAdvertisementWithEmailConsentGiven(loanDemand);

        assertEquals(consentState.equals(ConsentState.ACCEPTED), consentGiven);
    }

    @ParameterizedTest
    @EnumSource(value = ConsentState.class)
    void advertisement_phone_consents_should_return_correct_values(final ConsentState consentState,  final @Random LoanDemand loanDemand) {
        final var consentData = List.of(ConsentData.builder()
                        .consentState(consentState)
                        .consentType(ConsentType.PHONE)
                        .build(),
                ConsentData.builder()
                        .consentState(ConsentState.REVOKED)
                        .consentType(ConsentType.SMS)
                        .build()
        );
        loanDemand.setConsents(consentData);

        boolean consentGiven = mapper.toAdvertisementWithPhoneConsentGiven(loanDemand);

        assertEquals(consentState.equals(ConsentState.ACCEPTED), consentGiven);
    }

    @ParameterizedTest
    @EnumSource(value = ConsentState.class)
    void advertisement_sms_consents_should_return_correct_values(final ConsentState consentState,  final @Random LoanDemand loanDemand) {
        final var consentData = List.of(ConsentData.builder()
                        .consentState(consentState)
                        .consentType(ConsentType.SMS)
                        .build(),
                ConsentData.builder()
                        .consentState(ConsentState.REVOKED)
                        .consentType(ConsentType.EMAIL)
                        .build()
        );
        loanDemand.setConsents(consentData);

        boolean consentGiven = mapper.toAdvertisementWithSmsConsentGiven(loanDemand);

        assertEquals(consentState.equals(ConsentState.ACCEPTED), consentGiven);
    }

    @ParameterizedTest
    @EnumSource(value = ConsentState.class)
    void advertisement_letter_consents_should_return_correct_values(final ConsentState consentState,  final @Random LoanDemand loanDemand) {
        final var consentData = List.of(ConsentData.builder()
                        .consentState(consentState)
                        .consentType(ConsentType.LETTER)
                        .build(),
                ConsentData.builder()
                        .consentState(ConsentState.REVOKED)
                        .consentType(ConsentType.PHONE)
                        .build()
        );
        loanDemand.setConsents(consentData);

        boolean consentGiven = mapper.toAdvertisementWithLetterConsentGiven(loanDemand);

        assertEquals(consentState.equals(ConsentState.ACCEPTED), consentGiven);
    }

    @ParameterizedTest
    @EnumSource(
            value = FamilyStatus.class,
            names = {"MARRIED", "LIVING_IN_LONGTERM_RELATIONSHIP"},
            mode = EnumSource.Mode.INCLUDE)
    void toNumberOfAdults_whenMarried_shouldReturn2(final FamilyStatus familyStatus, final @Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails().setFamilyStatus(familyStatus);
        final int numberOfAdultsInHousehold = mapper.toNumberOfAdults(loanDemand);
        assertEquals(2, numberOfAdultsInHousehold);
    }

    @ParameterizedTest
    @EnumSource(
            value = FamilyStatus.class,
            names = {"MARRIED", "LIVING_IN_LONGTERM_RELATIONSHIP"},
            mode = EnumSource.Mode.EXCLUDE)
    void toNumberOfAdults_whenAlone_shouldReturn1(final FamilyStatus familyStatus, final @Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails().setFamilyStatus(familyStatus);
        final int numberOfAdultsInHousehold = mapper.toNumberOfAdults(loanDemand);
        assertEquals(1, numberOfAdultsInHousehold);
    }

    @Test
    void toPhoneNumber_willReplacePrefix_whenPrefixExists() {
        // given
        final String phoneNumberWithPrefix = "+4917656879078";
        final String phoneNumberWithoutPrefix = "017656879078";

        // when
        final var result = mapper.toPhoneNumber(phoneNumberWithPrefix);

        // then
        assertEquals(phoneNumberWithoutPrefix, result);
    }
}