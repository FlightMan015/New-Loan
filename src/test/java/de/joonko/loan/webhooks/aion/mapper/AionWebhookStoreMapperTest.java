package de.joonko.loan.webhooks.aion.mapper;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.webhooks.aion.model.AionOfferStatus;
import de.joonko.loan.webhooks.aion.model.AionWebhookRequest;
import de.joonko.loan.webhooks.aion.model.AionWebhookType;
import de.joonko.loan.webhooks.aion.model.Payload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AionWebhookStoreMapperTest {

    private AionWebhookStoreMapper aionWebhookStoreMapper;

    @BeforeEach
    void setUp() {
        aionWebhookStoreMapper = new AionWebhookStoreMapperImpl();
    }

    @Test
    void mapToStoreType() {
        // given
        final var aionWebhookRequest = AionWebhookRequest.builder()
                .id("hf293f8")
                .sourceSystem("BPM")
                .eventDateTime(ZonedDateTime.now())
                .type(AionWebhookType.CASHLOAN_OPEN.getValue())
                .payload(Payload.builder()
                        .processInstanceId("cdb18fd5-529c-4dd7-a701-e31b434ec113")
                        .iban("DExxxx")
                        .customerId("3928639")
                        .status(AionOfferStatus.SUCCESS)
                        .customerInfo("Your loan was disbursed and money transferred to your account")
                        .build())
                .build();

        // when
        var aionWebhookStore = aionWebhookStoreMapper.map(aionWebhookRequest);

        // then
        assertAll(
                () -> assertNull(aionWebhookStore.getId()),
                () -> assertEquals(aionWebhookRequest.getId(), aionWebhookStore.getAionWebhookId()),
                () -> assertEquals(aionWebhookRequest.getSourceSystem(), aionWebhookStore.getSourceSystem()),
                () -> assertEquals(aionWebhookRequest.getEventDateTime().toInstant(), aionWebhookStore.getEventDateTime().toInstant()),
                () -> assertEquals(aionWebhookRequest.getType(), aionWebhookStore.getType()),
                () -> assertEquals(aionWebhookRequest.getPayload().getProcessInstanceId(), aionWebhookStore.getPayload().getProcessInstanceId()),
                () -> assertEquals(aionWebhookRequest.getPayload().getIban(), aionWebhookStore.getPayload().getIban()),
                () -> assertEquals(aionWebhookRequest.getPayload().getCustomerId(), aionWebhookStore.getPayload().getCustomerId()),
                () -> assertEquals(aionWebhookRequest.getPayload().getStatus(), aionWebhookStore.getPayload().getStatus()),
                () -> assertEquals(aionWebhookRequest.getPayload().getCustomerInfo(), aionWebhookStore.getPayload().getCustomerInfo())

        );
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void testMappingStatusOnboardingWebhookType(final AionWebhookType type, final AionOfferStatus status, final LoanApplicationStatus expectedStatus) {
        final var mappedStatus = aionWebhookStoreMapper.mapStatus(type, status);

        assertEquals(expectedStatus, mappedStatus);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        AionWebhookType.CASHLOAN_OPEN,
                        AionOfferStatus.SUCCESS,
                        LoanApplicationStatus.PAID_OUT),
                Arguments.of(
                        AionWebhookType.CASHLOAN_OPEN,
                        AionOfferStatus.FAILED,
                        LoanApplicationStatus.REJECTED),
                Arguments.of(
                        AionWebhookType.CASHLOAN_OPEN,
                        AionOfferStatus.MANUAL_AML,
                        LoanApplicationStatus.PENDING),
                Arguments.of(
                        AionWebhookType.ONBOARDING,
                        AionOfferStatus.SUCCESS,
                        LoanApplicationStatus.PENDING),
                Arguments.of(
                        AionWebhookType.ONBOARDING,
                        AionOfferStatus.FAILED,
                        LoanApplicationStatus.REJECTED),
                Arguments.of(
                        AionWebhookType.ONBOARDING,
                        AionOfferStatus.MANUAL_AML,
                        LoanApplicationStatus.PENDING)
        );
    }
}
