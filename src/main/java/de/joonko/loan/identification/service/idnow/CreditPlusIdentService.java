package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.mapper.idnow.CreditPlusIdentRequestMapper;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.idnow.*;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.creditPlus.CreditPlusContractGateway;
import de.joonko.loan.partner.creditPlus.CreditPlusStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class CreditPlusIdentService extends IDNowIdentService {

    private final CreditPlusIdentRequestMapper creditPlusIdentRequestMapper;
    private final IdentificationPropConfig identificationPropConfig;
    private final CreditPlusContractGateway creditPlusContractGateway;
    private final CreditPlusStoreService creditPlusStoreService;

    public CreditPlusIdentService(IdNowClientApi idNowClientApi, IdentificationAuditService identificationAuditService, IdentificationPropConfig identificationPropConfig, IdentificationLinkService identificationLinkService, CreditPlusIdentRequestMapper creditPlusIdentRequestMapper, IdentificationPropConfig identificationPropConfig1, CreditPlusContractGateway creditPlusContractGateway, CreditPlusStoreService creditPlusStoreService) {
        super(idNowClientApi, identificationAuditService, identificationPropConfig, identificationLinkService);
        this.creditPlusIdentRequestMapper = creditPlusIdentRequestMapper;
        this.identificationPropConfig = identificationPropConfig1;
        this.creditPlusContractGateway = creditPlusContractGateway;
        this.creditPlusStoreService = creditPlusStoreService;
    }

    @Override
    public Mono<CreateIdentRequest> getIdentRequest(de.joonko.loan.identification.model.CreateIdentRequest createIdentRequest) {
        CreateIdentRequest identRequest = creditPlusIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest);
        if (identificationPropConfig.getAutoidentification()) {
            identRequest.setFirstName("X-MANUALTEST-HAPPYPATH");
        }
        return Mono.just(identRequest);
    }

    @Override
    public IdNowAccount getAccountId() {
        return IdNowAccount.CREDITPLUS;
    }

    @Override
    public String getTransactionId(String loanApplicationId) {
        return creditPlusStoreService.findCpReferenceNumberForAcceptedOffer(loanApplicationId).toString();
    }

    @Override
    public Mono<Documents> getDocuments(de.joonko.loan.identification.model.CreateIdentRequest createIdentRequest) {
        return creditPlusContractGateway.getContract(createIdentRequest.getApplicationId(), createIdentRequest.getDuration())
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
