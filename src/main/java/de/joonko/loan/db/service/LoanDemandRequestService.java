package de.joonko.loan.db.service;

import de.joonko.loan.db.repositories.LoanDemandRequestRepository;
import de.joonko.loan.offer.api.LoanDemandRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanDemandRequestService {

    private final LoanDemandRequestRepository repository;

    public Mono<LoanDemandRequest> findLoanDemandRequest(final String applicationId) {
        return Mono.fromCallable(() -> repository.findByApplicationId(applicationId)
                        .orElseThrow(() -> new IllegalStateException(String.format("loanDemandRequest not found for application id: %s", applicationId))))
                .subscribeOn(Schedulers.elastic());

    }

    public Flux<LoanDemandRequest> deleteAllByUserId(final String userId) {
        return Mono.fromCallable(() -> repository.deleteByUserUUID(userId))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<List<LoanDemandRequest>> removeFtsData(@NotNull List<String> userUuids) {
        return Mono.fromCallable(() -> repository.findAllById(userUuids))
                .map(userAdditional -> {
                    userAdditional.forEach(draft -> {
                        draft.setEmploymentDetails(null);
                        draft.setCreditDetails(null);
                        draft.setExpenses(null);
                        draft.setIncome(null);
                        draft.setDisposableIncome(null);
                        draft.setAccountDetails(null);
                        draft.setCustomDACData(null);
                    });
                    return userAdditional;
                })
                .flatMap(userDrafts -> Mono.fromCallable(() -> repository.saveAll(userDrafts)))
                .flatMapIterable(list -> list)
                .collectList()
                .subscribeOn(Schedulers.elastic());
    }
}
