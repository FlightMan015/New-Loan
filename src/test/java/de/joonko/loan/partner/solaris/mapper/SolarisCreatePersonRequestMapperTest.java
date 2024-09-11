package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.offer.domain.*;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.solaris.model.SolarisCreatePersonRequest;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static de.joonko.loan.util.SolarisBankConstant.MR;
import static de.joonko.loan.util.SolarisBankConstant.MS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolarisCreatePersonRequestMapperTest  extends BaseMapperTest {

    @Autowired
    private SolarisCreatePersonRequestMapper solarisCreatePersonRequestMapper;

    @Autowired
    private IdentificationPropConfig identificationPropConfig;

    @Test
    @DisplayName("Should map firstName")
    void firstName(@Random LoanDemand loanDemand) {
        identificationPropConfig.setAutoidentification(false);
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        PersonalDetails personalDetails = loanDemand.getPersonalDetails();
        assertEquals(personalDetails.getFirstName(), solarisCreatePersonRequest.getFirstName());
    }

    @Test
    @DisplayName("Should map firstName to X-MANUALTEST-HAPPYPATH when auto identification is enabled")
    void firstName_autoIdentification(@Random LoanDemand loanDemand) {
        identificationPropConfig.setAutoidentification(true);
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        PersonalDetails personalDetails = loanDemand.getPersonalDetails();
        assertEquals("X-MANUALTEST-HAPPYPATH", solarisCreatePersonRequest.getFirstName());
    }

    @Test
    @DisplayName("Should map lastName")
    void lastName(@Random LoanDemand loanDemand) {
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        PersonalDetails personalDetails = loanDemand.getPersonalDetails();
        assertEquals(personalDetails.getLastName(), solarisCreatePersonRequest.getLastName());
    }

    @Test
    @DisplayName("Should map email")
    void email(@Random LoanDemand loanDemand) {
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        ContactData contactData = loanDemand.getContactData();
        assertEquals(contactData.getEmail().getEmailString(), solarisCreatePersonRequest.getEmail());
    }

    @Test
    @DisplayName("Should map mobileNumber")
    void mobileNumber(@Random LoanDemand loanDemand) {
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        ContactData contactData = loanDemand.getContactData();
        assertEquals("+"+contactData.getMobile(), solarisCreatePersonRequest.getMobileNumber());
    }

    @Test
    @DisplayName("Should map gender to salutation for Male")
    void salutationMale(@Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails().setGender(Gender.MALE);
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        PersonalDetails personalDetails = loanDemand.getPersonalDetails();
        assertEquals("MR", solarisCreatePersonRequest.getSalutation());
    }

    @Test
    @DisplayName("Should map gender to salutation for Female")
    void salutationFemale(@Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails().setGender(Gender.FEMALE);
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        PersonalDetails personalDetails = loanDemand.getPersonalDetails();
        assertEquals("MS", solarisCreatePersonRequest.getSalutation());
    }

    @Test
    @DisplayName("Should map birthDate")
    void birthDate(@Random LoanDemand loanDemand) {
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        PersonalDetails personalDetails = loanDemand.getPersonalDetails();
        assertEquals(personalDetails.getBirthDate(), solarisCreatePersonRequest.getBirthDate());
    }

    @Test
    @DisplayName("Should map place of birth")
    void placeOfBirth(@Random LoanDemand loanDemand) {
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        PersonalDetails personalDetails = loanDemand.getPersonalDetails();
        assertEquals(personalDetails.getPlaceOfBirth(), solarisCreatePersonRequest.getBirthCity());
    }

    @Test
    @DisplayName("Should map countryCode to nationality")
    void countryToNationality(@Random LoanDemand loanDemand) {
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        PersonalDetails personalDetails = loanDemand.getPersonalDetails();
        assertEquals(personalDetails.getNationality().getCountryCode().toString(), solarisCreatePersonRequest.getNationality());
    }

    @Test
    @DisplayName("Should map male salutation")
    void salutation_male(@Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails().setGender(Gender.MALE);
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        assertEquals(MR, solarisCreatePersonRequest.getSalutation());
    }

    @Test
    @DisplayName("Should map female salutation")
    void salutation_female(@Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails().setGender(Gender.FEMALE);
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        assertEquals(MS, solarisCreatePersonRequest.getSalutation());
    }

    @Test
    @DisplayName("Should map previousAddress to contactAddress")
    void previousToContact(@Random LoanDemand loanDemand) {
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        PreviousAddress previousAddress = loanDemand.getContactData().getPreviousAddress();
        assertEquals(previousAddress.getCity(), solarisCreatePersonRequest.getContactAddress().getCity());
        assertEquals(previousAddress.getStreet(), solarisCreatePersonRequest.getContactAddress().getLine1());
        assertEquals(previousAddress.getCity(), solarisCreatePersonRequest.getContactAddress().getCity());
    }

    @Test
    @DisplayName("Should map contactData to address")
    void contactDataToAddress(@Random LoanDemand loanDemand) {
        SolarisCreatePersonRequest solarisCreatePersonRequest = solarisCreatePersonRequestMapper.toSolarisRequest(loanDemand);
        ContactData contactData = loanDemand.getContactData();
        assertEquals(contactData.getCity(), solarisCreatePersonRequest.getAddress().getCity());
        assertEquals(contactData.getStreetName(), solarisCreatePersonRequest.getAddress().getLine1());
        assertEquals(contactData.getStreetNumber(), solarisCreatePersonRequest.getAddress().getLine2());
        assertEquals(contactData.getZipCode().getCode(), solarisCreatePersonRequest.getAddress().getPostalCode());
    }
}
