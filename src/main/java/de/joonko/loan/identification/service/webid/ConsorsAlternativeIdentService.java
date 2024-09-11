package de.joonko.loan.identification.service.webid;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.service.IdentService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.consors.ConsorsContractGateway;
import de.joonko.loan.partner.consors.ConsorsStoreService;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class ConsorsAlternativeIdentService implements IdentService {

    private final ConsorsStoreService consorsStoreService;
    private final IdentificationLinkService identificationLinkService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final ConsorsContractGateway consorsContractGateway;

    @Override
    public Mono<CreateIdentResponse> createIdent(CreateIdentRequest createIdentRequest) {
        return getCreateIdentResponse(createIdentRequest.getApplicationId())
                .doOnError(e -> log.error("Not found kycUrl for applicationId: {}", createIdentRequest.getApplicationId()))
                .doOnSuccess(res -> identificationLinkService.add(createIdentRequest.getApplicationId(), createIdentRequest.getLoanOfferId(), createIdentRequest.getLoanProvider(), getProvider(), createIdentRequest.getApplicationId(), res.getKycUrl()));
    }

    @Override
    public Mono<String> getIdentStatus(String externalIdentId) {
        return identificationLinkService.getIdentificationByExternalIdentId(externalIdentId)
                .flatMap(identificationLink -> loanOfferStoreService.findById(identificationLink.getOfferId()))
                .map(LoanOfferStore::getKycStatus);
    }

    @Override
    public IdentificationProvider getProvider() {
        return IdentificationProvider.WEB_ID;
    }

    private Mono<CreateIdentResponse> getCreateIdentResponse(final String applicationId) {
        return consorsStoreService.getKYCLinkForApplicationId(applicationId)
                .zipWhen(kycLink -> getDocuments(applicationId))
                .map(tuple -> CreateIdentResponse.builder()
                        .kycUrl(tuple.getT1())
                        .documents(tuple.getT2())
                        .kycProvider(getProvider())
                        .build()
                );
    }

    private Mono<Documents> getDocuments(final String applicationId) {
        return consorsContractGateway.getContractForLoanApplicationId(applicationId)
                .map(contract -> Documents.builder()
                        .documents(List.of(
                                Document.builder()
                                        .content(contract)
                                        .documentId("contract")
                                        .build()
                        ))
                        .build());
    }
}
