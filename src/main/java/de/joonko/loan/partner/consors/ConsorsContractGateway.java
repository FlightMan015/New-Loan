package de.joonko.loan.partner.consors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class ConsorsContractGateway {

    private final ConsorsStoreService consorsStoreService;
    private final ConsorsClient consorsClient;

    public Mono<byte[]> getContractForLoanApplicationId(String applicationId) {
        String contractUrl = consorsStoreService.getDownloadSubscriptionDocumentLinkForApplicationId(applicationId);
        log.info("Contract downloadUrl: {} for applicationId: {}", contractUrl, applicationId);

        return consorsClient.getToken(applicationId)
                .flatMap(jwtToken -> consorsClient.getContract(jwtToken, contractUrl, applicationId));
    }
}
