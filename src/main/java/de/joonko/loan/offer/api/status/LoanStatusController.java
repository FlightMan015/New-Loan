package de.joonko.loan.offer.api.status;

import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.offer.domain.OffersStatusSyncingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin")
@GenericExceptionHandler
public class LoanStatusController {

    private final OffersStatusSyncingService offersStatusSyncingService;

    @PostMapping("/v1/sync/offer/status")
    public Mono<ResponseEntity> syncOffersStatus(@RequestBody @Valid final LoanStatusRequest loanStatusRequest) {
        return Mono.just(loanStatusRequest.getBanks())
                .doOnNext(banks -> log.debug("Sync offers status for: {}", loanStatusRequest.getBanks()))
                .flatMap(offersStatusSyncingService::sync)
                .map(ignore -> ResponseEntity.ok().build());
    }
}
