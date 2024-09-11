package de.joonko.loan.user.api.testdata;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.offer.api.*;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.userdata.api.model.*;
import de.joonko.loan.userdata.infrastructure.draft.model.UserContactStore;
import de.joonko.loan.userdata.infrastructure.draft.model.UserDraftInformationStore;
import de.joonko.loan.userdata.infrastructure.draft.model.UserEmploymentStore;
import de.joonko.loan.userdata.infrastructure.draft.model.UserPersonalStore;

import java.time.LocalDate;

public class UserDataITTestData {

    public UserAdditionalInformationStore buildUserAdditionalInformationStore(String userUuid) {
        UserAdditionalInformationStore userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setUserUUID(userUuid);
        userAdditionalInformationStore.setPersonalDetails(PersonalDetails.builder()
                .gender(Gender.MALE)
                .firstName("Mahmoud")
                .lastName("Mohamed")
                .familyStatus(FamilyStatus.MARRIED)
                .birthDate(LocalDate.of(1980, 3, 1))
                .nationality(Nationality.DE)
                .placeOfBirth("Warsaw")
                .countryOfBirth("Poland")
                .numberOfChildren(0)
                .numberOfDependants(2)
                .housingType(HousingType.RENT)
                .numberOfCreditCard(1).build());
        userAdditionalInformationStore.setEmploymentDetails(EmploymentDetails.builder()
                .employmentType(EmploymentType.REGULAR_EMPLOYED)
                .employerName("FinTecSystems GmbH")
                .employmentSince(ShortDate.builder().year(2020).month(12).build())
                .streetName("charlottenstrasse").houseNumber("57B")
                .postCode("10969")
                .city("Berlin")
                .houseNumber("13").build());
        userAdditionalInformationStore.setExpenses(Expenses.builder()
                .mortgages(0.0)
                .insuranceAndSavings(86.37)
                .loanInstalments(257.38)
                .rent(962.0)
                .alimony(0.0)
                .privateHealthInsurance(1900.0)
                .loanInstallmentsSwk(128.69)
                .vehicleInsurance(0.0)
                .monthlyLifeCost(100.0)
                .monthlyLoanInstallmentsDeclared(200.0)
                .acknowledgedMortgages(0.0)
                .acknowledgedRent(962.0).build());
        userAdditionalInformationStore.setIncome(Income.builder()
                .netIncome(10000.0)
                .pensionBenefits(0.0)
                .childBenefits(0.0)
                .otherRevenue(2500.0)
                .rentalIncome(0.0)
                .alimonyPayments(0.0)
                .acknowledgedNetIncome(0.0)
                .incomeDeclared(5000.0).build());
        userAdditionalInformationStore.setContactData(ContactData.builder()
                .streetName("Hardenbergstraße")
                .houseNumber("32")
                .postCode("10623")
                .city("Berlin")
                .livingSince(ShortDate.builder()
                        .month(11)
                        .year(2020)
                        .build())
                .previousAddress(LoanDemandFixtures.getPreviousAddress())
                .email("someone@joonko.io")
                .mobile("491748273421011").build());

        return userAdditionalInformationStore;
    }

    public UserDraftInformationStore buildUserDraftInformationStore(String userUuid) {
        final var userDraftInformationStore = new UserDraftInformationStore();
        userDraftInformationStore.setUserUUID(userUuid);
        userDraftInformationStore.setUserPersonal(UserPersonalStore.builder()
                .gender(Gender.MALE)
                .firstName("john")
                .lastName("smith")
                .familyStatus(FamilyStatus.MARRIED)
                .birthDate(LocalDate.of(1950, 10, 11))
                .nationality(Nationality.DE)
                .placeOfBirth("Berlin")
                .countryOfBirth("DE").build());
        userDraftInformationStore.setUserContact(UserContactStore.builder()
                .streetName("charlottenstrasse")
                .houseNumber("97")
                .postCode("90909")
                .city("Berlin")
                .livingSince(ShortDate.builder().year(2018).month(11).build())
                .email("mail@mail.com")
                .mobile("201019511841").build());
        userDraftInformationStore.setUserEmployment(UserEmploymentStore.builder()
                .employerName("bonify").build());

        return userDraftInformationStore;
    }

    public UserTransactionalDataStore buildUserTransactionalDataStore(String userUuid) {
        final var userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setUserUUID(userUuid);
        userTransactionalDataStore.setAccountDetails(AccountDetails.builder()
                .nameOnAccount("MUSTERMANN, HARTMUT")
                .iban("DE62888888880012345678")
                .bic("TESTDE88XXX")
                .bankName("SANTANDER").build());

        return userTransactionalDataStore;
    }

    public UserDataRequest buildValidUserDataRequest() {
        final var userDataReq = new UserDataRequest();
        userDataReq.setUserPersonal(UserPersonal.builder()
                .gender(Gender.MALE)
                .firstName("john")
                .lastName("smith")
                .familyStatus(FamilyStatus.MARRIED)
                .birthDate(LocalDate.of(1950, 10, 11))
                .nationality(Nationality.DE)
                .placeOfBirth("Berlin")
                .countryOfBirth("DE").build());
        userDataReq.setUserContact(UserContact.builder()
                .streetName("charlottenstrasse")
                .houseNumber("97")
                .postCode("90909")
                .city("Berlin")
                .livingSince(ShortDate.builder().year(2018).month(11).build())
                .email("mail@mail.com")
                .mobile("201019511841").build());
        userDataReq.setUserEmployment(UserEmployment.builder()
                .employmentType(EmploymentType.REGULAR_EMPLOYED)
                .employerName("FinTecSystems GmbH")
                .employmentSince(ShortDate.builder().year(2020).month(12).build())
                .streetName("charlottenstrasse").houseNumber("57B")
                .postCode("10969")
                .city("Berlin")
                .houseNumber("13")
                .taxId("12345678901").build());
        userDataReq.setUserHousing(UserHousing.builder()
                .housingType(HousingType.OWNER)
                .numberOfDependants(2)
                .numberOfChildren(2)
                .acknowledgedMortgages(1300.0)
                .build());
        userDataReq.setUserCredit(UserCredit.builder()
                .userExpenses(new UserExpenses())
                .build());

        return userDataReq;
    }

    public UserStatesStore buildUserStatesStore(String userUuid) {
        var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder().build());

        return userStatesStore;
    }
}
