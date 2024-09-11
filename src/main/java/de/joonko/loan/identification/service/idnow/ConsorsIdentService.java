package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.mapper.idnow.ConsorsCreateIdentRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.consors.ConsorsContractGateway;
import de.joonko.loan.partner.consors.ConsorsStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class ConsorsIdentService extends IDNowIdentService {

    private final ConsorsCreateIdentRequestMapper consorsCreateIdentRequestMapper;
    private final ConsorsContractGateway consorsContractGateway;
    private final ConsorsStoreService consorsStoreService;

    public ConsorsIdentService(
            IdNowClientApi idNowClientApi,
            ConsorsCreateIdentRequestMapper consorsCreateIdentRequestMapper,
            IdentificationPropConfig identificationPropConfig,
            ConsorsContractGateway consorsContractGateway,
            IdentificationAuditService identificationService,
            ConsorsStoreService consorsStoreService,
            IdentificationLinkService identificationLinkService) {
        super(idNowClientApi, identificationService, identificationPropConfig, identificationLinkService);
        this.consorsCreateIdentRequestMapper = consorsCreateIdentRequestMapper;
        this.consorsContractGateway = consorsContractGateway;
        this.consorsStoreService = consorsStoreService;
    }

    @Override
    public Mono<de.joonko.loan.identification.model.idnow.CreateIdentRequest> getIdentRequest(CreateIdentRequest createIdentRequest) {
        return Mono.just(consorsCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest));
    }

    @Override
    public String getTransactionId(String loanApplicationId) {
        return consorsStoreService.getContractIdForApplicationid(loanApplicationId);
    }

    @Override
    public IdNowAccount getAccountId() {
        return IdNowAccount.CONSORS;
    }

    @Override
    public Mono<Documents> getDocuments(CreateIdentRequest createIdentRequest) {
        return consorsContractGateway.getContractForLoanApplicationId(createIdentRequest.getApplicationId())
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
