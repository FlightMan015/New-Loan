package de.joonko.loan.webhooks;

import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.identification.service.IdentificationStatusService;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.metric.kyc.IdNowMetric;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.webhooks.idnow.IdNowWebHookController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class IdNowWebHookControllerTest {

    private IdNowWebHookController idNowWebHookController;

    private IdentificationStatusService identificationStatusService;
    private LoanDemandStoreService loanDemandStoreService;
    private LoanOfferStoreService loanOfferStoreService;
    private DataSolutionCommunicationManager dataSupportService;
    private IdentificationLinkService identificationLinkService;
    private IdNowMetric metric;

    @BeforeEach
    void setUp() {
        identificationStatusService = mock(IdentificationStatusService.class);
        loanDemandStoreService = mock(LoanDemandStoreService.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        dataSupportService = mock(DataSolutionCommunicationManager.class);
        identificationLinkService = mock(IdentificationLinkService.class);
        metric = mock(IdNowMetric.class);

        idNowWebHookController = new IdNowWebHookController(identificationStatusService, loanDemandStoreService, loanOfferStoreService,
                dataSupportService, identificationLinkService, metric);
    }

    @Test
    void shouldConsumeNotificationObject() {
        // given
        final var identification = WebhookFixtures.getWebhookNotificationRequest();
        final var identificationLink = getIdentificationLink();
        final var loanOfferStore = getLoanOfferStore();

        when(identificationLinkService.getByExternalIdentId("abcded")).thenReturn(identificationLink);
        when(loanOfferStoreService.findByLoanOfferId("offer2ddb2")).thenReturn(loanOfferStore);

        // when
        var handleWebhookMono = idNowWebHookController.handleIdNowWebHookNotification(identification);

        // then
        assertAll(
                () -> StepVerifier.create(handleWebhookMono).expectNextCount(1).verifyComplete(),
                () -> verify(identificationStatusService).saveWebhookNotification(identification),
                () -> verify(loanOfferStoreService).findByLoanOfferId(identificationLink.getOfferId()),
                () -> verify(metric).incrementKycCounter(anyString(), anyString()),
                () -> verify(loanOfferStoreService).save(loanOfferStore),
                () -> verify(dataSupportService).updateLoanOffers(anyString(), anyString(), anyString(), eq(OfferUpdateType.KYC_UPDATE))
        );
    }

    private LoanOfferStore getLoanOfferStore() {
        return LoanOfferStore.builder()
                .applicationId("5f845546b8335518fab2ddb2")
                .loanOfferId("offer2ddb2")
                .isAccepted(true)
                .userUUID("45546-5f845546b833-5518fab2d")
                .offer(LoanOffer.builder()
                        .loanProvider(new LoanProvider("AION"))
                        .build())
                .build();
    }

    private IdentificationLink getIdentificationLink() {
        return IdentificationLink.builder()
                .externalIdentId("abcded")
                .applicationId("5f845546b8335518fab2ddb2")
                .offerId("offer2ddb2")
                .insertTs(LocalDateTime.now())
                .build();
    }
}
