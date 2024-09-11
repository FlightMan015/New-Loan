package de.joonko.loan.messaging;

import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.avro.dto.dls_reports_data.DigitalLoansReportsDataTopic;
import de.joonko.loan.avro.dto.dls_reports_data.Offer;
import de.joonko.loan.avro.dto.salary_account.Account;
import de.joonko.loan.avro.dto.salary_account.Balance;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountRequest;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import de.joonko.loan.avro.dto.salary_account.Transactions;
import de.joonko.loan.avro.dto.user_additional_information.UserAdditionalInformation;
import de.joonko.loan.avro.dto.user_details.Data;
import de.joonko.loan.avro.dto.user_details.User;
import de.joonko.loan.avro.dto.user_details.create.UserCreateEvent;
import de.joonko.loan.avro.dto.user_details.delete.Event;
import de.joonko.loan.avro.dto.user_details.delete.UserDeleteEvent;
import de.joonko.loan.avro.dto.user_details.login.UserLoginEvent;
import de.joonko.loan.avro.dto.user_details.update.UserUpdateEvent;
import de.joonko.loan.common.utils.DateTimeConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class MessageTestData {

    private static final long USER_ID = 32897469L;
    public static final String TENANT_ID = "2f20a660-f0f2-4ca5-9fe6-b24b52cd1070";
    private static final OffsetDateTime OFFSET_DATE_TIME_1 = OffsetDateTime.of(LocalDate.of(2021, 1, 28), LocalTime.MIDNIGHT, ZoneOffset.ofHours(1));
    private static final OffsetDateTime OFFSET_DATE_TIME_2 = OffsetDateTime.of(LocalDate.of(2021, 2, 20), LocalTime.now(), ZoneOffset.ofHours(1));

    public static QuerySalaryAccountRequest getQuerySalaryAccountRequestTestData(String userUuid) {
        return QuerySalaryAccountRequest.newBuilder()
                .setUserId(USER_ID)
                .setUserUUID(userUuid)
                .setMaxLastUpdateDate(DateTimeConverter.toLong(OFFSET_DATE_TIME_1))
                .setMinTransactionBookingDate(DateTimeConverter.toLong(OFFSET_DATE_TIME_2))
                .build();
    }

    public static QuerySalaryAccountResponse getQuerySalaryAccountResponseTestData(String userUuid) {
        Account account = Account.newBuilder()
                .setBankName("Postbank Muenchen")
                .setBic("")
                .setCountryId("DE")
                .setDescription("description")
                .setHolder("Postbank Muenchen")
                .setIban("")
                .build();
        Balance balance = Balance.newBuilder()
                .setBalance(2123.0)
                .setCurrency("EUR")
                .setDate("2021-01-15")
                .setLimit(1000.0)
                .build();
        Transactions transactions = Transactions.newBuilder()
                .setAmount(-300.0)
                .setBookingDate(LocalDate.of(2020, 10, 30).toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.MAX))
                .setCurrency("EUR")
                .setBookingPurpose("KREDITKARTENABRECHNUNG HVB MasterCard Gold Kontakt 5483 47XX XXXX 4653 BC Abrechnung 10/16")
                .setCurrencyId("")
                .setPartnerAccountIBAN("")
                .setPartnerName("Partner name")
                .build();

        return QuerySalaryAccountResponse.newBuilder()
                .setAccount(account)
                .setBalance(balance)
                .setUserId(USER_ID)
                .setUserUUID(userUuid)
                .setTransactions(List.of(transactions))
                .setCreatedAt(DateTimeConverter.toLong(OFFSET_DATE_TIME_1))
                .setAccountInternalId("623958905L")
                .build();
    }

    public static DigitalLoansReportsDataTopic getDigitalLoansReportsDataTopicTestData() {
        return DigitalLoansReportsDataTopic.newBuilder()
                .setOffers(List.of(
                        Offer.newBuilder()
                                .setTimestamp(5678)
                                .setEffInterestRate(2.8)
                                .setAmount(1000)
                                .setStatus("REF")
                                .setReferenceId("1")
                                .setPartnerId("CONSORS")
                                .setDuration(12)
                                .setPartnerId("2")
                                .build(),
                        Offer.newBuilder()
                                .setTimestamp(6789)
                                .setEffInterestRate(1.9)
                                .setAmount(1000)
                                .setStatus("ausgezahlt")
                                .setReferenceId("3")
                                .setPartnerId("SWK")
                                .setDuration(12)
                                .setPartnerId("2")
                                .build()
                ))
                .build();
    }

    public static UserDeleteEvent getUserDeletionEventTestData(String userUuid) {
        return UserDeleteEvent.newBuilder()
                .setEvent(Event.newBuilder()
                        .setType("user.delete")
                        .setCreateInstant(1611151925947L)
                        .setId(UUID.randomUUID().toString())
                        .setTenantId(TENANT_ID)
                        .setUser(getUser(userUuid))
                        .build())
                .build();
    }

    public static UserCreateEvent getUserCreationTestData(String userUuid) {
        return UserCreateEvent.newBuilder()
                .setEvent(de.joonko.loan.avro.dto.user_details.create.Event.newBuilder()
                        .setType("user.create")
                        .setCreateInstant(1611151925947L)
                        .setId(UUID.randomUUID().toString())
                        .setTenantId(TENANT_ID)
                        .setUser(getUser(userUuid))
                        .build())
                .build();
    }

    public static UserLoginEvent getUserLoginEventTestData(String userUuid) {
        return UserLoginEvent.newBuilder()
                .setEvent(de.joonko.loan.avro.dto.user_details.login.Event.newBuilder()
                        .setType("user.login.success")
                        .setCreateInstant(1614639955594L)
                        .setId(UUID.randomUUID().toString())
                        .setTenantId(TENANT_ID)
                        .setUser(getUser(userUuid))
                        .setApplicationId("ba15fadf-183c-447f-b9d1-b65c9894b260")
                        .setAuthenticationType("PASSWORD")
                        .setConnectorId("e3306678-a53a-4964-9040-1c96f36dda72")
                        .build())
                .build();
    }

    public static UserUpdateEvent getUserUpdateTestData(String userUuid) {
        return UserUpdateEvent.newBuilder()
                .setEvent(de.joonko.loan.avro.dto.user_details.update.Event.newBuilder()
                        .setType("user.update")
                        .setCreateInstant(1611151925947L)
                        .setId(UUID.randomUUID().toString())
                        .setTenantId(TENANT_ID)
                        .setUser(getUser(userUuid))
                        .build())
                .build();
    }

    private static User getUser(String userUuid) {
        return User.newBuilder()
                .setActive(true)
                .setBirthDate("1965-02-27")
                .setConnectorId("b125a070-71c4-11e5-a837-0800200c9a69")
                .setData(Data.newBuilder()
                        .setAddressCity("Berlin")
                        .setAddressHouseNumber("13")
                        .setAddressStreet("charlotten")
                        .setAddressZipCode("10969")
                        .setFamilyStatus("single")
                        .setGender("female")
                        .setPlaceOfBirth("Sohag")
                        .setNationality("DE")
                        .build())
                .setEmail("new@gmail.com")
                .setFirstName("new")
                .setId(userUuid)
                .setInsertInstant(1610709770802L)
                .setLastLoginInstant(1610709829279L)
                .setLastName("new")
                .setLastUpdateInstant(1610709821694L)
                .setMobilePhone("1234567890")
                .setPasswordChangeRequired(false)
                .setPasswordLastUpdateInstant(1610709821694L)
                .setTenantId(TENANT_ID)
                .setTwoFactorDelivery("None")
                .setTwoFactorEnabled(false)
                .setUsernameStatus("ACTIVE")
                .setVerified(true)
                .build();
    }

    public static DacAccountSnapshot getDacAccountSnapshotTestData(String userUuid) {
        return DacAccountSnapshot.newBuilder()
                .setDacId("dacid")
                .setUserUUID(userUuid)
                .build();
    }

    public static UserAdditionalInformation getUserAdditionalInformationTestData(String userUuid) {
        return UserAdditionalInformation.newBuilder()
                .setUserUUID(userUuid)
                .build();
    }
}

