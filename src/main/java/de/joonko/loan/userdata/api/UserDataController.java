package de.joonko.loan.userdata.api;

import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.userdata.api.mapper.UserDataMapper;
import de.joonko.loan.userdata.api.model.UserDataRequest;
import de.joonko.loan.userdata.api.model.UserDataResponse;
import de.joonko.loan.userdata.domain.UserDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
@RestController
@RequestMapping("api/v1")
public class UserDataController {

    private final UserDataService userDataService;
    private final UserDataMapper userDataMapper;

    @GetMapping(value = "/userdata", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<UserDataResponse>> getUserData(final Principal principal) {
        return Mono.just(principal.getName())
                .doOnNext(userUuid -> log.info("Get user data for userUuid: {}", userUuid))
                .flatMap(userDataService::get)
                .map(userDataMapper::toApiModel)
                .map(ResponseEntity::ok);
    }

    @PutMapping(value = "/userdata", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> updateUserData(final Principal principal,
                                               @RequestBody UserDataRequest userDataRequest) {
        return Mono.just(userDataRequest)
                .doOnNext(any -> log.info("Update user data for userUuid: {}", principal.getName()))
                .map(userDataMapper::toDomainModel)
                .flatMap(userData -> userDataService.update(principal.getName(), userData))
                .map(ignore -> ResponseEntity.noContent().build());
    }
}
