package de.joonko.loan.partner.santander.mapper;


import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.Nationality;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import de.joonko.loan.user.service.persistence.domain.ConsentState;
import de.joonko.loan.user.service.persistence.domain.ConsentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static de.joonko.loan.common.utils.PhoneNumberUtil.extractPrefixFromPhoneNumber;
import static de.joonko.loan.offer.domain.FamilyStatus.getStatusesFor2AdultsInHousehold;

@Mapper(componentModel = "spring", uses = {SantanderGetOfferCurrentEmploymentDetailRequestMapper.class, SantanderGetOfferIncomeExpenseRequestMapper.class})
public abstract class SantanderGetOfferPersonalDetailRequestMapper {
    @Named("getAddressCountry")
    ScbCapsBcoWSStub.LandType getAddressCountry(LoanDemand loanDemand) {
        ScbCapsBcoWSStub.LandType country = new ScbCapsBcoWSStub.LandType();
        country.setLandType("DE");
        return country;
    }

    @Named("toGender")
    ScbCapsBcoWSStub.AnredeType toGender(LoanDemand loanDemand) {
        switch (loanDemand.getPersonalDetails().getGender()) {
            case MALE:
                return ScbCapsBcoWSStub.AnredeType.HERR;
            case FEMALE:
                return ScbCapsBcoWSStub.AnredeType.FRAU;
            default:
                throw new RuntimeException(loanDemand.getPersonalDetails().getGender() + " Gender not supported");
        }
    }

    @Named("toNumberOfAdults")
    int toNumberOfAdults(final LoanDemand loanDemand) {
        return getStatusesFor2AdultsInHousehold().contains(loanDemand.getPersonalDetails().getFamilyStatus()) ? 2 : 1;
    }

    @Named("toFamilyStatus")
    ScbCapsBcoWSStub.FamilienstandType toFamilyStatus(LoanDemand loanDemand) {
        switch (loanDemand.getPersonalDetails().getFamilyStatus()) {
            case SINGLE:
                return ScbCapsBcoWSStub.FamilienstandType.LEDIG;
            case MARRIED:
                return ScbCapsBcoWSStub.FamilienstandType.VERHEIRATET;
            case DIVORCED:
                return ScbCapsBcoWSStub.FamilienstandType.GESCHIEDEN;
            case WIDOWED:
                return ScbCapsBcoWSStub.FamilienstandType.VERWITWET;
            case LIVING_SEPARATELY:
                return ScbCapsBcoWSStub.FamilienstandType.GETRENNT_LEBEND;
            // TODO: check mapping with tapas
            case LIVING_IN_LONGTERM_RELATIONSHIP:
                return ScbCapsBcoWSStub.FamilienstandType.VERPARTNERT;
            default:
                throw new RuntimeException(loanDemand.getPersonalDetails().getFamilyStatus() + " family status not supported");
        }
    }

    @Named("toAdvertisementConsentGiven")
    static boolean toAdvertisementConsentGiven(final LoanDemand loanDemand) {
        return loanDemand.getConsents().stream().anyMatch(consent -> consent.getConsentState().equals(ConsentState.ACCEPTED));
    }

    @Named("toAdvertisementWithEmailConsentGiven")
    static boolean toAdvertisementWithEmailConsentGiven(final LoanDemand loanDemand) {
        return loanDemand.getConsents().stream().anyMatch(consent -> consent.getConsentType().equals(ConsentType.EMAIL)
                && consent.getConsentState().equals(ConsentState.ACCEPTED));
    }

    @Named("toAdvertisementWithPhoneConsentGiven")
    static boolean toAdvertisementWithPhoneConsentGiven(final LoanDemand loanDemand) {
        return loanDemand.getConsents().stream().anyMatch(consent -> consent.getConsentType().equals(ConsentType.PHONE) &&
                consent.getConsentState().equals(ConsentState.ACCEPTED));
    }

    @Named("toAdvertisementWithLetterConsentGiven")
    static boolean toAdvertisementWithLetterConsentGiven(final LoanDemand loanDemand) {
        return loanDemand.getConsents().stream().anyMatch(consent -> consent.getConsentType().equals(ConsentType.LETTER) &&
                consent.getConsentState().equals(ConsentState.ACCEPTED));
    }

    @Named("toAdvertisementWithSmsConsentGiven")
    static boolean toAdvertisementWithSmsConsentGiven(final LoanDemand loanDemand) {
        return loanDemand.getConsents().stream().anyMatch(consent -> consent.getConsentType().equals(ConsentType.SMS) &&
                consent.getConsentState().equals(ConsentState.ACCEPTED));
    }

    @Named("toPhoneNumber")
    static String toPhoneNumber(final String phoneNumber) {
        return extractPrefixFromPhoneNumber(phoneNumber);
    }

    @Mapping(target = "anrede", source = ".", qualifiedByName = "toGender")
    @Mapping(target = "name", source = "personalDetails.lastName")
    @Mapping(target = "vorname", source = "personalDetails.firstName")
    @Mapping(target = "geburtsdatum", source = "personalDetails.birthDate")
    @Mapping(target = "geburtsort", source = "personalDetails.placeOfBirth")
    @Mapping(target = "staatsangehoerigkeit", source = "personalDetails.nationality", qualifiedByName = "toNationality")
    @Mapping(target = "familienstand", source = ".", qualifiedByName = "toFamilyStatus")
    @Mapping(target = "erwachseneImHaushalt", source = ".", qualifiedByName = "toNumberOfAdults")

    @Mapping(target = "kinderImHaushalt", source = "personalDetails.numberOfChildren")
    @Mapping(target = "anzKindergeldber", source = "personalDetails.numberOfChildren")

    @Mapping(target = "telefon", source = "contactData.mobile", qualifiedByName = "toPhoneNumber")
    @Mapping(target = "mobil", source = "contactData.mobile", qualifiedByName = "toPhoneNumber")
    @Mapping(target = "email", source = "contactData.email.emailString")

    @Mapping(target = "aktuelleAdresse.strasse", source = "contactData.streetName")
    @Mapping(target = "aktuelleAdresse.hausnr", source = "contactData.streetNumber")
    @Mapping(target = "aktuelleAdresse.plz", source = "contactData.zipCode.code")
    @Mapping(target = "aktuelleAdresse.ort", source = "contactData.city")
    @Mapping(target = "aktuelleAdresse.land", source = ".", qualifiedByName = "getAddressCountry")
    @Mapping(target = "aktuelleAdresse.wohnhaftSeit", source = "contactData.livingSince")

    @Mapping(target = "vorherigeAdresse.strasse", source = "contactData.previousAddress.street")
    @Mapping(target = "vorherigeAdresse.plz", source = "contactData.previousAddress.postCode")
    @Mapping(target = "vorherigeAdresse.ort", source = "contactData.previousAddress.city")
    @Mapping(target = "vorherigeAdresse.hausnr", source = "contactData.previousAddress.houseNumber")
    @Mapping(target = "vorherigeAdresse.land", source = "contactData.previousAddress.country", qualifiedByName = "toNationality")

    @Mapping(target = "aktuellesBV", source = "loanDemand.employmentDetails")

    @Mapping(target = "werbezustimmung", source = ".", qualifiedByName = "toAdvertisementConsentGiven")
    @Mapping(target = "werbewegEmail", source = ".", qualifiedByName = "toAdvertisementWithEmailConsentGiven")
    @Mapping(target = "werbewegBrief", source = ".", qualifiedByName = "toAdvertisementWithLetterConsentGiven")
    @Mapping(target = "werbewegTelefon", source = ".", qualifiedByName = "toAdvertisementWithPhoneConsentGiven")
    @Mapping(target = "werbewegSmsMms", source = ".", qualifiedByName = "toAdvertisementWithSmsConsentGiven")

    @Mapping(target = "einnahmenAusgaben", source = "personalDetails")

    @Mapping(target = "bankverbindung.iban", source = "digitalAccountStatements.iban")
    @Mapping(target = "bankverbindung.kontoinhaber", expression = "java(de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.KontoinhaberType.DN1)")
    abstract ScbCapsBcoWSStub.DarlehnsnehmerXO toDarlehnsnehmer(LoanDemand loanDemand);

    @Named("toNationality")
    ScbCapsBcoWSStub.LandType toNationality(Nationality nationality) {
        if (nationality == null) {
            return null;
        }
        ScbCapsBcoWSStub.LandType landType = new ScbCapsBcoWSStub.LandType();
        landType.setLandType(nationality.name());
        return landType;
    }
}
