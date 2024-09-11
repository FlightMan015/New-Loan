package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider.testData;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.offer.api.*;
import de.joonko.loan.integrations.segment.CustomerData;
import de.joonko.loan.integrations.segment.Traits;
import io.fusionauth.domain.User;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class UserPersonalDataMapperTestData {
    public static CustomerData getCustomerData() {
        return CustomerData.builder()
                .traits(Traits.builder()
                        .hasAddedBank(true)
                        .tenantId("825056e0-5291-4956-af3c-42c05db3b25c")

                        .gender("male")
                        .firstName("Andreas")
                        .lastName("Bermig")
                        .maritalStatus("MARRIED")
                        .dateOfBirth(LocalDate.of(1960, 7, 7))
                        .nationality("DE")
                        .placeOfBirth("Berlin")
                        .childrenCount(0)
                        .housingSituation("purchased")
                        .numberOfCreditCard(0)

                        .addressStreet("Heller")
                        .addressHouseNumber("12")
                        .addressZipCode("41460")
                        .addressCity("Neuss")
                        .livingSince(LocalDate.of(2012, 12, 1))
                        .email("qatest+050701a@bonify.de")
                        .phone_number("015221490950")

                        .employmentType("Employee/Worker")
                        .nameOfEmployer("Dr. Andreas Christian Johannes Bermig")
                        .workContractStartDate(LocalDate.of(2020, 12, 25))
                        .addressStreetOfEmployer("Daimlerstr.")
                        .addressZipCodeOfEmployer("40212")
                        .addressCityOfEmployer("Düsseldorf")
                        .addressHouseNumberOfEmployer("4")

                        .employeeSalaryAmountLast1M(3.0)
                        .pensionAmountLast1M(0.0)
                        .childBenefitAmountLast1M(0.0)
                        .otherIncomeAmountLast1M(3.0)
                        .rentalIncomeLast1M(0.0)
                        .alimonyIncomeAmountLast1M(0.0)

                        .monthlyMortgage(-2000.0)
                        .monthlyLoanInstallments(-2.0)
                        .monthlyRent(-500.0)
                        .alimonyAmountLast1M(-4.0)
                        .monthlyPrivateHealthInsurance(-4.0)
                        .carInsuranceAmountLast1M(-5.0)

                        .probabilityOfDefault(0.0)
                        .bonimaScore(625)
                        .estimatedSchufaClass("B")
                        .build())
                .build();
    }

    public static UserPersonalData getUserPersonalData(String userId) {
        UserPersonalData userPersonalData = new UserPersonalData();
        userPersonalData.setUserUuid(userId);
        userPersonalData.setPersonalDetails(PersonalDetails.builder()
                .firstName("andreas")
                .lastName("bermig")
                .nationality(Nationality.DE)
                .gender(Gender.MALE)
                .familyStatus(FamilyStatus.SINGLE)
                .birthDate(LocalDate.of(1980, 3, 1))
                .numberOfChildren(2)
                .numberOfCreditCard(1)
                .housingType(HousingType.OWNER)
                .placeOfBirth("Berlin")
                .numberOfDependants(0)
                .build());
        userPersonalData.setEmploymentDetails(EmploymentDetails.builder()
                .city("Berlin")
                .employerName("Joonko AG")
                .employmentSince(new ShortDate(5, 2012))
                .postCode("10587")
                .employmentType(de.joonko.loan.offer.api.EmploymentType.REGULAR_EMPLOYED)
                .streetName("HardenbergStr.")
                .build());
        userPersonalData.setExpenses(Expenses.builder()
                .acknowledgedMortgages(0.0)
                .acknowledgedRent(900.0)
                .build());
        userPersonalData.setIncome(Income.builder().build());
        userPersonalData.setContactData(ContactData.builder()
                .city("Berlin")
                .houseNumber("11")
                .streetName("street")
                .postCode("10435")
                .email("test@email.com")
                .mobile("4915210362813")
                .build());
        userPersonalData.setCreditDetails(CreditDetails.builder()
                        .probabilityOfDefault(0.0)
                        .bonimaScore(124)
                        .estimatedSchufaClass("C")
                .build());
        return userPersonalData;
    }

    public static User getTestUser(String userId) {
        User user = new User();
        user.data = Map.of(
                "uid", 3196861L,
                "userVerifiedViaBankAccount", true,
                "addressCity", "berlin",
                "addressHouseNumber", "11",
                "addressStreet", "street",
                "addressZipCode", "10435",
                "gender", "male",
                "nationality", "DE",
                "placeOfBirth", "Hamburg",
                "familyStatus", "SINGLE"
        );
        user.firstName = "andreas";
        user.lastName = "bermig";
        user.mobilePhone = "+4915210362813";
        user.id = UUID.fromString(userId);
        user.email = "test@email.com";
        user.tenantId = UUID.fromString("825056e0-5291-4956-af3c-42c05db3b25c");
        return user;
    }
}
