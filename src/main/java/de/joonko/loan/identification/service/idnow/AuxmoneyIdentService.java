package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.mapper.idnow.AuxmoneyCreateIdentRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.Documents;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.auxmoney.AuxmoneyContractGateway;
import de.joonko.loan.partner.auxmoney.AuxmoneyPushNotificationGateway;
import de.joonko.loan.partner.auxmoney.AuxmoneySingleOfferCallResponseStore;
import de.joonko.loan.partner.auxmoney.AuxmoneySingleOfferCallResponseStoreService;
import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import de.joonko.loan.partner.auxmoney.model.ContractResponse;
import de.joonko.loan.partner.auxmoney.model.CorrelationDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j

public class AuxmoneyIdentService extends IDNowIdentService {

    private final AuxmoneyCreateIdentRequestMapper auxmoneyCreateIdentRequestMapper;
    private final AuxmoneyContractGateway auxmoneyContractGateway;
    private final AuxmoneyPushNotificationGateway auxmoneyPushNotificationGateway;
    private final AuxmoneySingleOfferCallResponseStoreService auxmoneySingleOfferCallResponseStoreService;
    private final static String AUX_MONEY_PREFIX = "joonaux";

    public AuxmoneyIdentService(
            IdNowClientApi idNowClientApi,
            AuxmoneyCreateIdentRequestMapper auxmoneyCreateIdentRequestMapper,
            IdentificationPropConfig identificationPropConfig,
            AuxmoneyContractGateway auxmoneyContractGateway,
            AuxmoneyPushNotificationGateway auxmoneyPushNotificationGateway,
            IdentificationAuditService identificationService,
            AuxmoneySingleOfferCallResponseStoreService auxmoneySingleOfferCallResponseStoreService,
            IdentificationLinkService identificationLinkService) {
        super(idNowClientApi, identificationService, identificationPropConfig, identificationLinkService);
        this.auxmoneyCreateIdentRequestMapper = auxmoneyCreateIdentRequestMapper;
        this.auxmoneyContractGateway = auxmoneyContractGateway;
        this.auxmoneyPushNotificationGateway = auxmoneyPushNotificationGateway;
        this.auxmoneySingleOfferCallResponseStoreService = auxmoneySingleOfferCallResponseStoreService;
    }

    @Override
    public Mono<de.joonko.loan.identification.model.idnow.CreateIdentRequest> getIdentRequest(CreateIdentRequest createIdentRequest) {
        return Mono.just(auxmoneyCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest));
    }

    @Override
    public IdNowAccount getAccountId() {
        return IdNowAccount.AUXMONEY;
    }

    @Override
    public Mono<Documents> getDocuments(CreateIdentRequest createIdentRequest) {
        AuxmoneySingleCallResponse auxmoneySingleCallResponse = getAuxmoneySingleCallResponse(createIdentRequest.getApplicationId());
        return auxmoneyContractGateway.getContract(auxmoneySingleCallResponse.getUserId(), auxmoneySingleCallResponse.getCreditId())
                .map(ContractResponse::getContract)
                .map(contract -> Documents.builder()
                        .documents(List.of(
                                Document.builder()
                                        .content(contract)
                                        .documentId("contract")
                                        .build()
                        ))
                        .build());
    }

    @Override
    public Mono<String> partnerSpecificPostProcessing(String identId, CreateIdentRequest createIdentRequest) {
        AuxmoneySingleCallResponse auxmoneySingleCallResponse = getAuxmoneySingleCallResponse(createIdentRequest.getApplicationId());
        CorrelationDataRequest correlationRequestData = CorrelationDataRequest.builder()
                .auxCreditNo(auxmoneySingleCallResponse.getCreditId())
                .auxUserNo(auxmoneySingleCallResponse.getUserId())
                .idNowTransactionId(identId)
                .transactionNo(String.join("-", AUX_MONEY_PREFIX, createIdentRequest.getApplicationId()))
                .build();
        return auxmoneyPushNotificationGateway.sendCorrelationData(correlationRequestData)
                .doOnSuccess(voidResponseEntity -> identificationAuditService.notificationSent(createIdentRequest, identId))
                .doOnError(throwable -> identificationAuditService.notificationSentError(createIdentRequest, identId, throwable.getMessage()))
                .doOnNext(response -> log.info("Sent CorrelationData to Auxmoney Ident Id {} , Credit Id {} ", identId, auxmoneySingleCallResponse.getCreditId()))
                .thenReturn(identId);
    }

    private AuxmoneySingleCallResponse getAuxmoneySingleCallResponse(String loanApplicationId) {
        AuxmoneySingleOfferCallResponseStore userIdAndCrditIdForLoanApplication = auxmoneySingleOfferCallResponseStoreService.getAuxmoneySingleOfferCallResponseStoreByLoanApplicationId(loanApplicationId);
        return userIdAndCrditIdForLoanApplication.getAuxmoneySingleCallResponse();
    }
}
