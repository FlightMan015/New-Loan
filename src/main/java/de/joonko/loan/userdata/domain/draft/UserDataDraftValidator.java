package de.joonko.loan.userdata.domain.draft;

import de.joonko.loan.userdata.domain.model.UserData;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDataDraftValidator {
    public Mono<UserData> validate(UserData userData) {
        // todo
        return Mono.empty();
    }
}
