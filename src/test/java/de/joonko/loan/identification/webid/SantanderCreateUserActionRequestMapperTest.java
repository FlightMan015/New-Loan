package de.joonko.loan.identification.webid;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.IdentificationFixture;
import de.joonko.loan.identification.config.WebIdPropConfig;
import de.joonko.loan.identification.mapper.webid.SantanderCreateUserActionRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.webid.useractionrequest.CreateUserActionRequest;
import de.joonko.loan.identification.model.webid.useractionrequest.ProcessParameters;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.github.glytching.junit.extension.random.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class SantanderCreateUserActionRequestMapperTest extends BaseMapperTest {

    @Autowired
    public WebIdPropConfig webIdPropConfig;
    @Autowired
    private SantanderCreateUserActionRequestMapper santanderCreateUserActionRequestMapper;

    @MockBean
    private LoanOfferStoreService loanOfferStoreService;

    @Random
    private LoanOfferStore loanOfferStore;

    @BeforeEach
    void beforeEach() {
        webIdPropConfig.setFrontendHost("https://stg-loan-client.joonkostaging.de");
        webIdPropConfig.setSantanderMd("some-santander-md-param");
        loanOfferStore.setLoanProviderReferenceNumber("some-scbAntragId");
        Mockito.when(loanOfferStoreService.findByLoanOfferId(any())).thenReturn(loanOfferStore);
    }

    @Test
    void transactionId() {
        CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
        CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
        assertEquals(createIdentRequest.getApplicationId(), createUserActionRequest.getTransactionId());
    }

    @Test
    void preferredLanguage() {
        CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
        CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
        assertEquals(createIdentRequest.getLanguage(), createUserActionRequest.getPreferredLanguage());
    }

    @Test
    void actionType() {
        CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
        CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
        assertEquals("sig", createUserActionRequest.getActionType());
    }

    @Test
    void termsAndConditionsConfirmed() {
        CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
        CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
        assertEquals(true, createUserActionRequest.getTermsAndConditionsConfirmed());
    }

    @Nested
    class UserData {
        @Test
        void firstname() {
            CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
            CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
            assertEquals(createIdentRequest.getFirstName(), createUserActionRequest.getUser().getFirstname());
        }

        @Test
        void lastname() {
            CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
            CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
            assertEquals(createIdentRequest.getLastName(), createUserActionRequest.getUser().getLastname());
        }

        @Test
        void gender() {
            CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
            CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
            assertEquals(createIdentRequest.getGender(), createUserActionRequest.getUser().getSex());
        }

        @Test
        void dateOfBirth() {
            CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
            CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
            assertEquals(createIdentRequest.getBirthday(), createUserActionRequest.getUser().getDateOfBirth());
        }

        @Nested
        class Address {

            @Test
            void street() {
                CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
                CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
                assertEquals(createIdentRequest.getStreet(), createUserActionRequest.getUser().getAddress().getStreet());
            }

            @Test
            void streetNo() {
                CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
                CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
                assertEquals(createIdentRequest.getHouseNumber(), createUserActionRequest.getUser().getAddress().getStreetNo());
            }

            @Test
            void zip() {
                CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
                CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
                assertEquals(createIdentRequest.getZipCode(), createUserActionRequest.getUser().getAddress().getZip());
            }

            @Test
            void city() {
                CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
                CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
                assertEquals(createIdentRequest.getCity(), createUserActionRequest.getUser().getAddress().getCity());
            }

            @Test
            void country() {
                CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
                CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
                assertEquals(createIdentRequest.getCountry(), createUserActionRequest.getUser().getAddress().getCountry());
            }
        }

        @Nested
        class Contact {
            @Test
            void email() {
                CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
                CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
                assertEquals(createIdentRequest.getEmail(), createUserActionRequest.getUser().getContact().getEmail());
            }

            @Test
            void cellWithPlus() {
                CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
                createIdentRequest.setMobilePhone("+491234567890");
                CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
                assertEquals("+491234567890", createUserActionRequest.getUser().getContact().getCell());
            }

            @Test
            void cellWithoutPlus() {
                CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
                createIdentRequest.setMobilePhone("491234567890");
                CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
                assertEquals("+491234567890", createUserActionRequest.getUser().getContact().getCell());
            }
        }
    }

    @Test
    void mapProcessParameters() {
        // given
        CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
        String applicationId = createIdentRequest.getApplicationId();

        // when
        ProcessParameters processParameters = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest).getProcessParameters();

        // then
        assertAll(
                () -> assertEquals("https://stg-loan-client.joonkostaging.de/closure?transaction-number=" + applicationId + "&r=false", processParameters.getRedirectDeclineUrl(), "failed mapping redirectDeclineUrl"),
                () -> assertEquals("https://stg-loan-client.joonkostaging.de/closure?transaction-number=" + applicationId, processParameters.getRedirectUrl(), "failed mapping redirectUrl"),
                () -> assertEquals("https://stg-loan-client.joonkostaging.de/closure?transaction-number=" + applicationId + "&r=true", processParameters.getRedirectSkipQesUrl(), "failed mapping redirectSkipQesUrl"),
                () -> assertEquals("https://stg-loan-client.joonkostaging.de/closure?transaction-number=" + applicationId + "&r=true", processParameters.getRedirectCancelIdentUrl(), "failed mapping redirectCancelIdentUrl"),
                () -> assertEquals(10L, processParameters.getRedirectTime().longValue(), "failed mapping redirectTime"),
                () -> assertEquals("Loan Application", processParameters.getProductType(), "failed mapping productType")
        );
    }

    @Nested
    class CustomParameters {

        @Test
        void mdTi() {
            CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
            CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
            assertEquals("bc_some-scbAntragId", createUserActionRequest.getCustomParameters().getMdTi());
        }

        @Test
        void md() {
            CreateIdentRequest createIdentRequest = IdentificationFixture.getCreateIdentRequest(Bank.SANTANDER.name());
            CreateUserActionRequest createUserActionRequest = santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
            assertEquals("some-santander-md-param", createUserActionRequest.getCustomParameters().getMd());
        }
    }
}