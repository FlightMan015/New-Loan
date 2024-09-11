package de.joonko.loan.customer.support;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.config.CustomerSupportConfig;
import de.joonko.loan.customer.support.exception.*;
import de.joonko.loan.customer.support.mapper.KycStatusEventMapper;
import de.joonko.loan.customer.support.mapper.OfferAcceptedEventMapper;
import de.joonko.loan.customer.support.mapper.UserMapper;
import de.joonko.loan.customer.support.model.ApplicationAuditEvent;
import de.joonko.loan.customer.support.model.OfferReceivedEvent;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.domain.LoanDemand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.ConnectException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerSupportService {

    private final CustomerSupportConfig customerSupportConfig;
    private final CustomerSupportGateway customerSupportGateway;
    private final UserMapper userMapper;
    private final OfferAcceptedEventMapper offerAcceptedEventMapper;
    private final KycStatusEventMapper kycStatusEventMapper;
    private final LoanOfferStoreService loanOfferStoreService;

    @Async
    @Retryable(value = {UserCreationException.class, ConnectException.class}, maxAttemptsExpression = "#{${retry.maxretry}}", backoff = @Backoff(delayExpression = "#{${retry.fixedBackoffseconds}}"))
    public void createUser(LoanDemand loanDemand) {
        customerSupportGateway.createUser(userMapper.mapToUser(loanDemand));
    }

    @Recover
    private void handleUserCreationException(UserCreationException exception, LoanDemand loanDemand) {
        log.error("User creation failed  for application id " + loanDemand.getLoanApplicationId(), exception);

    }

    @Recover
    private void handleConnectionException(Throwable ex) {
        log.error("Error connecting to customer support service", ex);
    }

    @Async
    @Retryable(value = {OfferReceivedEventException.class, ConnectException.class}, maxAttemptsExpression = "#{${retry.maxretry}}", backoff = @Backoff(delayExpression = "#{${retry.fixedBackoffseconds}}"))
    public void pushOfferReceivedEvent(String email, String id) {
        customerSupportGateway.pushOffersReceivedEvent(mapToOfferReceivedEvent(email, id));
    }

    @Recover
    private void handleOfferReceivedEventException(OfferReceivedEventException exception, String email, String id) {
        log.error("Offer Received event failed  for application id {}", id, exception);
    }

    @Async
    @Retryable(value = {CustomerSupportEventException.class, ConnectException.class}, maxAttemptsExpression = "#{${retry.maxretry}}", backoff = @Backoff(delayExpression = "#{${retry.fixedBackoffseconds}}"))
    public void pushApplicationAuditEvent(String email, String id) {
        customerSupportGateway.pushApplicationAuditEvent(mapToApplicationAuditEvent(email, id));
    }

    @Recover
    private void handleApplicationAuditEventException(CustomerSupportEventException exception, String email, String id) {
        log.error("No Offer event failed  for application id {}", id, exception);

    }

    @Async
    @Retryable(value = {AcceptOfferEventException.class, ConnectException.class}, maxAttemptsExpression = "#{${retry.maxretry}}", backoff = @Backoff(delayExpression = "#{${retry.fixedBackoffseconds}}"))
    public void pushAcceptOfferEvent(OfferStatus offerStatus, OfferRequest offerRequest) {
        LoanOfferStore loanOfferStore = loanOfferStoreService.findByLoanOfferId(offerRequest.getLoanOfferId());
        customerSupportGateway.pushOfferAcceptedEvent(offerAcceptedEventMapper.mapToOfferAcceptedEvent(offerRequest.getApplicationId(), offerStatus, loanOfferStore.getOffer()));
    }

    @Recover
    private void handleOfferAcceptedEventException(AcceptOfferEventException exception, OfferStatus offerStatus, OfferRequest offerRequest) {
        log.error("Accept Offer event failed for application id {} and loan Offer id {}", offerRequest.getApplicationId(), offerRequest.getLoanOfferId(), exception);
    }

    @Async
    @Retryable(value = {KycStatusEventException.class, ConnectException.class}, maxAttemptsExpression = "#{${retry.maxretry}}", backoff = @Backoff(delayExpression = "#{${retry.fixedBackoffseconds}}"))
    public void pushKycStatus(String applicationId, LoanOffer offer, String kycUrl, String kycStatus, String loanBankReferenceNumber, String kycReason) {
        customerSupportGateway.pushKycStatus(kycStatusEventMapper.mapToKycStatusEvent(applicationId, offer, kycUrl, kycStatus, loanBankReferenceNumber, kycReason));
    }

    @Recover
    private void handleKycStatusEventException(KycStatusEventException exception, String applicationId, LoanOffer offer, String kycUrl, String kycStatus, String loanBankReferenceNumber, String kycReason) {
        log.error("Kyc status event failed for application id {}", applicationId, exception);
    }

    private OfferReceivedEvent mapToOfferReceivedEvent(String email, String applicationId) {
        return OfferReceivedEvent.builder()
                .email(email)
                .createdAt(System.currentTimeMillis())
                .offersLink(getDashBoardUrl(customerSupportConfig.getDashBoardOffersEndpoint(), applicationId))
                .build();
    }

    private ApplicationAuditEvent mapToApplicationAuditEvent(String email, String applicationId) {
        return ApplicationAuditEvent.builder()
                .email(email)
                .createdAt(System.currentTimeMillis())
                .auditLink(getDashBoardUrl(customerSupportConfig.getDashBoardApplicationAuditEndpoint(), applicationId))
                .build();
    }

    private String getDashBoardUrl(String endpoint, String applicationId) {
        return UriComponentsBuilder.fromHttpUrl(customerSupportConfig.getDashBoardUrl())
                .path(endpoint)
                .queryParam("loanApplicationId", applicationId)
                .build().toUriString();
    }
}
