package de.joonko.loan.identification.service.webid;

import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.service.IdentService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStoreService;
import de.joonko.loan.webhooks.postbank.model.ContractState;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class PostbankIdentService implements IdentService {

    private final PostbankLoanDemandStoreService postbankLoanDemandStoreService;
    private final IdentificationLinkService identificationLinkService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final ContractStorageService contractStorageService;

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

    private Mono<CreateIdentResponse> getCreateIdentResponse(String applicationId) {
        return postbankLoanDemandStoreService.findByApplicationId(applicationId)
                .map(postbankLoanDemandStore -> postbankLoanDemandStore.getCreditResults().stream()
                        .filter(credit -> credit.getContractState() == ContractState.getSuccessState())
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException(String.format("Not found success contract state for applicationId: %s", applicationId))))
                .zipWhen(creditResult -> contractStorageService.getContracts(creditResult.getSavedContracts()))
                .map(tuple -> CreateIdentResponse.builder()
                        .kycUrl(tuple.getT1().getDebtorInformation().getDigitaleSignaturUrl())
                        .documents(tuple.getT2())
                        .kycProvider(getProvider())
                        .build()
                );
    }
}
