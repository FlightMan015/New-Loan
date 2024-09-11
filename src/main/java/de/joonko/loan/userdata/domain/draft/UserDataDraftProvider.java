package de.joonko.loan.userdata.domain.draft;

import de.joonko.loan.userdata.domain.model.UserData;
import de.joonko.loan.userdata.infrastructure.draft.model.UserDraftInformationStore;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserDataDraftProvider {

    Mono<UserData> get(String userUuid);

    Mono<UserData> save(String userUuid, UserData userData);

    Mono<List<UserDraftInformationStore>> removeFtsData(@NotNull List<String> userUuids);
}
