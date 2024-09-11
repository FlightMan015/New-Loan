package de.joonko.loan.identification.service.webid;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.mapper.webid.SantanderCreateUserActionRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.webid.useractionrequest.CreateUserActionRequest;
import de.joonko.loan.identification.model.webid.useractionresponse.CreateUserActionResponse;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.partner.santander.SantanderContractGateway;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class SantanderIdentService extends WebIDIdentService {

    private final SantanderCreateUserActionRequestMapper santanderCreateUserActionRequestMapper;
    private final SantanderContractGateway santanderContractGateway;

    public SantanderIdentService(
            @Qualifier("WebIdWebClient") WebClient webIdWebClient,
            SantanderCreateUserActionRequestMapper santanderCreateUserActionRequestMapper,
            IdentificationAuditService identificationAuditService,
            IdentificationLinkService identificationLinkService,
            LoanOfferStoreService loanOfferStoreService,
            SantanderContractGateway santanderContractGateway,
            ApiMetric apiMetric) {

        super(webIdWebClient, identificationAuditService, identificationLinkService, loanOfferStoreService, apiMetric);
        this.santanderCreateUserActionRequestMapper = santanderCreateUserActionRequestMapper;
        this.santanderContractGateway = santanderContractGateway;
    }

    @Override
    protected CreateUserActionRequest getIdentRequest(CreateIdentRequest createIdentRequest) {
        return santanderCreateUserActionRequestMapper.toCreateUserActionRequest(createIdentRequest);
    }

    @Override
    protected Mono<byte[]> fetchContract(CreateIdentRequest createIdentRequest, CreateUserActionResponse createUserActionResponse) {
        String loanOfferId = createIdentRequest.getLoanOfferId();
        log.debug("SANTANDER: Getting santanderAcceptedOffer by loanOfferId: {}", loanOfferId);

        return Mono.fromCallable(() -> loanOfferStoreService.findByLoanOfferId(loanOfferId))
                .map(LoanOfferStore::getLoanProviderReferenceNumber)
                .flatMap(scbAntragId -> santanderContractGateway.fetchContract(scbAntragId, loanOfferId, createUserActionResponse, createIdentRequest.isAdvertisingConsent()))
                .subscribeOn(Schedulers.elastic());
    }
}
