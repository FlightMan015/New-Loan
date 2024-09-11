package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.integrations.model.DistributionChannel;
import de.joonko.loan.integrations.segment.CustomerData;
import de.joonko.loan.integrations.segment.Traits;
import de.joonko.loan.offer.api.CreditDetails;
import de.joonko.loan.offer.api.EmploymentType;
import de.joonko.loan.offer.api.FamilyStatus;
import de.joonko.loan.offer.api.Gender;
import de.joonko.loan.offer.api.HousingType;
import de.joonko.loan.offer.api.Nationality;
import de.joonko.loan.offer.api.ShortDate;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.user.service.persistence.domain.ConsentData;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import io.fusionauth.domain.User;

import static de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.testData.UserPersonalDataMapperTestData.getCustomerData;
import static de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.testData.UserPersonalDataMapperTestData.getTestUser;
import static de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.testData.UserPersonalDataMapperTestData.getUserPersonalData;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPersonalDataMapperTest extends BaseMapperTest {

    @Autowired
    private UserPersonalDataMapper userPersonalDataMapper;

    private static final String USER_ID = "2f20a660-f0f2-4ca5-9fe6-b24b52cd1070";

    @Test
    void mapFromUserData() {
        // given
        User user = getTestUser(USER_ID);

        // when
        var mappedUserPersonal = userPersonalDataMapper.fromUserData(user);

        // then
        assertAll(
                () -> assertEquals(USER_ID, mappedUserPersonal.getUserUuid(), "userId should be mapped"),
                () -> assertEquals(user.data.get("uid"), mappedUserPersonal.getBonifyUserId(), "bonifyUserId should be mapped"),
                () -> assertEquals(user.email, mappedUserPersonal.getContactData().getEmail(), "email should be mapped"),
                () -> assertEquals(user.data.get("userVerifiedViaBankAccount"), mappedUserPersonal.getVerifiedViaBankAccount(), "verifiedViaBank should be mapped"),
                () -> assertEquals(DistributionChannel.BONIFY, mappedUserPersonal.getDistributionChannel(), "distributionChannel should be mapped"),
                () -> assertEquals(user.firstName, mappedUserPersonal.getPersonalDetails().getFirstName(), "firstName should be mapped"),
                () -> assertEquals(user.lastName, mappedUserPersonal.getPersonalDetails().getLastName(), "lastName should be mapped"),
                () -> assertEquals(user.data.get("addressCity"), mappedUserPersonal.getContactData().getCity(), "city should be mapped"),
                () -> assertEquals(user.data.get("addressHouseNumber"), mappedUserPersonal.getContactData().getHouseNumber(), "houseNumber should be mapped"),
                () -> assertEquals(user.data.get("addressStreet"), mappedUserPersonal.getContactData().getStreetName(), "street should be mapped"),
                () -> assertEquals(user.data.get("addressZipCode"), mappedUserPersonal.getContactData().getPostCode(), "postCode should be mapped"),
                () -> assertEquals(Nationality.DE, mappedUserPersonal.getPersonalDetails().getNationality(), "nationality should be mapped"),
                () -> assertEquals(user.data.get("placeOfBirth"), mappedUserPersonal.getPersonalDetails().getPlaceOfBirth(), "placeOfBirth should be mapped"),
                () -> assertEquals(Gender.MALE, mappedUserPersonal.getPersonalDetails().getGender(), "gender should be mapped"),
                () -> assertEquals(FamilyStatus.SINGLE, mappedUserPersonal.getPersonalDetails().getFamilyStatus(), "familyStatus should be mapped"),
                () -> assertEquals("4915210362813", mappedUserPersonal.getContactData().getMobile(), "mobile should be mapped"),
                () -> assertEquals(user.getTenantId().toString(), mappedUserPersonal.getTenantId(), "tenantId should be mapped")
        );
    }

    @Test
    void mapFromCustomerData() {
        // given
        CustomerData customerData = getCustomerData();
        Traits traits = customerData.getTraits();

        // when
        var mappedUserPersonal = userPersonalDataMapper.fromCustomerData(customerData);

        // then
        assertAll(
                () -> assertTrue(mappedUserPersonal.getVerifiedViaBankAccount()),
                () -> assertEquals(DistributionChannel.BONIFY, mappedUserPersonal.getDistributionChannel()),

                () -> assertEquals(Gender.MALE, mappedUserPersonal.getPersonalDetails().getGender()),
                () -> assertEquals(traits.getFirstName(), mappedUserPersonal.getPersonalDetails().getFirstName()),
                () -> assertEquals(traits.getLastName(), mappedUserPersonal.getPersonalDetails().getLastName()),
                () -> assertEquals(FamilyStatus.MARRIED, mappedUserPersonal.getPersonalDetails().getFamilyStatus()),
                () -> assertEquals(traits.getDateOfBirth(), mappedUserPersonal.getPersonalDetails().getBirthDate()),
                () -> assertEquals(Nationality.DE, mappedUserPersonal.getPersonalDetails().getNationality()),
                () -> assertEquals(traits.getPlaceOfBirth(), mappedUserPersonal.getPersonalDetails().getPlaceOfBirth()),
                () -> assertEquals(traits.getChildrenCount(), mappedUserPersonal.getPersonalDetails().getNumberOfChildren()),
                () -> assertEquals(HousingType.OWNER, mappedUserPersonal.getPersonalDetails().getHousingType()),
                () -> assertEquals(traits.getNumberOfCreditCard(), mappedUserPersonal.getPersonalDetails().getNumberOfCreditCard()),

                () -> assertEquals(traits.getAddressStreet(), mappedUserPersonal.getContactData().getStreetName()),
                () -> assertEquals(traits.getAddressHouseNumber(), mappedUserPersonal.getContactData().getHouseNumber()),
                () -> assertEquals(traits.getAddressZipCode(), mappedUserPersonal.getContactData().getPostCode()),
                () -> assertEquals(traits.getAddressCity(), mappedUserPersonal.getContactData().getCity()),
                () -> assertEquals(ShortDate.builder().year(2012).month(12).build(), mappedUserPersonal.getContactData().getLivingSince()),
                () -> assertEquals(traits.getEmail(), mappedUserPersonal.getContactData().getEmail()),
                () -> assertEquals(traits.getPhone_number(), mappedUserPersonal.getContactData().getMobile()),

                () -> assertEquals(EmploymentType.REGULAR_EMPLOYED, mappedUserPersonal.getEmploymentDetails().getEmploymentType()),
                () -> assertEquals(traits.getNameOfEmployer(), mappedUserPersonal.getEmploymentDetails().getEmployerName()),
                () -> assertEquals(ShortDate.builder().year(2020).month(12).build(), mappedUserPersonal.getEmploymentDetails().getEmploymentSince()),
                () -> assertEquals(traits.getAddressStreetOfEmployer(), mappedUserPersonal.getEmploymentDetails().getStreetName()),
                () -> assertEquals(traits.getAddressZipCodeOfEmployer(), mappedUserPersonal.getEmploymentDetails().getPostCode()),
                () -> assertEquals(traits.getAddressCityOfEmployer(), mappedUserPersonal.getEmploymentDetails().getCity()),
                () -> assertEquals(traits.getAddressHouseNumberOfEmployer(), mappedUserPersonal.getEmploymentDetails().getHouseNumber()),

                () -> assertEquals(traits.getEmployeeSalaryAmountLast1M(), mappedUserPersonal.getIncome().getNetIncome()),
                () -> assertEquals(traits.getPensionAmountLast1M(), mappedUserPersonal.getIncome().getPensionBenefits()),
                () -> assertEquals(traits.getChildBenefitAmountLast1M(), mappedUserPersonal.getIncome().getChildBenefits()),
                () -> assertEquals(traits.getOtherIncomeAmountLast1M(), mappedUserPersonal.getIncome().getOtherRevenue()),
                () -> assertEquals(traits.getRentalIncomeLast1M(), mappedUserPersonal.getIncome().getRentalIncome()),
                () -> assertEquals(traits.getAlimonyIncomeAmountLast1M(), mappedUserPersonal.getIncome().getAlimonyPayments()),

                () -> assertEquals(Math.abs(traits.getMonthlyMortgage()), mappedUserPersonal.getExpenses().getMortgages()),
                () -> assertNull(mappedUserPersonal.getExpenses().getInsuranceAndSavings()),
                () -> assertEquals(Math.abs(traits.getMonthlyLoanInstallments()), mappedUserPersonal.getExpenses().getLoanInstalments()),
                () -> assertEquals(Math.abs(traits.getMonthlyRent()), mappedUserPersonal.getExpenses().getRent()),
                () -> assertEquals(Math.abs(traits.getAlimonyAmountLast1M()), mappedUserPersonal.getExpenses().getAlimony()),
                () -> assertEquals(Math.abs(traits.getMonthlyPrivateHealthInsurance()), mappedUserPersonal.getExpenses().getPrivateHealthInsurance()),
                () -> assertEquals(Math.abs(traits.getCarInsuranceAmountLast1M()), mappedUserPersonal.getExpenses().getVehicleInsurance()),

                () -> assertEquals(traits.getProbabilityOfDefault(), mappedUserPersonal.getCreditDetails().getProbabilityOfDefault()),
                () -> assertEquals(traits.getEstimatedSchufaClass(), mappedUserPersonal.getCreditDetails().getEstimatedSchufaClass()),
                () -> assertEquals(traits.getBonimaScore(), mappedUserPersonal.getCreditDetails().getBonimaScore())
        );
    }

    @Test
    void mapToUserPersonalInfo() {
        // given
        UserPersonalData userPersonalData = getUserPersonalData(USER_ID);

        // when
        var mappedUserPersonal = userPersonalDataMapper.toUserPersonalInfo(userPersonalData);

        // then
        assertAll(
                () -> assertEquals(userPersonalData.getUserUuid(), mappedUserPersonal.getUserUUID(), "userId should be mapped"),
                () -> assertEquals(userPersonalData.getPersonalDetails().getFirstName(), mappedUserPersonal.getFirstName(), "firstName should be mapped"),
                () -> assertEquals(userPersonalData.getPersonalDetails().getLastName(), mappedUserPersonal.getLastName(), "lastName should be mapped"),
                () -> assertEquals(userPersonalData.getContactData().getCity(), mappedUserPersonal.getAddressCity(), "city should be mapped"),
                () -> assertEquals(userPersonalData.getContactData().getHouseNumber(), mappedUserPersonal.getAddressHouseNumber(), "houseNumber should be mapped"),
                () -> assertEquals(userPersonalData.getContactData().getStreetName(), mappedUserPersonal.getAddressStreet(), "street should be mapped"),
                () -> assertEquals(userPersonalData.getContactData().getPostCode(), mappedUserPersonal.getAddressZipCode(), "postCode should be mapped"),
                () -> assertEquals(userPersonalData.getContactData().getEmail(), mappedUserPersonal.getEmail(), "email should be mapped"),
                () -> assertEquals(userPersonalData.getPersonalDetails().getNationality(), mappedUserPersonal.getNationality(), "nationality should be mapped"),
                () -> assertEquals(userPersonalData.getPersonalDetails().getPlaceOfBirth(), mappedUserPersonal.getPlaceOfBirth(), "placeOfBirth should be mapped"),
                () -> assertEquals(userPersonalData.getPersonalDetails().getGender(), mappedUserPersonal.getGender(), "gender should be mapped"),
                () -> assertEquals(userPersonalData.getPersonalDetails().getFamilyStatus(), mappedUserPersonal.getFamilyStatus(), "familyStatus should be mapped"),
                () -> assertEquals(userPersonalData.getContactData().getMobile(), mappedUserPersonal.getMobilePhone(), "mobile should be mapped")
        );
    }

    @Test
    void mapToUserAdditionalInfo() {
        // given
        UserPersonalData userPersonalData = getUserPersonalData(USER_ID);
        final List<ConsentData> consents = List.of();
        final var existingCreditDetails = CreditDetails.builder()
                .bonimaScore(786)
                .estimatedSchufaClass("D")
                .creditCardLimitDeclared(700.0)
                .build();

        // when
        var mappedUserPersonal = userPersonalDataMapper.toUserAdditionalInfo(userPersonalData, consents, existingCreditDetails);

        // then
        assertAll(
                () -> assertEquals(userPersonalData.getUserUuid(), mappedUserPersonal.getUserUUID(), "userId should be mapped"),
                () -> assertEquals(userPersonalData.getPersonalDetails(), mappedUserPersonal.getPersonalDetails(), "personalDetails should be mapped"),
                () -> assertEquals(userPersonalData.getEmploymentDetails(), mappedUserPersonal.getEmploymentDetails(), "employmentDetails should be mapped"),
                () -> assertEquals(userPersonalData.getExpenses(), mappedUserPersonal.getExpenses(), "expenses should be mapped"),
                () -> assertEquals(userPersonalData.getIncome(), mappedUserPersonal.getIncome(), "income should be mapped"),
                () -> assertEquals(userPersonalData.getContactData(), mappedUserPersonal.getContactData(), "contactData should be mapped"),
                () -> assertEquals(userPersonalData.getCreditDetails().getBonimaScore(), mappedUserPersonal.getCreditDetails().getBonimaScore(), "creditDetails.bonimaScore should be mapped"),
                () -> assertEquals(userPersonalData.getCreditDetails().getEstimatedSchufaClass(), mappedUserPersonal.getCreditDetails().getEstimatedSchufaClass(), "creditDetails.estimatedSchufaClass should be mapped"),
                () -> assertEquals(userPersonalData.getCreditDetails().getProbabilityOfDefault(), mappedUserPersonal.getCreditDetails().getProbabilityOfDefault(), "creditDetails.probabilityOfDefault should be mapped"),
                () -> assertEquals(existingCreditDetails.getCreditCardLimitDeclared(), mappedUserPersonal.getCreditDetails().getCreditCardLimitDeclared(), "creditDetails.creditCardLimitDeclared should be mapped"),
                () -> assertEquals(consents, mappedUserPersonal.getConsentData(), "consents should be mapped")
        );
    }
}
