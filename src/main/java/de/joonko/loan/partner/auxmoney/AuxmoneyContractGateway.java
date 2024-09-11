package de.joonko.loan.partner.auxmoney;

import de.joonko.loan.partner.auxmoney.model.AuxmoneyCreateContractRequest;
import de.joonko.loan.partner.auxmoney.model.ContractResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuxmoneyContractGateway {

    @Qualifier("auxmoneyWebClient")
    private final WebClient auxmoneyWebClient;

    public Mono<ContractResponse> getContract(String userId, String creditID) {
        return auxmoneyWebClient.post()
                .uri("/distributionline/api/rest/partnerendpoints/contracts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(AuxmoneyCreateContractRequest.builder()
                        .creditId(creditID)
                        .userId(userId)
                        .build())
                .retrieve()
                .bodyToMono(ContractResponse.class)
                .doOnError(throwable -> log.error("Error while requesting contract ", throwable))
                .doOnSuccess(contractResponse -> log.info("Received  contract data {}", contractResponse.getSuccess()));
    }
}
