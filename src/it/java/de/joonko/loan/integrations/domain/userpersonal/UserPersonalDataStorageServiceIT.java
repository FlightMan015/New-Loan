package de.joonko.loan.integrations.domain.userpersonal;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalDataStorageService;
import de.joonko.loan.offer.api.*;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class})
@SpringBootTest
class UserPersonalDataStorageServiceIT {

    @Autowired
    private UserPersonalDataStorageService userPersonalDataStorageService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void getUserData() {
        // given
        final var userUuid = "4536b6c6-1c80-4387-b947-684fc833b9c7";
        final var userAdditional = buildAdditionalInfo(userUuid);
        mongoTemplate.insert(userAdditional);

        // when
        final var actualUserData = userPersonalDataStorageService.getUserPersonalData(userUuid);

        // then
        StepVerifier.create(actualUserData)
                .consumeNextWith(actual -> assertAll(
                        () -> assertEquals(userAdditional.getUserUUID(), actual.getUserUuid()),
                        () -> assertEquals(userAdditional.getCreditDetails(), actual.getCreditDetails()),
                        () -> assertEquals(userAdditional.getPersonalDetails(), actual.getPersonalDetails()),
                        () -> assertEquals(userAdditional.getContactData(), actual.getContactData()),
                        () -> assertEquals(userAdditional.getEmploymentDetails(), actual.getEmploymentDetails()),
                        () -> assertEquals(userAdditional.getIncome(), actual.getIncome()),
                        () -> assertEquals(userAdditional.getExpenses(), actual.getExpenses())
                ))
                .verifyComplete();
    }

    private UserAdditionalInformationStore buildAdditionalInfo(String userUuid) {
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
}
