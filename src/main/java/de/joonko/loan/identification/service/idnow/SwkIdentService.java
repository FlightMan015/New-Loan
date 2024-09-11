package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.mapper.idnow.SwkCreateIdentRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.swk.SwkContractGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class SwkIdentService extends IDNowIdentService {

    private final SwkCreateIdentRequestMapper swkCreateIdentRequestMapper;
    private final SwkContractGateway swkContractGateway;

    public SwkIdentService(
            IdNowClientApi idNowClientApi,
            IdentificationAuditService identificationAuditService,
            SwkCreateIdentRequestMapper swkCreateIdentRequestMapper,
            IdentificationPropConfig identificationPropConfig,
            SwkContractGateway swkContractGateway,
            IdentificationLinkService identificationLinkService) {
        super(idNowClientApi, identificationAuditService, identificationPropConfig, identificationLinkService);
        this.swkCreateIdentRequestMapper = swkCreateIdentRequestMapper;
        this.swkContractGateway = swkContractGateway;
    }

    @Override
    public Mono<de.joonko.loan.identification.model.idnow.CreateIdentRequest> getIdentRequest(CreateIdentRequest createIdentRequest) {
        return Mono.defer(() -> Mono.just(swkCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest)));
    }

    @Override
    public IdNowAccount getAccountId() {
        return IdNowAccount.SWK;
    }

    @Override
    public Mono<Documents> getDocuments(CreateIdentRequest createIdentRequest) {
        return swkContractGateway.getContract(createIdentRequest.getApplicationId(), createIdentRequest.getDuration())
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
