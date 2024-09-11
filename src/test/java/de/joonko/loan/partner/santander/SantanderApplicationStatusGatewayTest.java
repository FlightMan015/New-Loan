package de.joonko.loan.partner.santander;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.partner.santander.stub.FaultException;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static de.joonko.loan.acceptoffer.domain.LoanApplicationStatus.PAID_OUT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
public class SantanderApplicationStatusGatewayTest {

    private SantanderApplicationStatusGateway santanderApplicationStatusGateway;

    private SantanderLoanApplicationStatusApiMapper mapper;
    private LoanOfferStoreService loanOfferStoreService;
    private SantanderStoreService santanderStoreService;
    private SantanderClientApi santanderClientApi;
    private LoanApplicationAuditTrailService loanApplicationAuditTrailService;

    @BeforeEach
    void setUp() {
        mapper = mock(SantanderLoanApplicationStatusApiMapper.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        santanderStoreService = mock(SantanderStoreService.class);
        santanderClientApi = mock(SantanderClientApi.class);
        loanApplicationAuditTrailService = mock(LoanApplicationAuditTrailService.class);

        santanderApplicationStatusGateway = new SantanderApplicationStatusGateway(mapper, loanOfferStoreService,
                santanderStoreService, santanderClientApi, loanApplicationAuditTrailService);
    }

    @SneakyThrows
    @Test
    void getStatus_happyPath(@Random OfferRequest offerRequest) {
        final var offer = LoanOfferStore
                .builder()
                .loanOfferId(offerRequest.getLoanOfferId())
                .applicationId(offerRequest.getApplicationId())
                .offer(LoanOffer.builder().durationInMonth(offerRequest.getDuration().getValue()).build())
                .offerStatus("APPROVED")
                .statusLastUpdateDate(OffsetDateTime.now().minusHours(2))
                .build();
        final var scbAntragId = "123";
        final var santanderStatusResponse = new ScbCapsBcoWSStub.GetKreditantragsstatusResult();
        final var status = new ScbCapsBcoWSStub.KreditantragsstatusXO();
        status.setStatus(ScbCapsBcoWSStub.AntragstatusType.ABGESCHLOSSEN);
        santanderStatusResponse.setAntragsstatus(status);
        final var argumentCaptor = ArgumentCaptor.forClass(ScbCapsBcoWSStub.GetKreditantragsstatusParams.class);

        // when
        when(mapper.toLoanApplicationStatusRequest(offerRequest)).thenReturn(new ScbCapsBcoWSStub.GetKreditantragsstatusParams());
        when(loanOfferStoreService.findByLoanOfferId(offerRequest.getLoanOfferId())).thenReturn(offer);
        when(santanderStoreService.getScbAntragId(offerRequest.getApplicationId(), offer.getOffer().getDurationInMonth())).thenReturn(scbAntragId);
        when(santanderClientApi.getApplicationStatus(any(ScbCapsBcoWSStub.GetKreditantragsstatusParams.class))).thenReturn(santanderStatusResponse);
        when(mapper.fromLoanApplicationStatusResponse(santanderStatusResponse)).thenReturn(PAID_OUT);


        final var response = santanderApplicationStatusGateway.getStatus(offerRequest);

        assertAll(
                () -> StepVerifier.create(response).consumeNextWith(actualResponse ->
                        assertEquals(PAID_OUT, actualResponse)
                ).verifyComplete(),
                () -> verify(mapper).toLoanApplicationStatusRequest(offerRequest),
                () -> verify(loanOfferStoreService).findByLoanOfferId(offer.getLoanOfferId()),
                () -> verify(santanderStoreService).getScbAntragId(offerRequest.getApplicationId(), offer.getOffer().getDurationInMonth()),
                () -> verify(santanderClientApi).getApplicationStatus(argumentCaptor.capture()),
                () -> verify(mapper).fromLoanApplicationStatusResponse(santanderStatusResponse),
                () -> assertEquals(scbAntragId, argumentCaptor.getValue().getScbAntragId())
        );
    }

    @SneakyThrows
    @Test
    void getStatus_errorCase(@Random OfferRequest offerRequest) {
        final var offer = LoanOfferStore
                .builder()
                .loanOfferId(offerRequest.getLoanOfferId())
                .applicationId(offerRequest.getApplicationId())
                .offer(LoanOffer.builder().durationInMonth(offerRequest.getDuration().getValue()).build())
                .offerStatus("APPROVED")
                .statusLastUpdateDate(OffsetDateTime.now().minusHours(2))
                .build();
        final var scbAntragId = "123";
        final var santanderStatusResponse = new ScbCapsBcoWSStub.GetKreditantragsstatusResult();
        final var status = new ScbCapsBcoWSStub.KreditantragsstatusXO();
        status.setStatus(ScbCapsBcoWSStub.AntragstatusType.ABGESCHLOSSEN);
        santanderStatusResponse.setAntragsstatus(status);

        // when
        when(mapper.toLoanApplicationStatusRequest(offerRequest)).thenReturn(new ScbCapsBcoWSStub.GetKreditantragsstatusParams());
        when(loanOfferStoreService.findByLoanOfferId(offerRequest.getLoanOfferId())).thenReturn(offer);
        when(santanderStoreService.getScbAntragId(offerRequest.getApplicationId(), offer.getOffer().getDurationInMonth())).thenReturn(scbAntragId);
        when(santanderClientApi.getApplicationStatus(any(ScbCapsBcoWSStub.GetKreditantragsstatusParams.class))).thenThrow(new FaultException("Exception"));
        doNothing().when(loanApplicationAuditTrailService).saveApplicationError(offer.getApplicationId(), "Exception", Bank.SANTANDER.label);


        final var response = santanderApplicationStatusGateway.getStatus(offerRequest);

        assertAll(
                () -> StepVerifier.create(response).expectNextCount(0).verifyComplete(),
                () -> verify(mapper).toLoanApplicationStatusRequest(offerRequest),
                () -> verify(loanOfferStoreService).findByLoanOfferId(offer.getLoanOfferId()),
                () -> verify(santanderStoreService).getScbAntragId(offerRequest.getApplicationId(), offer.getOffer().getDurationInMonth()),
                () -> verify(santanderClientApi).getApplicationStatus(any(ScbCapsBcoWSStub.GetKreditantragsstatusParams.class)),
                () -> verify(loanApplicationAuditTrailService).saveApplicationError(offer.getApplicationId(), "Exception", Bank.SANTANDER.label),
                () -> verifyNoMoreInteractions(mapper)
        );
    }
}
