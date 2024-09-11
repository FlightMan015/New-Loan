package de.joonko.loan.partner.santander;

import de.joonko.loan.identification.model.webid.useractionresponse.CreateUserActionResponse;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Slf4j
@Component
public class SantanderContractGateway {

    private final SantanderClientApi santanderClientApi;

    public Mono<byte[]> fetchContract(String scbAntragId, String loanOfferId, @NotNull CreateUserActionResponse createUserActionResponse, boolean advertisingConsent) {
        return Mono.just(scbAntragId)
                .doOnNext(antragId -> log.debug("SANTANDER: Creating contract entry for scbAntragId: {}, loanOfferId: {}, web-id actionId: {} , advertisingConsent :{} ",
                        antragId, loanOfferId, createUserActionResponse.getActionId(), advertisingConsent))
                .flatMap(antragId -> santanderClientApi.createContractEntry(antragId, createUserActionResponse.getActionId()))
                .doOnNext(contractEntryStatus -> log.info("SANTANDER: Contract entry status is {}. Fetching contract entry for loanOfferId: {}, web-id actionId: {} , advertisingConsent :{} ",
                        contractEntryStatus.getStatusCodeValue(), loanOfferId, createUserActionResponse.getActionId(), advertisingConsent))
                .flatMap(contractEntryStatus -> getContract(scbAntragId, advertisingConsent));
    }

    private Mono<byte[]> getContract(String scbAntragId, boolean advertisingConsent) {
        return Mono.fromCallable(() -> santanderClientApi.getContract(scbAntragId, advertisingConsent))
                .doOnError(throwable -> log.error("Failed getting contract for scbAntragId: {}", scbAntragId, throwable))
                .subscribeOn(Schedulers.elastic());
    }
}
