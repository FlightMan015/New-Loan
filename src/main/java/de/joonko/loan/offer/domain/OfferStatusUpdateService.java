package de.joonko.loan.offer.domain;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.metric.OfferStatusMetric;
import de.joonko.loan.metric.kyc.KycMetric;

import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static de.joonko.loan.acceptoffer.domain.LoanApplicationStatus.UNDEFINED;

@Slf4j
@RequiredArgsConstructor
@Service
public class OfferStatusUpdateService {

    private final LoanOfferStoreService loanOfferStoreService;
    private final OfferStatusMetric offerStatusMetric;
    private final DataSolutionCommunicationManager dataSolutionCommunicationManager;
    private final KycMetric kycMetric;

    public Mono<LoanOfferStore> updateOfferStatus(final @NotNull OfferRequest request, final @NotNull LoanApplicationStatus newStatus) {
        return Mono.just(newStatus)
                .filter(status -> UNDEFINED != status)
                .doOnNext(status -> log.info("Updating offer status for loanOfferId: {}, loanProvider: {}, with status: {}", request.getLoanOfferId(), request.getLoanProvider(), status))
                .flatMap(status -> loanOfferStoreService.updateOfferStatus(request.getLoanOfferId(), status.name()))
                .doOnNext(updatedLoanOffer -> offerStatusMetric.incrementOfferStatusCounter(updatedLoanOffer.getOfferStatus(), request.getLoanProvider()))
                .doOnNext(updatedLoanOffer -> dataSolutionCommunicationManager.updateLoanOffers(updatedLoanOffer.getUserUUID(), updatedLoanOffer.getApplicationId(), updatedLoanOffer.getLoanOfferId(), OfferUpdateType.LOAN_STATUS_UPDATE));
    }

    public Mono<LoanOfferStore> updateKycStatus(final @NotNull LoanOfferStore loanOfferStore, final @NotNull LoanApplicationStatus newStatus) {
        return Mono.just(newStatus)
                .doOnNext(status -> log.info("Updating offer KYC status for loanOfferId: {}, loanProvider: {}, with status: {}", loanOfferStore.getLoanOfferId(), loanOfferStore.getOffer().getLoanProvider().getName(), status))
                .flatMap(status -> loanOfferStoreService.updateKycStatus(loanOfferStore.getLoanOfferId(), status.name()))
                .doOnNext(updatedLoanOffer -> kycMetric.incrementKycCounter(newStatus.name(), loanOfferStore.getOffer().getLoanProvider().getName(), loanOfferStore.getKycProvider()))
                .doOnNext(updatedLoanOffer -> dataSolutionCommunicationManager.updateLoanOffers(updatedLoanOffer.getUserUUID(), updatedLoanOffer.getApplicationId(), updatedLoanOffer.getLoanOfferId(), OfferUpdateType.KYC_UPDATE));
    }
}
