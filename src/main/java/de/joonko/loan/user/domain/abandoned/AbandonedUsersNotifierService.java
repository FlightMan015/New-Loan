package de.joonko.loan.user.domain.abandoned;

import de.joonko.loan.config.AbandonedUsersConfig;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class AbandonedUsersNotifierService {

    private final LoanOfferStoreService loanOfferStoreService;
    private final DataSolutionCommunicationManager dataSolutionCommunicationManager;
    private final UserStatesStoreService userStatesStoreService;
    private final AbandonedUsersConfig abandonedUsersConfig;


    public Mono<Void> send() {
        return Mono.just(abandonedUsersConfig.getDaysAgo())
                .flatMap(loanOfferStoreService::findAnyOfferForEachAbandonedUser)
                .doOnNext(list -> log.info("Found {} abandoned users", list.size()))
                .filter(list -> !list.isEmpty())
                .zipWhen(offers -> userStatesStoreService.findAllByUserUUID(offers.stream().map(LoanOfferStore::getUserUUID).collect(toList())), this::mergeOffersWithUserIds)
                .doOnNext(tuple -> log.debug("Ready to send {} abandoned loan offers messages", tuple.size()))
                .flatMapMany(Flux::fromIterable)
                .flatMap(tuple -> dataSolutionCommunicationManager.sendLoanOffer(tuple.getT1(), tuple.getT2(), OfferUpdateType.STALE_OFFERS_NOTIFICATION))
                .then();
    }

    private List<Tuple2<Long, LoanOfferStore>> mergeOffersWithUserIds(List<LoanOfferStore> offers, List<UserStatesStore> userStatesStores) {
        final var userUuidsWithBonifyUserIds = userStatesStores.stream().collect(toMap(UserStatesStore::getUserUUID, UserStatesStore::getBonifyUserId));

        return offers.stream()
                .filter(o -> userUuidsWithBonifyUserIds.containsKey(o.getUserUUID()))
                .map(o -> Tuples.of(userUuidsWithBonifyUserIds.get(o.getUserUUID()), o))
                .collect(toList());
    }
}
