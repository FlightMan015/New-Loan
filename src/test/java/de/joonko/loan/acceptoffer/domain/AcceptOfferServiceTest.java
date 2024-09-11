package de.joonko.loan.acceptoffer.domain;

import com.google.common.collect.ImmutableList;

import de.joonko.loan.acceptoffer.api.AcceptOfferRequest;
import de.joonko.loan.acceptoffer.api.AcceptOfferResponse;
import de.joonko.loan.acceptoffer.api.OfferRequestMapper;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandRequestService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.db.vo.OfferAcceptedEnum;
import de.joonko.loan.offer.api.GetOffersMapper;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.solaris.SolarisStoreService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.glytching.junit.extension.exception.ExpectedException;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith({RandomBeansExtension.class, MockitoExtension.class})
class AcceptOfferServiceTest {

    private AcceptOfferService acceptOfferService;


    @Random
    private OfferStatus offerStatus;

    @Random
    private AcceptOfferRequest acceptOfferRequest;

    @Random
    private OfferRequest offerRequest;

    @Random
    private LoanDemand loanDemand;

    @Random
    private LoanOfferStore loanOfferStore;

    @Mock
    private AcceptOfferGateway consorsGateway;

    @Mock
    private AcceptOfferGateway auxmoneyGateway;

    @Mock
    private DataSupportService dataSupportService;

    @Mock
    private LoanOfferStoreService loanOfferStoreService;

    @Mock
    private SolarisStoreService solarisStoreService;

    @Mock
    private LoanApplicationAuditTrailService loanApplicationAuditTrailService;

    @Mock
    private LoanDemandRequestService loanDemandRequestService;

    @Mock
    private GetOffersMapper getOffersMapper;

    @Mock
    private OfferRequestMapper offerRequestMapper;

    @BeforeEach
    void setup() {
        acceptOfferService = new AcceptOfferService(ImmutableList.of(consorsGateway, auxmoneyGateway), loanOfferStoreService,
                solarisStoreService, loanApplicationAuditTrailService, loanDemandRequestService, getOffersMapper, offerRequestMapper);
    }

    @Test
    void acceptOfferNoOfferFound() {
        when(loanOfferStoreService.findById(acceptOfferRequest.getLoanOfferId())).thenReturn(Mono.empty());

        final var actualOfferStatus = acceptOfferService.acceptOffer(acceptOfferRequest, "test user id");

        assertAll(
                () -> StepVerifier.create(actualOfferStatus)
                        .expectSubscription()
                        .expectNextCount(0)
                        .verifyComplete(),
                () -> verify(loanOfferStoreService).findById(acceptOfferRequest.getLoanOfferId()),
                () -> verifyNoMoreInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(loanApplicationAuditTrailService, loanDemandRequestService, offerRequestMapper, consorsGateway, getOffersMapper)
        );
    }

    @Test
    void acceptOfferNotBelongingToUser() {
        when(loanOfferStoreService.findById(acceptOfferRequest.getLoanOfferId())).thenReturn(Mono.just(LoanOfferStore.builder().userUUID("otherUserID").build()));

        final var actualOfferStatus = acceptOfferService.acceptOffer(acceptOfferRequest, "userID");

        assertAll(
                () -> StepVerifier.create(actualOfferStatus)
                        .expectSubscription()
                        .expectError(IllegalStateException.class)
                        .verify(),
                () -> verify(loanOfferStoreService).findById(acceptOfferRequest.getLoanOfferId()),
                () -> verifyNoMoreInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(loanApplicationAuditTrailService, loanDemandRequestService, offerRequestMapper, consorsGateway, getOffersMapper)
        );
    }

    @Test
    void acceptOfferSuccessCase() {
        final var loanOffer = loanOfferStore.toBuilder()
                .userUUID("userID")
                .build();
        final var loanDemandRequest = LoanDemandRequest.builder()
                .applicationId(loanOffer.getApplicationId())
                .build();
        offerRequest = offerRequest.toBuilder().loanProvider("Consors Finanz").build();
        when(loanOfferStoreService.findById(acceptOfferRequest.getLoanOfferId())).thenReturn(Mono.just(loanOffer));
        when(loanDemandRequestService.findLoanDemandRequest(loanOffer.getApplicationId())).thenReturn(Mono.just(loanDemandRequest));
        when(offerRequestMapper.fromRequest(loanOffer)).thenReturn(offerRequest);
        when(getOffersMapper.fromRequest(loanDemandRequest, loanOffer.getApplicationId(), loanOffer.getUserUUID())).thenReturn(loanDemand);
        when(loanOfferStoreService.updateAcceptedStatus(offerRequest.getLoanOfferId(), "loanProviderReferenceNumber", OfferAcceptedEnum.USER)).thenReturn(Mono.just(loanOffer));
        when(consorsGateway.getBank()).thenReturn(Bank.CONSORS);
        when(consorsGateway.acceptOffer(any())).thenReturn(Mono.just(offerStatus));
        when(consorsGateway.getLoanProviderReferenceNumber(anyString(), anyString())).thenReturn(Mono.just("loanProviderReferenceNumber"));
        when(offerRequestMapper.toResponse(offerStatus)).thenReturn(AcceptOfferResponse.builder().build());

        final var actualOfferStatus = acceptOfferService.acceptOffer(acceptOfferRequest, "userID");

        assertAll(
                () -> StepVerifier.create(actualOfferStatus)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(loanOfferStoreService).findById(acceptOfferRequest.getLoanOfferId()),
                () -> verify(loanApplicationAuditTrailService).acceptOfferRequestReceived(loanOffer.getApplicationId(), loanOffer.getOffer().getLoanProvider().getName()),
                () -> verify(loanDemandRequestService).findLoanDemandRequest(loanOffer.getApplicationId()),
                () -> verify(offerRequestMapper).fromRequest(loanOffer),
                () -> verify(getOffersMapper).fromRequest(loanDemandRequest, loanOffer.getApplicationId(), loanOffer.getUserUUID()),
                () -> verify(loanOfferStoreService).updateAcceptedStatus(offerRequest.getLoanOfferId(), "loanProviderReferenceNumber", OfferAcceptedEnum.USER),
                () -> verify(loanApplicationAuditTrailService).acceptOfferRequestServedSuccess(offerRequest.getApplicationId(), offerRequest.getLoanOfferId(), offerRequest.getLoanProvider()),
                () -> verify(offerRequestMapper).toResponse(offerStatus),
                () -> verifyNoMoreInteractions(loanOfferStoreService, loanApplicationAuditTrailService, loanDemandRequestService, offerRequestMapper, getOffersMapper)
        );
    }


    @Test
    void acceptOfferByUserSuccessCase() {
        final var loanOfferId = "offerId";
        final var userId = "userID";
        final var loanOffer = loanOfferStore.toBuilder()
                .loanOfferId(loanOfferId)
                .userUUID(userId)
                .isAccepted(null)
                .build();
        final var loanDemandRequest = LoanDemandRequest.builder()
                .applicationId(loanOffer.getApplicationId())
                .build();
        offerRequest = offerRequest.toBuilder().loanProvider("Consors Finanz").build();
        when(loanOfferStoreService.findById(loanOfferId)).thenReturn(Mono.just(loanOffer));
        when(loanDemandRequestService.findLoanDemandRequest(loanOffer.getApplicationId())).thenReturn(Mono.just(loanDemandRequest));
        when(offerRequestMapper.fromRequest(loanOffer)).thenReturn(offerRequest);
        when(getOffersMapper.fromRequest(loanDemandRequest, loanOffer.getApplicationId(), loanOffer.getUserUUID())).thenReturn(loanDemand);
        when(loanOfferStoreService.updateAcceptedStatus(offerRequest.getLoanOfferId(), "loanProviderReferenceNumber", OfferAcceptedEnum.USER)).thenReturn(Mono.just(loanOffer));
        when(consorsGateway.getBank()).thenReturn(Bank.CONSORS);
        when(consorsGateway.acceptOffer(any())).thenReturn(Mono.just(offerStatus));
        when(consorsGateway.getLoanProviderReferenceNumber(anyString(), anyString())).thenReturn(Mono.just("loanProviderReferenceNumber"));

        final var response = acceptOfferService.acceptOfferByUser(loanOfferId, userId);

        assertAll(
                () -> StepVerifier.create(response)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(loanOfferStoreService).findById(loanOfferId),
                () -> verify(loanDemandRequestService).findLoanDemandRequest(loanOffer.getApplicationId()),
                () -> verify(offerRequestMapper).fromRequest(loanOffer),
                () -> verify(getOffersMapper).fromRequest(loanDemandRequest, loanOffer.getApplicationId(), loanOffer.getUserUUID()),
                () -> verify(loanOfferStoreService).updateAcceptedStatus(offerRequest.getLoanOfferId(), "loanProviderReferenceNumber", OfferAcceptedEnum.USER),
                () -> verify(loanApplicationAuditTrailService).acceptOfferRequestServedSuccess(offerRequest.getApplicationId(), offerRequest.getLoanOfferId(), offerRequest.getLoanProvider()),
                () -> verifyNoMoreInteractions(loanOfferStoreService, loanDemandRequestService, offerRequestMapper, getOffersMapper)
        );
    }


    @Test
    void acceptOfferByInternalUserNoOfferFound() {
        final var offerId = "offerId";

        when(loanOfferStoreService.findById(offerId)).thenReturn(Mono.empty());

        final var actualOfferStatus = acceptOfferService.acceptOfferByInternalUser(offerId);

        assertAll(
                () -> StepVerifier.create(actualOfferStatus)
                        .expectSubscription()
                        .expectNextCount(0)
                        .verifyComplete(),
                () -> verify(loanOfferStoreService).findById(offerId),
                () -> verifyNoMoreInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(loanApplicationAuditTrailService, loanDemandRequestService, offerRequestMapper, consorsGateway, getOffersMapper)
        );
    }

    @Test
    void acceptOfferByInternalUserOffAlreadyAccepted() {
        final var offerId = "offerId";

        when(loanOfferStoreService.findById(offerId)).thenReturn(Mono.just(LoanOfferStore.builder().isAccepted(true).loanProviderReferenceNumber("a").build()));

        final var actualOfferStatus = acceptOfferService.acceptOfferByInternalUser(offerId);

        assertAll(
                () -> StepVerifier.create(actualOfferStatus)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(loanOfferStoreService).findById(offerId),
                () -> verifyNoMoreInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(loanApplicationAuditTrailService, loanDemandRequestService, offerRequestMapper, consorsGateway, getOffersMapper)
        );
    }

    @Test
    void acceptOfferByInternalUserSuccessCase() {
        final var offerId = "offerId";
        final var loanOffer = loanOfferStore.toBuilder()
                .loanOfferId(offerId)
                .isAccepted(false)
                .userUUID("userID")
                .build();
        final var loanDemandRequest = LoanDemandRequest.builder()
                .applicationId(loanOffer.getApplicationId())
                .build();
        offerRequest = offerRequest.toBuilder().loanProvider("Consors Finanz").build();
        when(loanOfferStoreService.findById(offerId)).thenReturn(Mono.just(loanOffer));
        when(loanDemandRequestService.findLoanDemandRequest(loanOffer.getApplicationId())).thenReturn(Mono.just(loanDemandRequest));
        when(offerRequestMapper.fromRequest(loanOffer)).thenReturn(offerRequest);
        when(getOffersMapper.fromRequest(loanDemandRequest, loanOffer.getApplicationId(), loanOffer.getUserUUID())).thenReturn(loanDemand);
        when(loanOfferStoreService.updateAcceptedStatus(offerRequest.getLoanOfferId(), "loanProviderReferenceNumber", OfferAcceptedEnum.INTERNAL)).thenReturn(Mono.just(loanOffer));

        when(consorsGateway.getBank()).thenReturn(Bank.CONSORS);
        when(consorsGateway.acceptOffer(any())).thenReturn(Mono.just(offerStatus));
        when(consorsGateway.getLoanProviderReferenceNumber(anyString(), anyString())).thenReturn(Mono.just("loanProviderReferenceNumber"));

        final var actualOfferStatus = acceptOfferService.acceptOfferByInternalUser(offerId);

        assertAll(
                () -> StepVerifier.create(actualOfferStatus)
                        .expectSubscription()
                        .expectNextCount(1)
                        .verifyComplete(),
                () -> verify(loanOfferStoreService).findById(offerId),
                () -> verify(loanApplicationAuditTrailService).acceptOfferByInternalUserRequestReceived(loanOffer.getApplicationId(), loanOffer.getOffer().getLoanProvider().getName()),
                () -> verify(loanDemandRequestService).findLoanDemandRequest(loanOffer.getApplicationId()),
                () -> verify(offerRequestMapper).fromRequest(loanOffer),
                () -> verify(getOffersMapper).fromRequest(loanDemandRequest, loanOffer.getApplicationId(), loanOffer.getUserUUID()),
                () -> verify(loanOfferStoreService).updateAcceptedStatus(offerRequest.getLoanOfferId(), "loanProviderReferenceNumber", OfferAcceptedEnum.INTERNAL),
                () -> verify(loanApplicationAuditTrailService).acceptOfferRequestServedSuccess(offerRequest.getApplicationId(), offerRequest.getLoanOfferId(), offerRequest.getLoanProvider()),
                () -> verifyNoMoreInteractions(loanOfferStoreService, loanApplicationAuditTrailService, loanDemandRequestService, offerRequestMapper, getOffersMapper)
        );
    }

}
