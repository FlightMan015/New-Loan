package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.mapper.idnow.AionCreateIdentRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.aion.AionContractGateway;
import de.joonko.loan.partner.aion.AionStoreService;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@Slf4j
public class AionIdentService extends IDNowIdentService {

    private final AionCreateIdentRequestMapper aionCreateIdentRequestMapper;
    private final AionContractGateway aionContractGateway;
    private final AionStoreService aionStoreService;

    public AionIdentService(
            IdNowClientApi idNowClientApi,
            IdentificationPropConfig identificationPropConfig,
            IdentificationAuditService identificationAuditService,
            AionCreateIdentRequestMapper aionCreateIdentRequestMapper,
            IdentificationLinkService identificationLinkService,
            AionContractGateway aionContractGateway,
            AionStoreService aionStoreService) {
        super(idNowClientApi, identificationAuditService, identificationPropConfig, identificationLinkService);
        this.aionCreateIdentRequestMapper = aionCreateIdentRequestMapper;
        this.aionContractGateway = aionContractGateway;
        this.aionStoreService = aionStoreService;
    }

    @Override
    public Mono<de.joonko.loan.identification.model.idnow.CreateIdentRequest> getIdentRequest(CreateIdentRequest createIdentRequest) {
        return aionStoreService.findByApplicationId(createIdentRequest.getApplicationId())
                .map(Optional::get)
                .map(CreditApplicationResponseStore::getRepresentativeId)
                .doOnError(throwable -> log.error("Failed getting representativeId for applicationId: {}", createIdentRequest.getApplicationId(), throwable))
                .map(representativeId -> aionCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest, representativeId));
    }

    @Override
    public IdNowAccount getAccountId() {
        return IdNowAccount.AION;
    }

    @Override
    public Mono<Documents> getDocuments(CreateIdentRequest createIdentRequest) {
        return aionContractGateway.getDocuments(createIdentRequest.getApplicationId());
    }
}
