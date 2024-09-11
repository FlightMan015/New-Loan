package de.joonko.loan.partner.consors;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.partner.consors.auth.JwtToken;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.partner.consors.mapper.ConsorsAcceptOfferApiMapper;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferRequest;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.LinkRelation;
import de.joonko.loan.partner.consors.model.SubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class ConsorsAcceptOfferGatewayTest {

    private ConsorsAcceptOfferGateway consorsAcceptOfferGateway;

    private ConsorsAcceptOfferApiMapper consorsAcceptOfferApiMapper;
    private ConsorsStoreService consorsStoreService;
    private LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private ConsorsClient consorsClient;
    private ConsorsNotificationSenderService consorsNotificationSenderService;
    private LoanOfferStoreService loanOfferStoreService;

    @BeforeEach
    void setUp() {
        consorsAcceptOfferApiMapper = mock(ConsorsAcceptOfferApiMapper.class);
        consorsStoreService = mock(ConsorsStoreService.class);
        loanApplicationAuditTrailService = mock(LoanApplicationAuditTrailService.class);
        consorsClient = mock(ConsorsClient.class);
        consorsNotificationSenderService = mock(ConsorsNotificationSenderService.class);
        loanOfferStoreService = mock(LoanOfferStoreService.class);

        consorsAcceptOfferGateway = new ConsorsAcceptOfferGateway(consorsAcceptOfferApiMapper, consorsStoreService, loanApplicationAuditTrailService,
                consorsClient, consorsNotificationSenderService, loanOfferStoreService);
    }

    @Test
    void acceptAnOffer() {
        // given
        final var request = ConsorsAcceptOfferRequest.builder().build();
        final var applicationId = "3f92h8923f";
        final var offerId = "9328539";
        final var jwtToken = new JwtToken("jwtToken");
        final var linkRelation = new LinkRelation();
        final var acceptOfferResponse = new ConsorsAcceptOfferResponse();
        final var loanProviderOfferId = "123";
        acceptOfferResponse.setSubscriptionStatus(SubscriptionStatus.APPROVED);
        acceptOfferResponse.setContractIdentifier(loanProviderOfferId);
        when(consorsStoreService.getFinalizeSubscriptionLinkForApplicationId(applicationId)).thenReturn(linkRelation);
        when(consorsClient.getToken(applicationId)).thenReturn(Mono.just(new JwtToken("jwtToken")));
        when(consorsClient.finalizeSubscription(jwtToken, linkRelation, request, applicationId)).thenReturn(Mono.just(acceptOfferResponse));

        // when
        var actualAccepted = consorsAcceptOfferGateway.callApi(request, applicationId, offerId);

        // then
        assertAll(
                () -> StepVerifier.create(actualAccepted).expectNextCount(1).verifyComplete(),
                () -> verify(loanApplicationAuditTrailService).acceptOfferRequestSent(applicationId, Bank.CONSORS),
                () -> verify(consorsStoreService).getFinalizeSubscriptionLinkForApplicationId(applicationId),
                () -> verify(consorsClient).getToken(applicationId),
                () -> verify(consorsClient).finalizeSubscription(jwtToken, linkRelation, request, applicationId),
                () -> verify(consorsStoreService).saveAcceptedOffer(applicationId, acceptOfferResponse),
                () -> verify(loanApplicationAuditTrailService).acceptOfferResponseReceivedConsors(applicationId, acceptOfferResponse),
                () -> verify(consorsNotificationSenderService).sendEmailWithAccountSnapshot(acceptOfferResponse.getContractIdentifier(), applicationId)
        );
    }

    @Test
    void storeRefusedOffer() {
        // given
        final var request = ConsorsAcceptOfferRequest.builder().build();
        final var applicationId = "3f92h8923f";
        final var offerId = "9328539";
        final var jwtToken = new JwtToken("jwtToken");
        final var linkRelation = new LinkRelation();
        final var acceptOfferResponse = new ConsorsAcceptOfferResponse();
        final var loanProviderOfferId = "123";
        acceptOfferResponse.setSubscriptionStatus(SubscriptionStatus.REFUSED);
        acceptOfferResponse.setContractIdentifier(loanProviderOfferId);
        when(consorsStoreService.getFinalizeSubscriptionLinkForApplicationId(applicationId)).thenReturn(linkRelation);
        when(consorsClient.getToken(applicationId)).thenReturn(Mono.just(new JwtToken("jwtToken")));
        when(consorsClient.finalizeSubscription(jwtToken, linkRelation, request, applicationId)).thenReturn(Mono.just(acceptOfferResponse));
        when(loanOfferStoreService.updateOffer(offerId, loanProviderOfferId, LoanApplicationStatus.REJECTED)).thenReturn(Mono.just(LoanOfferStore.builder().build()));

        // when
        var actualAccepted = consorsAcceptOfferGateway.callApi(request, applicationId, offerId);

        // then
        assertAll(
                () -> StepVerifier.create(actualAccepted).verifyError(),
                () -> verify(loanApplicationAuditTrailService).acceptOfferRequestSent(applicationId, Bank.CONSORS),
                () -> verify(consorsStoreService).getFinalizeSubscriptionLinkForApplicationId(applicationId),
                () -> verify(consorsClient).getToken(applicationId),
                () -> verify(consorsClient).finalizeSubscription(jwtToken, linkRelation, request, applicationId),
                () -> verify(loanOfferStoreService).updateOffer(offerId, loanProviderOfferId, LoanApplicationStatus.REJECTED),
                () -> verify(loanApplicationAuditTrailService).acceptOfferErrorResponseReceived(eq(applicationId), anyString(), eq(Bank.CONSORS))
        );
    }
}
