package de.joonko.loan.offer.domain;

import de.joonko.loan.acceptoffer.api.OfferRequestMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatusService;
import de.joonko.loan.avro.dto.dls_reports_data.DigitalLoansReportsDataTopic;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static de.joonko.loan.acceptoffer.domain.LoanApplicationStatus.getIntermediateStatuses;

@Slf4j
@AllArgsConstructor
@Service
public class OffersStatusSyncingService {

    private final LoanApplicationStatusService applicationStatusService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final OfferRequestMapper mapper;
    private final OfferStatusUpdateService offerStatusUpdateService;

    public Mono<Void> sync(final @NotNull Set<Bank> banks) {
        return Mono.just(banks)
                .filter(loanProviders -> !loanProviders.isEmpty())
                .flatMapMany(loanProviders -> loanOfferStoreService.findByOfferStatusAndLoanProvider(loanProviders, getIntermediateStatuses()))
                .filter(this::isValidForSyncing)
                .flatMap(this::getStatusAndSave)
                .doOnError(e -> log.error("Error while syncing offers status", e))
                .then();
    }

    public Mono<Void> syncFromDS(final @NotNull DigitalLoansReportsDataTopic digitalLoansReportsDataTopic) {
        return Flux.fromIterable(digitalLoansReportsDataTopic.getOffers())
                .flatMap(offer -> {
                    final var bank = Bank.fromValue(offer.getPartnerId());
                    if (bank.isEmpty() || !Bank.getStatusReportBanks().contains(bank.get())) {
                        log.error("Statue Report: Received a bank - {} status report not acknowledged in the system for status reporting", offer.getPartnerId());
                        return Mono.empty();
                    }
                    return Mono.just(offer);
                })
                .flatMap(offer -> loanOfferStoreService.getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDurationAndLoanProvider(offer.getReferenceId(), offer.getDuration(), Bank.valueOf(offer.getPartnerId()).label)
                        .map(mapper::fromRequest)
                        .flatMap(request -> offerStatusUpdateService.updateOfferStatus(request, mapStatus(offer.getStatus(), offer.getPartnerId())))
                )
                .then();
    }

    private LoanApplicationStatus mapStatus(final String status, final String loanProvider) {
        if (Bank.CONSORS.name().equals(loanProvider)) {
            switch (status) {
                case "SS":
                case "ANN":
                    return LoanApplicationStatus.CANCELED;
                case "ENC":
                case "SOL":
                    return LoanApplicationStatus.PAID_OUT;
                case "REF":
                    return LoanApplicationStatus.REJECTED;
                default:
                    return LoanApplicationStatus.PENDING;
            }
        }
        if (Bank.SWK_BANK.name().equals(loanProvider)) {
            switch (status) {
                case "abgelehnt":
                    return LoanApplicationStatus.REJECTED;
                case "ausgezahlt":
                case "erledigt (zurückgezahlt)":
                    return LoanApplicationStatus.PAID_OUT;
                default:
                    return LoanApplicationStatus.PENDING;
            }
        }
        throw new RuntimeException(String.format("Not recognised bank - %s for status report", loanProvider));
    }

    private boolean isValidForSyncing(LoanOfferStore loanOffer) {
        if (loanOffer.getStatusLastUpdateDate() == null) {
            return true;
        }
        return Optional.of(loanOffer.getStatusLastUpdateDate())
                .filter(dateTime -> dateTime.isAfter(OffsetDateTime.now().minusDays(30)))
                .isPresent();
    }

    private Mono<LoanOfferStore> getStatusAndSave(final LoanOfferStore loanOfferStore) {
        return Mono.just(loanOfferStore)
                .map(mapper::fromRequest)
                .zipWhen(applicationStatusService::getStatus)
                .flatMap(tuple -> offerStatusUpdateService.updateOfferStatus(tuple.getT1(), tuple.getT2()));
    }
}
