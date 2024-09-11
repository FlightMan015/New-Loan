package de.joonko.loan.partner.swk;

import de.joonko.loan.acceptoffer.domain.AcceptOfferGateway;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.config.SwkConfig;
import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.domain.DomainDefault;
import de.joonko.loan.partner.swk.model.SwkOffer;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "swk.enabled",
        havingValue = "true",
        matchIfMissing = true)

public class SwkAcceptOfferGateway implements AcceptOfferGateway<SwkAcceptOfferApiMapper, SwkAcceptOfferRequest, SwkAcceptOfferResponse> {

    private final SwkAcceptOfferApiMapper acceptOfferApiMapper;

    private final SwkStoreService swkStoreService;
    private final SwkConfig swkConfig;

    private final LoanOfferStoreService loanOfferStoreService;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final SwkLoanProviderApiMapper mapper;
    private final FTSAccountSnapshotGateway ftsAccountSnapshotGateway;

    @Override
    public SwkAcceptOfferApiMapper getMapper() {
        return acceptOfferApiMapper;
    }


    @Override
    public Mono<SwkAcceptOfferResponse> callApi(SwkAcceptOfferRequest swkAcceptOfferRequest, String applicationId, String offerId) {
        log.info("Accepting offer for SWK for loanApplication id {}, and offer id {}", applicationId, offerId);


        if (isPreCheckOffer(applicationId, swkAcceptOfferRequest.getOfferId())) {
            log.info("User Accepted offer from pre check service ");
            loanApplicationAuditTrailService.remark(applicationId, Bank.SWK_BANK, "User accepted offer from pre check service");
            CreditApplicationServiceStub.ApplyForCredit applyForCredit = swkAcceptOfferRequest.getApplyForCredit();
            try {
                CreditApplicationServiceStub creditApplicationServiceStub = new CreditApplicationServiceStub(swkConfig.getHost()
                        .concat(swkConfig.getCreditApplicationService()));

                applyForCredit.getRequest()
                        .getExtraInfo()[0].setName("fintec_raw_json_DN1");
                applyForCredit.getRequest()
                        .getExtraInfo()[0].setValue(getEncodedAccountSnapshot(applicationId, applyForCredit.getRequest()
                        .getExtraInfo()[0].getValue()));

                CreditApplicationServiceStub.ApplyForCredit applyForCreditRequest = new CreditApplicationServiceStub.ApplyForCredit();
                CreditApplicationServiceStub.ApplyForCreditResponse applyForCreditResponse = null;
                applyForCreditRequest.setRequest(swkAcceptOfferRequest.getApplyForCredit()
                        .getRequest());
                applyForCreditResponse = creditApplicationServiceStub.applyForCredit(applyForCreditRequest);
                log.info("Response from SWK for {} is Success {} , status {} and error {}", applyForCredit.getRequest()
                        .getClientIdentification()
                        .getRequestId(), applyForCreditResponse.get_return()
                        .getSuccess(), applyForCreditResponse.get_return()
                        .getStatus(), applyForCreditResponse.get_return()
                        .getErrorDetails()
                        .getMessage());
                CreditApplicationServiceStub.CreditOffer creditOffer = applyForCreditResponse.get_return();
                swkStoreService.saveOffer(SwkOffer.builder()
                        .applicationId(applicationId)
                        .creditOffer(creditOffer)
                        .build());
            } catch (Exception e) {
                log.error("Error while accepting application id {} , offer id", applicationId, swkAcceptOfferRequest.getOfferId());
                loanApplicationAuditTrailService.saveApplicationError(applicationId, e.getMessage(), Bank.SWK_BANK.name());
                loanApplicationAuditTrailService.acceptOfferErrorResponseReceived(applicationId, e.getMessage(), Bank.SWK_BANK);
                throw new RuntimeException("Error while accepting offer ", e);
            }
        }


        return Mono.just(new SwkAcceptOfferResponse());
    }

    private boolean isPreCheckOffer(String id, String offerId) {

        try {
            LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(offerId);
            log.info("Found offer with duration {}", acceptedOffer.getOffer().getDurationInMonth());
            return StringUtils.isEmpty(swkStoreService.getCustomerNumber(id, acceptedOffer.getOffer().getDurationInMonth()));
        } catch (Exception e) {
            return true;
        }


    }

    @Override
    public Mono<String> getLoanProviderReferenceNumber(String applicationId, String loanOfferId) {
        LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(loanOfferId);
        log.info("Found offer with duration {}", acceptedOffer.getOffer().getDurationInMonth());

        return Mono.fromCallable(() -> swkStoreService.getCustomerNumber(applicationId, acceptedOffer.getOffer().getDurationInMonth()))
                .subscribeOn(Schedulers.elastic());
    }

    private String getEncodedAccountSnapshot(String applicationId, String transactionId) {
        log.info("SWK : Fetching accountSnapshot for transactionId {} ", transactionId);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(ftsAccountSnapshotGateway.getAccountSnapshot(transactionId, DomainDefault.FTS_QUERY_PARAM_VALUE_JSON2), StandardCharsets.UTF_8))) {
            JSONArray accountSnapshotArray = new JSONArray(br.lines().collect(Collectors.joining("\n")));
            return Base64.getEncoder().encodeToString(accountSnapshotArray.get(0).toString().getBytes());
        } catch (Exception e) {
            log.info("SWK : Failed fetching accountSnapshot for transactionId {} ", transactionId);
            loanApplicationAuditTrailService.saveApplicationError(applicationId, e.getMessage(), Bank.SWK_BANK.label);
            return null;
        }
    }

    @Override
    public Bank getBank() {
        return Bank.SWK_BANK;
    }
}
