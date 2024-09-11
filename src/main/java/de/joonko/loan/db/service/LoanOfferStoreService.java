package de.joonko.loan.db.service;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.repositories.CustomLoanOfferStoreRepository;
import de.joonko.loan.db.repositories.LoanOfferStoreRepository;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.db.vo.OfferAcceptedEnum;
import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.offer.api.GetOffersMapper;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.user.states.OfferDataStateDetails;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanOfferStoreService {

    private final LoanOfferStoreRepository loanOfferStoreRepository;
    private final CustomLoanOfferStoreRepository customLoanOfferStoreRepository;
    private final GetOffersConfigurations getOffersConfigurations;
    private final GetOffersMapper getOffersMapper;

    public Flux<LoanOfferStore> saveAll(@NotNull List<LoanOffer> loanOffers, String userUUID, String applicationId, String parentApplicationId) {
        List<LoanOfferStore> offerStores = loanOffers.stream()
                .map(o -> getOffersMapper.toStore(o, userUUID, applicationId, parentApplicationId))
                .collect(toList());

        return Mono.fromCallable(() -> loanOfferStoreRepository.saveAll(offerStores))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<LoanOfferStore> updateOfferStatus(final String loanOfferId, final String newStatus) {
        return Mono.just(loanOfferId)
                .flatMap(this::findById)
                .filter(existingLoanOffer -> !newStatus.equals(existingLoanOffer.getOfferStatus()))
                .doOnNext(existingLoanOffer -> log.info("Offer status for offerId: {}, updated from {} to {}", loanOfferId, existingLoanOffer.getOfferStatus(), newStatus))
                .map(existingLoanOffer -> {
                    existingLoanOffer.setOfferStatus(newStatus);
                    return existingLoanOffer;
                })
                .flatMap(this::saveOffer);
    }

    public Mono<LoanOfferStore> updateKycStatus(final String loanOfferId, final String newStatus) {
        return Mono.just(loanOfferId)
                .flatMap(this::findById)
                .filter(existingLoanOffer -> !newStatus.equals(existingLoanOffer.getKycStatus()))
                .doOnNext(existingLoanOffer -> log.info("Offer KYC status for offerId: {}, updated from {} to {}", loanOfferId, existingLoanOffer.getKycStatus(), newStatus))
                .map(existingLoanOffer -> {
                    existingLoanOffer.setKycStatus(newStatus);
                    return existingLoanOffer;
                })
                .flatMap(this::saveOffer);
    }

    public Mono<LoanOfferStore> updateAcceptedStatus(String loanOfferId, String loanProviderReferenceNumber, final OfferAcceptedEnum acceptedBy) {
        return findById(loanOfferId)
                .map(loanOfferStore -> {
                    loanOfferStore.setIsAccepted(true);
                    loanOfferStore.setLoanProviderReferenceNumber(loanProviderReferenceNumber);
                    loanOfferStore.setAcceptedBy(acceptedBy);
                    return loanOfferStore;
                })
                .flatMap(this::saveOffer);
    }

    public Mono<LoanOfferStore> updateOffer(String loanOfferId, String loanProviderReferenceNumber, LoanApplicationStatus status) {
        return findById(loanOfferId)
                .map(loanOfferStore -> {
                    loanOfferStore.setLoanProviderReferenceNumber(loanProviderReferenceNumber);
                    loanOfferStore.setOfferStatus(status.name());
                    return loanOfferStore;
                })
                .flatMap(this::saveOffer);
    }

    public LoanOfferStore findByLoanOfferId(String loanOfferId) {
        return loanOfferStoreRepository.findById(loanOfferId)
                .orElseThrow(() -> new RuntimeException("LoanOfferStore not found for offer id" + loanOfferId));
    }

    public Mono<LoanOfferStore> findById(String loanOfferId) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.findById(loanOfferId)
                        .orElseThrow(() -> new RuntimeException("LoanOfferStore not found for offer id: " + loanOfferId)))
                .subscribeOn(Schedulers.elastic());
    }

    public List<LoanOfferStore> findAllByLoanApplicationId(String applicationId) {
        return loanOfferStoreRepository.findAllByApplicationId(applicationId);
    }

    public Set<LoanOfferStore> findNotDeletedOffersWithRecommendations(String userUuid, @NotNull String
            applicationId, @NotNull Set<OfferDataStateDetails> offersStates) {
        Map<String, Integer> applicationIdsWithAmounts = offersStates.stream()
                .filter(stateDetails -> isRequestedOrRecommended(stateDetails, applicationId))
                .collect(toMap(OfferDataStateDetails::getApplicationId, OfferDataStateDetails::getAmount));

        var banksLabels = Bank.getAllBanks().stream()
                .map(bank -> bank.label)
                .collect(toSet());

        return loanOfferStoreRepository.getByUserIdApplicationIdAndProviders(userUuid, applicationId, banksLabels).stream()
                .filter(loanOffer -> isWithinAmountLimit(loanOffer, applicationIdsWithAmounts))
                .collect(toSet());
    }

    public Mono<List<LoanOfferStore>> getLoanOffers(@NotNull String userUuid, @NotNull String applicationId) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.getNotDeletedOffers(userUuid, applicationId))
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<LoanOfferStore> getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDurationAndLoanProvider(@NotNull final String loanProviderReferenceNumber, int durationInMonths, @NotNull final String loanProviderName) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.findAllByLoanProviderReferenceNumberAndOfferDurationInMonthAndOfferLoanProviderName(loanProviderReferenceNumber, durationInMonths, loanProviderName))
                .filter(offers -> offers.size() == 1)
                .map(offers -> offers.stream().findFirst().get())
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Could not find a single offer by loanProviderReferenceNumber - {} and duration - {} and loanProvider - {} to update status", loanProviderReferenceNumber, durationInMonths, loanProviderName);
                    return Mono.empty();
                })).subscribeOn(Schedulers.elastic());
    }

    public Mono<LoanOfferStore> getSingleLoanOfferForLoanProviderReferenceNumber(@NotNull String loanProviderReferenceNumber) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.findAllByLoanProviderReferenceNumber(loanProviderReferenceNumber))
                .flatMap(offers ->
                        {
                            if (offers.size() == 1) {
                                return Mono.just(offers.stream().findFirst().get());
                            } else {
                                log.error("Could not find a single offer by loanProviderReferenceNumber - {} to update", loanProviderReferenceNumber);
                            }
                            return Mono.empty();
                        }
                )
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<List<LoanOfferStore>> getLoanOffers(@NotNull String userUuid, @NotNull String applicationId,
                                                    boolean bonifyLoans) {
        var banksSet = bonifyLoans ? Set.of(Bank.getBonifyBank()) : Bank.getExternalBanks();

        var banksLabels = banksSet.stream()
                .map(bank -> bank.label)
                .collect(toSet());

        return Mono.fromCallable(() -> loanOfferStoreRepository.getByUserIdApplicationIdAndProviders(userUuid, applicationId, banksLabels))
                .subscribeOn(Schedulers.elastic());
    }

    private boolean isRequestedOrRecommended(OfferDataStateDetails stateDetails, String applicationId) {
        return applicationId.equals(stateDetails.getParentApplicationId()) || applicationId.equals(stateDetails.getApplicationId());
    }

    private boolean isWithinAmountLimit(LoanOfferStore loanOffer, Map<String, Integer> applicationIdsWithAmounts) {
        double amountFraction = getOffersConfigurations.getMinMaxFractionOutOfAskedAmountToDisplay();

        return applicationIdsWithAmounts.containsKey(loanOffer.getApplicationId()) &&
                loanOffer.getOffer().getAmount() >= applicationIdsWithAmounts.get(loanOffer.getApplicationId()) * (1 - amountFraction) &&
                loanOffer.getOffer().getAmount() <= applicationIdsWithAmounts.get(loanOffer.getApplicationId()) * (1 + amountFraction);
    }

    public Flux<LoanOfferStore> deleteAllByUserId(final String userUuid) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.deleteByUserUUID(userUuid))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    public void softDeleteOffersForUser(String userUUID) {
        customLoanOfferStoreRepository.softDeleteOffersForUser(userUUID);
    }

    public void save(LoanOfferStore acceptedOffer) {
        loanOfferStoreRepository.save(acceptedOffer);
    }

    public Mono<LoanOfferStore> saveOffer(LoanOfferStore loanOffer) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.save(loanOffer))
                .subscribeOn(Schedulers.elastic());
    }

    public Flux<LoanOfferStore> findByOfferStatusAndLoanProvider
            (Set<Bank> loanProviders, Set<LoanApplicationStatus> offerStatuses) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.findByOfferStatusAndLoanProvider(loanProviders, offerStatuses))
                .doOnNext(list -> log.debug("Found {} offers", list.size()))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<LoanOfferStore> findAcceptedByApplicationIdAndBankProvider(@NotNull final String applicationId,
                                                                           @NotNull final Bank loanProvider) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.findByApplicationIdAndIsAcceptedAndLoanProvider(applicationId, loanProvider.getLabel()))
                .flatMapIterable(list -> list)
                .next()
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<List<LoanOfferStore>> findLoanOfferStoreByUserUUIDAndContractsExists(@NotNull final String userUUid) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.findLoanOfferStoreByUserUUIDAndContractsIsNotNull(userUUid));
    }

    public Mono<List<LoanOfferStore>> findAnyOfferForEachAbandonedUser(final int daysAgo) {
        return getAnyOfferForEachActiveUser(daysAgo)
                .map(offersForActiveUsers -> offersForActiveUsers.stream().map(LoanOfferStore::getUserUUID).collect(toSet()))
                .flatMap(activeUsers -> getAnyOfferForEachAbandonedUser(daysAgo, activeUsers));
    }

    private Mono<List<LoanOfferStore>> getAnyOfferForEachActiveUser(final int daysAgo) {
        final var finiteStates = Set.of("CANCELED", "CANCELLED", "FAILURE", "ABORTED", "REJECTED", "APPROVED", "PAID_OUT", "SUCCESS", "SUCCESS_DATA_CHANGED");
        final var startDate = LocalDateTime.now().minusDays(daysAgo).with(LocalTime.MIDNIGHT);
        final var endDate = startDate.plusDays(1);

        return Mono.fromCallable(() -> loanOfferStoreRepository.getAnyOfferForEachActiveUser(startDate, endDate, finiteStates))
                .doOnNext(list -> log.debug("Found offers for {} active users since {} days", list.size(), daysAgo))
                .subscribeOn(Schedulers.elastic());
    }

    private Mono<List<LoanOfferStore>> getAnyOfferForEachAbandonedUser(final int daysAgo, Set<String> activeUserUuids) {
        final var startDate = LocalDateTime.now().minusDays(daysAgo).with(LocalTime.MIDNIGHT);
        final var endDate = startDate.plusDays(1);

        return Mono.fromCallable(() -> loanOfferStoreRepository.getAnyOfferForEachAbandonedUser(startDate, endDate, activeUserUuids))
                .doOnNext(list -> log.info("Found offers for {} abandoned users since {} days", list.size(), daysAgo))
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<Map<String, LoanOfferStore>> getLatestUpdatedOffersGroupedByApplication(@NotNull final Set<String> applicationIds) {
        return Mono.fromCallable(() -> loanOfferStoreRepository.getLatestUpdatedOffersForEachApplication(applicationIds))
                .map(list -> list.stream().collect(toMap(LoanOfferStore::getApplicationId, Function.identity())))
                .doOnNext(map -> log.info("Found {} latest updated offers", map.size()))
                .subscribeOn(Schedulers.elastic());
    }
}
