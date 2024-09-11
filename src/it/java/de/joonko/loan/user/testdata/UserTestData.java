package de.joonko.loan.user.testdata;

import de.joonko.loan.offer.api.ContactData;
import de.joonko.loan.offer.api.CreditDetails;
import de.joonko.loan.offer.api.EmploymentDetails;
import de.joonko.loan.offer.api.EmploymentType;
import de.joonko.loan.offer.api.Expenses;
import de.joonko.loan.offer.api.FamilyStatus;
import de.joonko.loan.offer.api.Gender;
import de.joonko.loan.offer.api.HousingType;
import de.joonko.loan.offer.api.Income;
import de.joonko.loan.offer.api.Nationality;
import de.joonko.loan.offer.api.PersonalDetails;
import de.joonko.loan.offer.api.ShortDate;
import de.joonko.loan.offer.api.model.UserPersonalDetails;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.UserStatesStore;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class UserTestData {
    public static UserPersonalDetails getUserPersonalDetails() {
        return UserPersonalDetails.builder()
                .contactData(ContactData.builder()
                        .streetName("charlottenstrasse")
                        .houseNumber("97")
                        .city("Berlin")
                        .email("mail@mail.com")
                        .mobile("201019511841")
                        .livingSince(ShortDate.builder().year(2018).month(11).build())
                        .postCode("90909").build())
                .employmentDetails(EmploymentDetails.builder()
                        .employmentSince(ShortDate.builder().year(2020).month(12).build())
                        .postCode("10969")
                        .employerName("FinTecSystems GmbH")
                        .streetName("charlottenstrasse")
                        .employmentType(EmploymentType.REGULAR_EMPLOYED)
                        .city("Berlin").build())
                .expenses(Expenses.builder()
                        .acknowledgedRent(765.0)
                        .acknowledgedMortgages(123.0)
                        .monthlyLifeCost(444.44)
                        .monthlyLoanInstallmentsDeclared(123.25)
                        .build())
                .creditDetails(CreditDetails.builder()
                        .creditCardLimitDeclared(101.23)
                        .build())
                .income(Income.builder()
                        .incomeDeclared(999.99)
                        .build())
                .personalDetails(PersonalDetails.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .familyStatus(FamilyStatus.SINGLE)
                        .numberOfCreditCard(1)
                        .numberOfChildren(0)
                        .housingType(HousingType.RENT)
                        .gender(Gender.MALE)
                        .nationality(Nationality.DE)
                        .birthDate(LocalDate.of(1970, 10, 15))
                        .numberOfDependants(0)
                        .placeOfBirth("Warsaw")
                        .countryOfBirth("DE")
                        .build())
                .build();
    }

    public static UserStatesStore getUserStatesStore(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().requestDateTime(OffsetDateTime.now()).build());
        return userStatesStore;
    }

    public static UserPersonalInformationStore getPersonalInformation(String userUuid) {
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID(userUuid);
        userPersonalInformationStore.setAddressCity("Berlin");
        userPersonalInformationStore.setFirstName("Janusz");
        userPersonalInformationStore.setAddressStreet("23");
        userPersonalInformationStore.setEmail("asd@asd.com");
        userPersonalInformationStore.setBirthDate(LocalDate.of(1990, 1, 1));
        userPersonalInformationStore.setLastName("Doe");
        userPersonalInformationStore.setFamilyStatus(FamilyStatus.SINGLE);
        userPersonalInformationStore.setAddressHouseNumber("12");
        userPersonalInformationStore.setGender(Gender.MALE);
        userPersonalInformationStore.setNationality(Nationality.DE);
        userPersonalInformationStore.setPlaceOfBirth("Warsaw");

        return userPersonalInformationStore;
    }
}
