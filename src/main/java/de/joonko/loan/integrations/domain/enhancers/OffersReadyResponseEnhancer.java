package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.integrations.domain.OffersStateValidator;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreMapper;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.api.mapper.LoanOfferStoreMapper;
import de.joonko.loan.offer.api.model.LoanOffer;
import de.joonko.loan.offer.api.model.LoanOfferStore;
import de.joonko.loan.offer.api.model.OfferResponseState;
import de.joonko.loan.offer.api.model.OffersReadyResponse;
import de.joonko.loan.offer.api.model.OffersResponse;
import de.joonko.loan.user.service.UserPersonalInfoService;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static de.joonko.loan.common.CollectionUtil.mapList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Slf4j
@RequiredArgsConstructor
@Component
public class OffersReadyResponseEnhancer implements ResponseEnhancer<OffersReadyResponse> {

    private final UserTransactionalDataStoreService userTransactionalDataStoreService;
    private final UserPersonalInfoService userPersonalInfoService;
    private final UserTransactionalDataStoreMapper userTransactionalDataStoreMapper;
    private final LoanOfferStoreMapper loanOfferStoreMapper;
    private final DataSolutionCommunicationManager dataSolutionCommunicationManager;
    private final UserStatesStoreService userStatesStoreService;
    private final OffersStateValidator offersStateValidator;
    private final LoanOfferStoreService loanOfferStoreService;

    @Override
    public Mono<OffersResponse<OffersReadyResponse>> buildResponseData(final OfferRequest offerRequest) {
        return Mono.just(offerRequest.getUserUUID())
                .flatMap(userStatesStoreService::findById)
                .map(UserStatesStore::getOfferDateStateDetailsSet)
                .flatMap(stateDetailsSet ->
                        Mono.zip(getLoanOffers(offerRequest, stateDetailsSet), getKycDetails(offerRequest.getUserUUID()), getRecentQueriedAmounts(stateDetailsSet)))
                .doOnNext(any -> log.debug("Building offers ready response for userId: {}", offerRequest.getUserUUID()))
                .map(tuple -> buildOffersReadyResponse(tuple.getT1(), tuple.getT2(), tuple.getT3(), offerRequest.isRequestedBonifyLoans()));
    }

    @Override
    public OfferResponseState getState() {
        return OfferResponseState.OFFERS_READY;
    }

    private Mono<List<LoanOfferStore>> getLoanOffers(OfferRequest offerRequest, Set<OfferDataStateDetails> stateDetailsSet) {
        return Flux.fromIterable(stateDetailsSet)
                .filter(offersDetails -> Objects.nonNull(offersDetails.getResponseDateTime()) &&
                        offersDetails.getAmount().equals(offerRequest.getRequestedAmount()) &&
                        !offersDetails.isExpiredAlready() &&
                        offerRequest.getRequestedPurpose().equals(offersDetails.getPurpose()))
                .sort(comparing(OfferDataStateDetails::getResponseDateTime).reversed())
                .map(OfferDataStateDetails::getApplicationId)
                .next()
                .flatMap(applicationId -> getLoanOffers(offerRequest, applicationId))
                .map(mapList(loanOfferStoreMapper::map));
    }

    private Mono<List<de.joonko.loan.db.vo.LoanOfferStore>> getLoanOffers(OfferRequest offerRequest, String applicationId) {
        return loanOfferStoreService.getLoanOffers(offerRequest.getUserUUID(), applicationId)
                .doOnNext(offers -> this.sendLoanOffers(offerRequest, applicationId, offers));
    }

    private void sendLoanOffers(OfferRequest offerRequest, String applicationId, List<de.joonko.loan.db.vo.LoanOfferStore> allOffers) {
        final var bonifyLoansCount = (int) allOffers.stream()
                .filter(o -> Bank.getBonifyBank().label.equals(o.getOffer().getLoanProvider().getName())).count();

        final var otherLoansCount = allOffers.size() - bonifyLoansCount;

        dataSolutionCommunicationManager.sendLoanOffers(offerRequest, applicationId, allOffers, bonifyLoansCount, otherLoansCount);
    }

    private Mono<Set<Integer>> getRecentQueriedAmounts(Set<OfferDataStateDetails> offerDateStateDetailsSet) {
        return Flux.fromIterable(offerDateStateDetailsSet)
                .filter(offersStateValidator::areStillValidOffers)
                .map(OfferDataStateDetails::getAmount)
                .collect(toCollection(TreeSet::new));
    }

    private Mono<KycRelatedPersonalDetails> getKycDetails(String userId) {
        return Mono.zip(userTransactionalDataStoreService.getKycRelatedPersonalDetails(userId), userPersonalInfoService.findById(userId))
                .map(tuple -> userTransactionalDataStoreMapper.mapToKycRelatedPersonalDetails(tuple.getT1().getAccountDetails(), tuple.getT2()));
    }

    private OffersResponse<OffersReadyResponse> buildOffersReadyResponse(final List<LoanOfferStore> allOffers, final KycRelatedPersonalDetails kycDetails, final Set<Integer> recentQueriedAmounts, final boolean bonifyLoans) {
        final var banksSet = bonifyLoans ? Set.of(Bank.getBonifyBank()) : Bank.getAllBanks();
        final var banksLabels = banksSet.stream()
                .map(bank -> bank.label)
                .collect(toSet());

        final var requestedOffers = allOffers.stream()
                .filter(o -> banksLabels.contains(o.getOffer().getLoanProvider().getName()))
                .sorted(comparing(LoanOfferStore::getOffer, sortByAmountDescThenByMonthlyInstallmentAsc()))
                .collect(toList());

        return OffersResponse.<OffersReadyResponse>builder()
                .state(getState())
                .data(OffersReadyResponse.builder()
                        .offers(requestedOffers)
                        .kycRelatedPersonalDetails(kycDetails)
                        .recentQueriedAmounts(recentQueriedAmounts)
                        .totalOffers(allOffers.size())
                        .requestedOffers(requestedOffers.size())
                        .build())
                .build();
    }

    private Comparator<LoanOffer> sortByAmountDescThenByMonthlyInstallmentAsc() {
        return comparing(LoanOffer::getAmount).reversed().thenComparing(LoanOffer::getMonthlyRate);
    }

}
