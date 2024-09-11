package de.joonko.loan.offer.testdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.segment.CustomerData;
import de.joonko.loan.offer.api.*;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.user.states.*;
import de.joonko.loan.util.JsonUtil;

import io.fusionauth.domain.User;
import io.fusionauth.domain.api.UserResponse;

import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static de.joonko.loan.integrations.model.DistributionChannel.BONIFY;
import static de.joonko.loan.user.states.Status.MISSING_USER_INPUT;

public class LoanDemandTestData {

    public static final UUID BONIFY_TENANT_ID = UUID.fromString("825056e0-5291-4956-af3c-42c05db3b25c");
    public static final String BONIFY_USER_ID = "1613508";

    private static final String LOAN_PURPOSE = "new_car";

    public static UserStatesStore getUserStatesStoreForOffersReady(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setBonifyUserId(123L);
        userStatesStore.setLastRequestedLoanAmount(10000);
        userStatesStore.setLastRequestedPurpose(LOAN_PURPOSE);
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .state(Status.SUCCESS)
                .responseDateTime(OffsetDateTime.now().minusHours(1))
                .build());
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder()
                .state(Status.SUCCESS)
                .additionalFieldsForHighAmountAdded(true)
                .responseDateTime(OffsetDateTime.now().minusHours(1)).build());
        userStatesStore.add(OfferDataStateDetails.builder()
                .amount(10000)
                .purpose("phone")
                .applicationId("applicationId3")
                .state(Status.SUCCESS)
                .responseDateTime(OffsetDateTime.now().minusHours(1))
                .build());
        userStatesStore.add(OfferDataStateDetails.builder()
                .amount(10000)
                .purpose(LOAN_PURPOSE)
                .applicationId("applicationId1")
                .state(Status.SUCCESS)
                .responseDateTime(OffsetDateTime.now().minusHours(2))
                .build());
        userStatesStore.add(OfferDataStateDetails.builder()
                .amount(8000)
                .applicationId("applicationId2")
                .parentApplicationId("applicationId1")
                .state(Status.SUCCESS)
                .responseDateTime(OffsetDateTime.now().minusHours(1))
                .build());

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForOffersReadyWithMissingUserInfo(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setLastRequestedLoanAmount(10000);
        userStatesStore.setLastRequestedPurpose(LOAN_PURPOSE);
        userStatesStore.setTransactionalDataStateDetails(TransactionalDataStateDetails.builder()
                .state(Status.SUCCESS)
                .responseDateTime(OffsetDateTime.now().minusHours(1))
                .build());
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder()
                .state(MISSING_USER_INPUT)
                .responseDateTime(OffsetDateTime.now().minusDays(100)).build());
        userStatesStore.add(OfferDataStateDetails.builder()
                .amount(10000)
                .purpose(LOAN_PURPOSE)
                .applicationId("applicationId1")
                .parentApplicationId("parentApplication")
                .state(Status.SUCCESS)
                .responseDateTime(OffsetDateTime.now().minusHours(1))
                .build());
        userStatesStore.add(OfferDataStateDetails.builder()
                .amount(8000)
                .applicationId("applicationId2")
                .parentApplicationId("applicationId1")
                .state(Status.SUCCESS)
                .responseDateTime(OffsetDateTime.now().minusHours(1))
                .build());

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForFetchingAdditionalInfo(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(5))
                .responseFromDataSolution(OffsetDateTime.now())
                .responseDateTime(OffsetDateTime.now())
                .accountInternalId("35762965820")
                .state(Status.SUCCESS)
                .build();
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForMissingPersonalData(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(5))
                .responseFromDataSolution(OffsetDateTime.now())
                .responseDateTime(OffsetDateTime.now())
                .accountInternalId("35762965820")
                .state(Status.SUCCESS)
                .build();
        StateDetails userPersonalData = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusSeconds(5))
                .responseDateTime(OffsetDateTime.now().minusSeconds(3))
                .state(MISSING_USER_INPUT)
                .build();
        userStatesStore.setUserPersonalInformationStateDetails(userPersonalData);
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForFetchingOffers(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setDistributionChannel(BONIFY);
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(5))
                .responseFromDataSolution(OffsetDateTime.now())
                .responseDateTime(OffsetDateTime.now())
                .accountInternalId("35762965820")
                .state(Status.SUCCESS)
                .build();
        StateDetails userPersonalData = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusSeconds(5))
                .responseDateTime(OffsetDateTime.now())
                .state(Status.SUCCESS)
                .additionalFieldsForHighAmountAdded(true)
                .build();
        userStatesStore.setUserPersonalInformationStateDetails(userPersonalData);
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForBonifyUser(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setBonifyUserId(1L);
        userStatesStore.setDistributionChannel(BONIFY);
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .responseDateTime(OffsetDateTime.now())
                .state(Status.MISSING_SALARY_ACCOUNT)
                .accountInternalId("1234")
                .userVerifiedByBankAccount(true)
                .build();
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForBonifyOutdatedSalaryAccount(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setBonifyUserId(-1L);
        userStatesStore.setDistributionChannel(BONIFY);
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(1))
                .responseFromDataSolution(OffsetDateTime.now())
                .accountInternalId("35762965820")
                .state(Status.OUTDATED_SALARY_ACCOUNT)
                .build();
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForClassifyingTransactions(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setDistributionChannel(BONIFY);
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(5))
                .responseFromDataSolution(OffsetDateTime.now())
                .sentForClassification(OffsetDateTime.now())
                .state(Status.SENT_FOR_CLASSIFICATION)
                .build();
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForMissingSalaryAccountAfterClassification(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setBonifyUserId(123L);
        userStatesStore.setDistributionChannel(BONIFY);
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .accountInternalId("123")
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(5))
                .responseFromDataSolution(OffsetDateTime.now())
                .sentForClassification(OffsetDateTime.now())
                .responseDateTime(OffsetDateTime.now())
                .state(Status.MISSING_SALARY_ACCOUNT)
                .salaryAccountAdded(false)
                .build();
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForUserJourneyState(String userUuid, Integer loanAmount, String loanPurpose) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setBonifyUserId(123L);
        userStatesStore.setDistributionChannel(BONIFY);
        userStatesStore.setLastRequestedLoanAmount(loanAmount);
        userStatesStore.setLastRequestedPurpose(loanPurpose);

        return userStatesStore;
    }

    public static UserStatesStore getUserStatesStoreForQueryDataSolution(String userUuid) {
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userUuid);
        userStatesStore.setDistributionChannel(BONIFY);
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(5))
                .state(Status.QUERY_DATA_SOLUTION)
                .build();
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        return userStatesStore;
    }

    public static List<LoanOfferStore> getLoanOffers(String userUuid) {
        return List.of(
                LoanOfferStore.builder()
                        .userUUID(userUuid)
                        .isAccepted(true)
                        .offer(LoanOffer.builder().monthlyRate(BigDecimal.ONE)
                                .loanProvider(new LoanProvider(Bank.AION.label)).amount(10000).build())
                        .loanProviderReferenceNumber("loanProviderReferenceNumber1")
                        .applicationId("applicationId1").build(),
                LoanOfferStore.builder()
                        .userUUID(userUuid)
                        .offer(LoanOffer.builder().monthlyRate(BigDecimal.ONE)
                                .loanProvider(new LoanProvider(Bank.CONSORS.label)).amount(10000).build())
                        .isAccepted(false)
                        .loanProviderReferenceNumber("loanProviderReferenceNumber2")
                        .applicationId("applicationId1").build(),
                LoanOfferStore.builder()
                        .userUUID(userUuid)
                        .offer(LoanOffer.builder().monthlyRate(BigDecimal.TEN)
                                .loanProvider(new LoanProvider(Bank.SWK_BANK.label)).amount(8000).build())
                        .isAccepted(false)
                        .loanProviderReferenceNumber("loanProviderReferenceNumber3")
                        .applicationId("applicationId2")
                        .parentApplicationId("applicationId1").build(),
                LoanOfferStore.builder()
                        .userUUID(userUuid)
                        .offer(LoanOffer.builder().monthlyRate(BigDecimal.ONE)
                                .loanProvider(new LoanProvider(Bank.SWK_BANK.label)).amount(8000).build())
                        .isAccepted(false)
                        .loanProviderReferenceNumber("loanProviderReferenceNumber4")
                        .applicationId("applicationId2")
                        .parentApplicationId("applicationId1").build()
        );
    }

    public static void mockGettingBonifyUserFromAuthServer(WireMockServer mockServer, String userUuid) throws JsonProcessingException {
        User user = getUser(userUuid, true, false);
        mockGettingUserFromAuthServer(mockServer, user);
    }

    public static void mockGettingNonBonifyUserFromAuthServer(WireMockServer mockServer, String userUuid) throws JsonProcessingException {
        User user = getUser(userUuid, false, false);
        mockGettingUserFromAuthServer(mockServer, user);
    }

    private static void mockGettingUserFromAuthServer(WireMockServer mockServer, User user) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.get("/fusionAuth/api/user/" + user.id)
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withBody(JsonUtil.getObjectAsJsonString(new UserResponse(user)))));
    }

    public static void mockGettingUserFromSegment(WireMockServer mockServer) throws JsonProcessingException {
        mockServer.stubFor(
                WireMock.get("/v1/spaces/someSpaceId/collections/users/profiles/")
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.OK.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())
                                .withBody(JsonUtil.getObjectAsJsonString(new CustomerData()))));
    }

    public static void mockExceptionFromAuthServer(WireMockServer mockServer, String userUuid) {

        mockServer.stubFor(
                WireMock.get("/fusionAuth/api/user/" + userUuid)
                        .willReturn(aResponse()
                                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .withHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType())));
    }

    private static User getUser(String userUuid, boolean isBonify, boolean verifiedViaBankAccount) {
        User user = new User();
        user.active = true;
        user.id = UUID.fromString(userUuid);

        if (isBonify) {
            user.tenantId = BONIFY_TENANT_ID;
            user.data.put("uid", BONIFY_USER_ID);
        } else {
            user.tenantId = UUID.randomUUID();
        }

        if (verifiedViaBankAccount) {
            user.data.put("userVerifiedViaBankAccount", false);
        }

        return user;
    }

    public static UserTransactionalDataStore getTransactionalData(String userUuid) {
        UserTransactionalDataStore userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setUserUUID(userUuid);
        userTransactionalDataStore.setCreatedAt(LocalDateTime.now());
        userTransactionalDataStore.setFtsTransactionId("123");
        userTransactionalDataStore.setDacId("456");
        userTransactionalDataStore.setExpenses(Expenses.builder()
                .alimony(0.0)
                .insuranceAndSavings(86.37)
                .loanInstalments(257.38)
                .mortgages(0.0)
                .privateHealthInsurance(1900.0)
                .rent(962.0)
                .loanInstallmentsSwk(128.69)
                .vehicleInsurance(0.0)
                .acknowledgedRent(962.0)
                .acknowledgedMortgages(0.0)
                .monthlyLoanInstallmentsDeclared(200.0)
                .monthlyLifeCost(100.0)
                .build());
        userTransactionalDataStore.setIncome(Income.builder()
                .alimonyPayments(0.0)
                .childBenefits(0.0)
                .netIncome(10000.0)
                .otherRevenue(2500.0)
                .pensionBenefits(0.0)
                .rentalIncome(0.0)
                .acknowledgedNetIncome(0.0)
                .incomeDeclared(5000.0)
                .build());
        userTransactionalDataStore.setCustomDACData(CustomDACData.builder()
                .has3IncomeTags(false)
                .carInformation(false)
                .netIncomeHasGovSupport(false)
                .hasConsorsPreventionTags(false)
                .hasSwkPreventionTags(false)
                .totalIncomeInLast90Days(10000.0)
                .gamblingAmountInLast90Days(0.0)
                .cashWithdrawalsInLast90Days(0.0)
                .hasSalary(true)
                .build());
        userTransactionalDataStore.setAccountDetails(AccountDetails.builder()
                .isJointlyManaged(false)
                .currency("EUR")
                .nameOnAccount("MUSTERMANN, HARTMUT")
                .createdAt(LocalDateTime.now())
                .limit(1000.0)
                .iban("DE36500105177243855757")
                .bic("TESTDE88XXX")
                .balanceDate(LocalDate.of(2021, 1, 15))
                .balance(2123.0)
                .transactions(List.of())
                .build());
        CustomDacPersonalDetails customDacPersonalDetails = new CustomDacPersonalDetails();
        customDacPersonalDetails.setFirstName("Janusz");
        customDacPersonalDetails.setLastName("Doe");
        customDacPersonalDetails.setNumberOfChildren("0");
        customDacPersonalDetails.setNumberOfCreditCard(1);
        customDacPersonalDetails.setEmployerName("FinTecSystems GmbH");
        userTransactionalDataStore.setCustomDacPersonalDetails(customDacPersonalDetails);

        return userTransactionalDataStore;
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
        userPersonalInformationStore.setNumberOfChildren(2);
        userPersonalInformationStore.setNationality(Nationality.DE);
        userPersonalInformationStore.setPlaceOfBirth("Warsaw");

        return userPersonalInformationStore;
    }

    public static UserAdditionalInformationStore getAdditionalInformation(String userUuid) {
        UserAdditionalInformationStore userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setUserUUID(userUuid);
        userAdditionalInformationStore.setContactData(ContactData.builder()
                .mobile("201019511841")
                .livingSince(ShortDate.builder().year(2018).month(11).build())
                .postCode("90909").build());
        userAdditionalInformationStore.setEmploymentDetails(EmploymentDetails.builder()
                .employmentSince(ShortDate.builder().year(2020).month(12).build())
                .employmentType(EmploymentType.REGULAR_EMPLOYED)
                .postCode("10969")
                .employerName("FinTecSystems GmbH")
                .streetName("charlottenstrasse").houseNumber("57B")
                .city("Berlin").build());
        userAdditionalInformationStore.setPersonalDetails(PersonalDetails.builder()
                .numberOfCreditCard(1)
                .numberOfChildren(0)
                .housingType(HousingType.RENT)
                .nationality(Nationality.DE)
                .firstName("Mahmoud")
                .lastName("Mohamed")
                .placeOfBirth("Warsaw").build());

        return userAdditionalInformationStore;
    }

}
