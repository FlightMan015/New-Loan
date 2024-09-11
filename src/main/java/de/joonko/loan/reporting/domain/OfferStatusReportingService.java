package de.joonko.loan.reporting.domain;

import de.joonko.loan.common.utils.DateTimeConverter;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.reporting.api.OfferStatusMapper;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;

@Slf4j
@AllArgsConstructor
@Service
public class OfferStatusReportingService {

    private final UserStatesStoreService userStatesStoreService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final OfferStatusMapper offerStatusMapper;

    public Mono<List<OfferStatus>> get(UUID distributionChannelId, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        return Mono.just(distributionChannelId)
                .flatMap(tenantId -> getOfferStatusDataFromUserStates(tenantId, startDateTime, endDateTime))
                .flatMap(this::getOfferStatusDataFromLoanOffers)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No offers statuses found for tenantId: {}, startDate: {}, endDate: {}", distributionChannelId, startDateTime, endDateTime);
                    return Mono.just(List.of());
                }));
    }

    private Mono<Map<String, OfferStatus>> getOfferStatusDataFromUserStates(UUID distributionChannelId, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        return Mono.just(distributionChannelId)
                .flatMap(tenantId -> userStatesStoreService.findAll(tenantId, startDateTime, endDateTime))
                .doOnNext(list -> log.info("Found {} distinct users in UserStatesStore", list.size()))
                .map(userStatesStores -> userStatesStores.stream()
                        .map(userStatesStore -> filterStateDetails(userStatesStore, startDateTime, endDateTime).stream()
                                .map(stateDetails -> Map.entry(stateDetails.getApplicationId(), offerStatusMapper.to(userStatesStore, stateDetails)))
                                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
                        .flatMap(map -> map.entrySet().stream())
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .doOnNext(map -> log.info("Found {} applications in UserStatesStore", map.size()));
    }

    private Mono<List<OfferStatus>> getOfferStatusDataFromLoanOffers(Map<String, OfferStatus> applicationWithOfferStatus) {
        return Mono.just(applicationWithOfferStatus.keySet())
                .filter(set -> !set.isEmpty())
                .flatMap(loanOfferStoreService::getLatestUpdatedOffersGroupedByApplication)
                .doOnNext(map -> log.info("Found {} offers in LoanOfferStore", map.size()))
                .map(applicationWithOffer -> merge(applicationWithOfferStatus, applicationWithOffer));
    }

    private List<OfferDataStateDetails> filterStateDetails(UserStatesStore userStatesStore, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        return userStatesStore.getOfferDateStateDetailsSet().stream()
                .filter(stateDetails -> stateDetails.getRequestDateTime().isAfter(startDateTime) && stateDetails.getRequestDateTime().isBefore(endDateTime))
                .collect(toList());
    }

    private List<OfferStatus> merge(Map<String, OfferStatus> applicationWithOfferStatus, Map<String, LoanOfferStore> applicationWithOffer) {
        return applicationWithOfferStatus.entrySet().stream()
                .map(entry -> {
                    final var offerStatus = entry.getValue();
                    LoanOfferStore loanOffer = applicationWithOffer.get(entry.getKey());
                    if (loanOffer == null) {
                        return offerStatus;
                    }
                    offerStatus.setOfferProvider(new LoanProvider(loanOffer.getOffer().getLoanProvider().getName()));
                    offerStatus.setOffersReceivedAt(DateTimeConverter.from(loanOffer.getInsertTS()));
                    offerStatus.setOfferAcceptedAt(loanOffer.getAcceptedDate());
                    offerStatus.setKycStatus(loanOffer.getKycStatus());
                    offerStatus.setKycStatusLastUpdatedAt(loanOffer.getKycStatusLastUpdateDate());
                    offerStatus.setOfferStatus(loanOffer.getOfferStatus());
                    offerStatus.setOfferStatusLastUpdatedAt(loanOffer.getStatusLastUpdateDate());

                    return offerStatus;
                }).filter(Objects::nonNull)
                .collect(toList());
    }
}
