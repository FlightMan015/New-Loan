package de.joonko.loan.integrations.segment;

import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.webclient.LocalMockServerRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SegmentClientTest {

    private SegmentClient segmentClient;
    private SegmentPropertiesConfig segmentPropertiesConfig;
    private ApiMetric apiMetric;

    private static WebClient webClient;
    private static LocalMockServerRunner mockServerRunner;
    private static SegmentClientMocks segmentClientMocks;

    @BeforeAll
    static void beforeAll() {
        mockServerRunner = new LocalMockServerRunner();
        segmentClientMocks = new SegmentClientMocks(mockServerRunner.getServer());
        webClient = mockServerRunner.getWebClient();
    }

    @BeforeEach
    void setUp() {
        mockServerRunner.resetAll();
        segmentPropertiesConfig = mock(SegmentPropertiesConfig.class);
        apiMetric = mock(ApiMetric.class);
        segmentClient = new SegmentClient(webClient, segmentPropertiesConfig, apiMetric);
    }

    @AfterAll
    static void afterAll() {
        mockServerRunner.stop();
    }

    @Test
    void get200WhenGettingUserTraits() {
        // given
        String spaceId = "space-id";
        String id = "email:test@test.com";
        when(segmentPropertiesConfig.getPersonasSpaceId()).thenReturn(spaceId);
        segmentClientMocks.fake200WhenGettingUserTraits(spaceId, id);

        // when
        var monoCustomerData = segmentClient.getUserTraits(id);

        // then
        StepVerifier.create(monoCustomerData)
                .consumeNextWith(customerData -> assertAll(
                        () -> assertTrue(customerData.getTraits().getHasAddedBank()),
                        () -> assertEquals("825056e0-5291-4956-af3c-42c05db3b25c", customerData.getTraits().getTenantId()),

                        () -> assertEquals("male", customerData.getTraits().getGender()),
                        () -> assertEquals("Andreas", customerData.getTraits().getFirstName()),
                        () -> assertEquals("Bermig", customerData.getTraits().getLastName()),
                        () -> assertEquals("MARRIED", customerData.getTraits().getMaritalStatus()),
                        () -> assertEquals(LocalDate.of(1960, 7, 7), customerData.getTraits().getDateOfBirth()),
                        () -> assertEquals("DE", customerData.getTraits().getNationality()),
                        () -> assertEquals("Berlin", customerData.getTraits().getPlaceOfBirth()),
                        () -> assertEquals(0, customerData.getTraits().getChildrenCount()),
                        () -> assertEquals("purchased", customerData.getTraits().getHousingSituation()),
                        () -> assertEquals(0, customerData.getTraits().getNumberOfCreditCard()),

                        () -> assertEquals(937, customerData.getTraits().getBonimaScore()),
                        () -> assertEquals("G", customerData.getTraits().getEstimatedSchufaClass()),
                        () -> assertEquals(0.32145, customerData.getTraits().getProbabilityOfDefault()),

                        () -> assertEquals("Heller", customerData.getTraits().getAddressStreet()),
                        () -> assertEquals("12", customerData.getTraits().getAddressHouseNumber()),
                        () -> assertEquals("41460", customerData.getTraits().getAddressZipCode()),
                        () -> assertEquals("Neuss", customerData.getTraits().getAddressCity()),
                        () -> assertEquals(LocalDate.of(2012, 12, 1), customerData.getTraits().getLivingSince()),
                        () -> assertEquals("qatest+050701a@bonify.de", customerData.getTraits().getEmail()),
                        () -> assertEquals("015221490950", customerData.getTraits().getPhone_number()),

                        () -> assertEquals("OTHER", customerData.getTraits().getEmploymentType()),
                        () -> assertEquals("Dr. Andreas Christian Johannes Bermig", customerData.getTraits().getNameOfEmployer()),
                        () -> assertEquals(LocalDate.of(2020, 12, 25), customerData.getTraits().getWorkContractStartDate()),
                        () -> assertEquals("Daimlerstr.", customerData.getTraits().getAddressStreetOfEmployer()),
                        () -> assertEquals("40212", customerData.getTraits().getAddressZipCodeOfEmployer()),
                        () -> assertEquals("Düsseldorf", customerData.getTraits().getAddressCityOfEmployer()),
                        () -> assertEquals("4", customerData.getTraits().getAddressHouseNumberOfEmployer()),

                        () -> assertEquals(3.0, customerData.getTraits().getEmployeeSalaryAmountLast1M()),
                        () -> assertEquals(0.0, customerData.getTraits().getPensionAmountLast1M()),
                        () -> assertEquals(0.0, customerData.getTraits().getChildBenefitAmountLast1M()),
                        () -> assertEquals(3.0, customerData.getTraits().getOtherIncomeAmountLast1M()),
                        () -> assertEquals(0.0, customerData.getTraits().getRentalIncomeLast1M()),
                        () -> assertEquals(0.0, customerData.getTraits().getAlimonyIncomeAmountLast1M()),

                        () -> assertEquals(2000.0, customerData.getTraits().getMonthlyMortgage()),
                        () -> assertEquals(0.0, customerData.getTraits().getMonthlyInsurance()),
                        () -> assertEquals(0.0, customerData.getTraits().getMonthlyLoanInstallments()),
                        () -> assertEquals(500.0, customerData.getTraits().getMonthlyRent()),
                        () -> assertEquals(0.0, customerData.getTraits().getAlimonyAmountLast1M()),
                        () -> assertEquals(0.0, customerData.getTraits().getMonthlyPrivateHealthInsurance()),
                        () -> assertEquals(0.0, customerData.getTraits().getCarInsuranceAmountLast1M()))
                ).verifyComplete();
        verify(apiMetric).incrementStatusCounter(HttpStatus.OK, ApiComponent.SEGMENT, ApiName.GET_USER_TRAITS);
    }

    @Test
    void get401WhenGettingUserTraits() {
        // given
        String spaceId = "space-id";
        String id = "email:test@test.com";
        when(segmentPropertiesConfig.getPersonasSpaceId()).thenReturn(spaceId);
        doNothing().when(apiMetric).incrementStatusCounter(HttpStatus.UNAUTHORIZED, ApiComponent.SEGMENT, ApiName.GET_USER_TRAITS);
        segmentClientMocks.fake401WhenGettingUserTraits(spaceId, id);

        // when
        var monoCustomerData = segmentClient.getUserTraits(id);

        // then
        assertAll(
                () -> StepVerifier.create(monoCustomerData).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.UNAUTHORIZED, ApiComponent.SEGMENT, ApiName.GET_USER_TRAITS)
        );
    }

    @Test
    void get404WhenGettingUserTraits() {
        // given
        String spaceId = "space-id";
        String id = "email:test@test.com";
        when(segmentPropertiesConfig.getPersonasSpaceId()).thenReturn(spaceId);
        segmentClientMocks.fake404WhenGettingUserTraits(spaceId, id);

        // when
        var monoCustomerData = segmentClient.getUserTraits(id);

        // then
        assertAll(
                () -> StepVerifier.create(monoCustomerData).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.NOT_FOUND, ApiComponent.SEGMENT, ApiName.GET_USER_TRAITS)
        );
    }

    @Test
    void get500WhenGettingUserTraits() {
        // given
        String spaceId = "space-id";
        String id = "email:test@test.com";
        when(segmentPropertiesConfig.getPersonasSpaceId()).thenReturn(spaceId);
        segmentClientMocks.fake500WhenGettingUserTraits(spaceId, id);

        // when
        var monoCustomerData = segmentClient.getUserTraits(id);

        // then
        assertAll(
                () -> StepVerifier.create(monoCustomerData).verifyError(),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.INTERNAL_SERVER_ERROR, ApiComponent.SEGMENT, ApiName.GET_USER_TRAITS)
        );
    }
}
