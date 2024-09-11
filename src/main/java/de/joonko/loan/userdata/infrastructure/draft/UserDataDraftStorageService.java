package de.joonko.loan.userdata.infrastructure.draft;

import de.joonko.loan.user.UserDataNotFoundException;
import de.joonko.loan.userdata.domain.draft.UserDataDraftProvider;
import de.joonko.loan.userdata.domain.model.UserData;
import de.joonko.loan.userdata.infrastructure.draft.mapper.UserDraftInformationMapper;
import de.joonko.loan.userdata.infrastructure.draft.model.UserDraftInformationStore;
import de.joonko.loan.userdata.infrastructure.draft.repository.UserDraftInformationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class UserDataDraftStorageService implements UserDataDraftProvider {

    private final UserDraftInformationRepository repository;
    private final UserDraftInformationMapper mapper;

    public Mono<UserData> get(@NotNull final String userUuid) {
        return Mono.fromCallable(() -> repository.findById(userUuid)
                        .orElseThrow(() -> new UserDataNotFoundException(String.format("Missing draft user data with userUuid: %s", userUuid))))
                .doOnError(err -> log.error("Missing draft user data with userUuid: {}", userUuid))
                .map(mapper::toDomainModel)
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<UserData> save(@NotNull final String userUuid, @NotNull final UserData userDataDraft) {
        final var userAdditionalInformationDraftStore = mapper.toUserDraftStore(userUuid, userDataDraft);
        return Mono.fromCallable(() -> repository.save(userAdditionalInformationDraftStore))
                .map(mapper::toDomainModel)
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<List<UserDraftInformationStore>> removeFtsData(@NotNull List<String> userUuids) {
        return Mono.fromCallable(() -> repository.findAllById(userUuids))
                .map(userDrafts -> {
                    userDrafts.forEach(draft -> draft.setUserEmployment(null));
                    return userDrafts;
                })
                .flatMap(userDrafts -> Mono.fromCallable(() -> repository.saveAll(userDrafts)))
                .flatMapIterable(list -> list)
                .collectList()
                .subscribeOn(Schedulers.elastic());
    }
}
