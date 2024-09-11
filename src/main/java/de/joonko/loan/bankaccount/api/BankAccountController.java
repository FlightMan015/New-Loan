package de.joonko.loan.bankaccount.api;

import de.joonko.loan.bankaccount.domain.BankAccountService;
import de.joonko.loan.exception.GenericExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
@RestController
@RequestMapping("api/v1")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @DeleteMapping(value = "/user/bank-account")
    public Mono<ResponseEntity> deleteBankAccount(final Principal principal) {
        return Mono.just(principal)
                .map(Principal::getName)
                .doOnNext(userUuid -> log.info("Request to delete transactional data for userUuid: {}", userUuid))
                .flatMap(bankAccountService::delete)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
