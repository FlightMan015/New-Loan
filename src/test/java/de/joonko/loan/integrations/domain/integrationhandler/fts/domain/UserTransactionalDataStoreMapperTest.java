package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import de.joonko.loan.offer.api.*;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTransactionalDataStoreMapperTest {

    private UserTransactionalDataStoreMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserTransactionalDataStoreMapperImpl();
    }

    @Test
    void mapEmploymentTypeWhenMissing() {
        // given
        var employmentDetails = EmploymentDetails.builder().build();
        var customDacData = CustomDACData.builder()
                .hasSalary(true)
                .build();
        var customPersonalData = new CustomDacPersonalDetails();
        customPersonalData.setEmployerName("Google");

        // when
        var actualEmploymentDetails = mapper.customMapping(employmentDetails, customPersonalData, customDacData);

        // then
        assertAll(
                () -> assertEquals("Google", actualEmploymentDetails.getEmployerName()),
                () -> assertEquals(EmploymentType.REGULAR_EMPLOYED, actualEmploymentDetails.getEmploymentType())
        );
    }

    @Test
    void doNotMapEmploymentTypeWhenAlreadyExist() {
        // given
        var employmentDetails = EmploymentDetails.builder()
                .employmentType(EmploymentType.REGULAR_EMPLOYED)
                .employerName("Google")
                .build();
        var customDacData = CustomDACData.builder().build();
        var customPersonalData = new CustomDacPersonalDetails();

        // when
        var actualEmploymentDetails = mapper.customMapping(employmentDetails, customPersonalData, customDacData);

        // then
        assertAll(
                () -> assertEquals("Google", actualEmploymentDetails.getEmployerName()),
                () -> assertEquals(EmploymentType.REGULAR_EMPLOYED, actualEmploymentDetails.getEmploymentType())
        );
    }

    @Test
    void mapToKycRelatedPersonalDetails() {
        // given
        AccountDetails accountDetails = AccountDetails.builder()
                .iban("DE36500105177243855757")
                .bic("TESTDE88XXX")
                .build();
        UserPersonalInformationStore userPersonal = new UserPersonalInformationStore();
        userPersonal.setFirstName("John");
        userPersonal.setLastName("Doe");

        // when
        var actualKycRelatedPersonalDetails = mapper.mapToKycRelatedPersonalDetails(accountDetails, userPersonal);

        // then
        assertAll(
                () -> assertEquals(userPersonal.getLastName() + ", " + userPersonal.getFirstName(), actualKycRelatedPersonalDetails.getNameOnAccount()),
                () -> assertEquals(accountDetails.getBic(), actualKycRelatedPersonalDetails.getBic()),
                () -> assertEquals(accountDetails.getIban(), actualKycRelatedPersonalDetails.getIban())
        );
    }
}
