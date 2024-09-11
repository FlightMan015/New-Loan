package de.joonko.loan.user.api.abandoned;

import de.joonko.loan.user.domain.abandoned.AbandonedUsersNotifierService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin")
public class AbandonedUsersController {

    private final AbandonedUsersNotifierService abandonedUsersNotifierService;

    @PostMapping("/v1/user/abandoned")
    public Mono<ResponseEntity> sendAbandonedUsers() {
        log.debug("Send abandoned users notification");

        return abandonedUsersNotifierService.send()
                .map(ignore -> ResponseEntity.ok().build());
    }
}
