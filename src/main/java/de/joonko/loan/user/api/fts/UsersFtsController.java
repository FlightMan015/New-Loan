package de.joonko.loan.user.api.fts;

import de.joonko.loan.user.domain.fts.UsersFtsDeletionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin")
public class UsersFtsController {

    private final UsersFtsDeletionService usersFtsDeletionService;

    @DeleteMapping("/v1/user/fts")
    public Mono<ResponseEntity> deleteOldUserFtsData() {
        return usersFtsDeletionService.delete()
                .doOnNext(userStates -> log.debug("deleted fts data for {} users", userStates.size()))
                .then(Mono.fromCallable(() -> ResponseEntity.ok().build()));
    }
}
