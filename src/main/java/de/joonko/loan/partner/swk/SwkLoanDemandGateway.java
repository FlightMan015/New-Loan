package de.joonko.loan.partner.swk;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.config.SwkConfig;
import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.offer.domain.DomainDefault;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.swk.mapper.SwkApplyForCreditRequestToCheckForCreditRequestMapper;
import de.joonko.loan.partner.swk.mapper.SwkApplyForCreditreditResponseToCheckForCreditResponseMapper;
import de.joonko.loan.partner.swk.model.SwkCreditApplicationOffer;
import de.joonko.loan.partner.swk.model.SwkOffer;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import de.joonko.loan.partner.swk.stub.PreCheckServiceStub;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "swk.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SwkLoanDemandGateway implements LoanDemandGateway<SwkLoanProviderApiMapper, CreditApplicationServiceStub.ApplyForCredit, List<CreditApplicationServiceStub.CreditOffer>> {

    private final SwkLoanProviderApiMapper mapper;
    private final FTSAccountSnapshotGateway ftsAccountSnapshotGateway;
    private final SwkStoreService swkStoreService;
    private final SwkConfig swkConfig;
    private final SwkPrecheckFilter precheckFilter;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final SwkApplyForCreditRequestToCheckForCreditRequestMapper swkApplyForCreditRequestToCheckForCreditRequestMapper;
    private final SwkApplyForCreditreditResponseToCheckForCreditResponseMapper swkApplyForCreditreditResponseToCheckForCreditResponseMapper;
    private final DataSupportService dataSupportService;

    @Override
    public LoanProvider getLoanProvider() {
        return LoanProvider.builder().name(Bank.SWK_BANK.getLabel()).build();
    }

    @Override
    public SwkLoanProviderApiMapper getMapper() {
        return mapper;
    }

    @Override
    public Mono<List<CreditApplicationServiceStub.CreditOffer>> callApi(CreditApplicationServiceStub.ApplyForCredit applyForCredit, String id) {
        loanApplicationAuditTrailService.sendingLoanDemandRequest(id, Bank.SWK_BANK);
        log.info("Sending loan demand request to SWK For {} ", applyForCredit.getRequest()
                .getClientIdentification()
                .getRequestId());
        CreditApplicationServiceStub creditApplicationServiceStub;
        applyForCredit.getRequest().getExtraInfo()[0].setName("fintec_raw_json_DN1");
        applyForCredit.getRequest().getExtraInfo()[0].setValue(getEncodedAccountSnapshot(id, applyForCredit.getRequest().getExtraInfo()[0].getValue()));
        try {
            creditApplicationServiceStub = new CreditApplicationServiceStub(swkConfig.getHost()
                    .concat(swkConfig.getCreditApplicationService()));
            try {
                return getOffers(creditApplicationServiceStub, applyForCredit, id)
                        .map(offers -> {
                            log.info("Received offer for duration {} ", offers.get(0).getDuration());
                            pushRedOffersReceivedToDataAnalytics(id, offers);
                            updateLoandApplicationAuditTrail(id, offers);
                            return filterGreenOffer(offers, id);
                        })
                        .doOnError(throwable -> loanApplicationAuditTrailService.receivedLoanDemandResponseError(id, throwable.getMessage(), Bank.SWK_BANK));
            } catch (RemoteException e) {
                loanApplicationAuditTrailService.saveApplicationError(id, e.getMessage(), Bank.SWK_BANK.label);
            }
        } catch (RemoteException e) {
            log.info("Error while fetching offers : {}, for applicationId : {} ", e.getMessage(), id);
            loanApplicationAuditTrailService.saveApplicationError(id, e.getMessage(), Bank.SWK_BANK.label);
        }
        return Mono.empty();
    }

    private void updateLoandApplicationAuditTrail(String id, List<CreditApplicationServiceStub.CreditOffer> offers) {
        offers.forEach(creditOffer -> {
            loanApplicationAuditTrailService.receivedLoanDemandResponseSwk(id, creditOffer);
            if (creditOffer.getStatus() == -1 || creditOffer.getStatus() == 0 || !creditOffer.getSuccess()) {
                loanApplicationAuditTrailService.receivedAsRedProfileSwk(id, creditOffer.getStatus(), creditOffer.getCustomerAccountNumber());
            } else if (creditOffer.getStatus() != -1 && creditOffer.getSuccess() && (creditOffer.getStatus() == 1 || creditOffer.getStatus() == 2)) {
                loanApplicationAuditTrailService.receivedAsGreenProfileSwk(id, creditOffer.getStatus(), creditOffer.getCustomerAccountNumber());
            }
        });
    }

    private List<CreditApplicationServiceStub.CreditOffer> filterGreenOffer(List<CreditApplicationServiceStub.CreditOffer> offers, String applicationId) {
        offers.forEach(offer -> swkStoreService.saveOffer(SwkOffer.builder().creditOffer(offer).applicationId(applicationId).build()));
        return offers.stream().filter(offer -> offer.getStatus() != -1 && offer.getSuccess() && (offer.getStatus() == 1 || offer.getStatus() == 2))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean filterGateway(LoanDemand loanDemand) throws RemoteException {
        if (!precheckFilter.test(loanDemand)) return true;
        CreditApplicationServiceStub.ApplyForCredit applyForCredit = mapper.toLoanProviderRequest(loanDemand, loanDemand.getDuration());
        CreditApplicationServiceStub creditApplicationServiceStub = new CreditApplicationServiceStub(swkConfig.getHost()
                .concat(swkConfig.getCreditApplicationService()));
        applyForCredit.getRequest().getExtraInfo()[0].setName("fintec_raw_json_DN1");
        applyForCredit.getRequest().getExtraInfo()[0].setValue(getEncodedAccountSnapshot(loanDemand.getLoanApplicationId(), applyForCredit.getRequest().getExtraInfo()[0].getValue()));

        CreditApplicationServiceStub.ApplyForCredit applyForCreditRequest = new CreditApplicationServiceStub.ApplyForCredit();
        CreditApplicationServiceStub.ApplyForCreditResponse applyForCreditResponse = null;
        applyForCredit.getRequest().setDuration(60);
        applyForCreditRequest.setRequest(applyForCredit.getRequest());

        applyForCreditResponse = creditApplicationServiceStub.applyForCredit(applyForCreditRequest);

        log.info("Response from SWK for {} with customerAccountNumber {} :is Success {} , status {} and error {}",
                applyForCredit.getRequest().getClientIdentification().getRequestId(),
                applyForCreditResponse.get_return().getCustomerAccountNumber(),
                applyForCreditResponse.get_return().getSuccess(),
                applyForCreditResponse.get_return().getStatus(),
                applyForCreditResponse.get_return().getErrorDetails().getMessage());
        CreditApplicationServiceStub.CreditOffer creditOffer = applyForCreditResponse.get_return();
        if (creditOffer.getSuccess() && (creditOffer.getStatus() == 1 || creditOffer.getStatus() == 2) && (StringUtils.isNotEmpty(creditOffer.getCustomerAccountNumber()))) {
            swkStoreService.saveCreditApplicationOffer(SwkCreditApplicationOffer.builder().creditOffer(creditOffer).applicationId(loanDemand.getLoanApplicationId()).build());
            return false;
        }
        return true;
    }

    @Override
    public List<LoanDuration> getDurations(Integer loanAsked) {
        return List.of(LoanDuration.TWENTY_FOUR, LoanDuration.THIRTY_SIX, LoanDuration.FORTY_EIGHT, LoanDuration.SIXTY, LoanDuration.SEVENTY_TWO, LoanDuration.EIGHTY_FOUR, LoanDuration.NINETY_SIX, LoanDuration.ONE_HUNDRED_EIGHT, LoanDuration.ONE_HUNDRED_TWENTY);
    }

    private void pushRedOffersReceivedToDataAnalytics(String id, List<CreditApplicationServiceStub.CreditOffer> offers) {
        for (CreditApplicationServiceStub.CreditOffer offer : offers) {
            if (offer.getStatus() == -1 || offer.getStatus() == 0 || !offer.getSuccess()) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    dataSupportService.pushRedOffersReceivedTopic(id, Bank.SWK_BANK.toString(), offer.getErrorDetails().getMessage(), String.valueOf(offer.getReasonOfDenial()), mapper.writeValueAsString(offer));
                } catch (Exception e) {
                    log.info("failed to send red offers topic to data analytics team for applicationId : {} , error {}", id, e.getMessage());
                }

            }
        }

    }

    private Mono<List<CreditApplicationServiceStub.CreditOffer>> getOffers(CreditApplicationServiceStub creditApplicationServiceStub, CreditApplicationServiceStub.ApplyForCredit applyForCredit, String id) throws RemoteException {
        return Mono.just(getPreCheckServiceOffers(applyForCredit, id));
    }

    private List<CreditApplicationServiceStub.CreditOffer> getPreCheckServiceOffers(CreditApplicationServiceStub.ApplyForCredit applyForCredit, String id) throws RemoteException {
        log.info("Finding offers from PreCheck service for application id {} ", id);
        PreCheckServiceStub.CheckForCredit checkForCreditRequest = new PreCheckServiceStub.CheckForCredit();
        PreCheckServiceStub.CheckForCredit checkForCredit = swkApplyForCreditRequestToCheckForCreditRequestMapper.toCheckForCreditRequest(applyForCredit);

        checkForCreditRequest.setRequest(checkForCredit.getRequest());

        PreCheckServiceStub preCheckServiceStub = new PreCheckServiceStub(swkConfig.getHost().concat(swkConfig.getPreCheckServiceHttpSoap12Endpoint()));
        PreCheckServiceStub.CheckForCreditResponse checkForCreditResponse = preCheckServiceStub.checkForCredit(checkForCredit);
        log.info("Response from SWK from  Pre Check Service  {} is Success {} , and error {}", checkForCredit.getRequest()
                .getClientIdentification()
                .getRequestId(), checkForCreditResponse.get_return()
                .getSuccess(), checkForCreditResponse.get_return()
                .getErrorDetails()
                .getMessage());
        loanApplicationAuditTrailService.receivedLoanDemandResponseFromPreCheckServiceSwk(id, checkForCreditResponse);
        List<CreditApplicationServiceStub.CreditOffer> creditOfferList = new ArrayList<>();
        PreCheckServiceStub.PreCheckResponse preCheckResponse = checkForCreditResponse.get_return();
        if (preCheckResponse.getSuccess()) {
            PreCheckServiceStub.CreditOffer[] creditOffers = preCheckResponse.getCreditOffers();
            for (int i = 0; i < creditOffers.length; i++) {
                //Offer with status 2 are valid offers for joonko
                if (creditOffers[i].getStatus() == 2) {

                    creditOffers[i].setSuccess(true);//Bug in SWK
                    creditOfferList.add(swkApplyForCreditreditResponseToCheckForCreditResponseMapper.toCheckForCreditRequest(creditOffers[i]));
                }
            }

        }
        log.info("Total Credit offers {} ", creditOfferList.size());
        return creditOfferList;
    }

    private String getEncodedAccountSnapshot(String applicationId, String transactionId) {
        log.info("SWK : Fetching accountSnapshot for transactionId {} ", transactionId);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(ftsAccountSnapshotGateway.getAccountSnapshot(transactionId, DomainDefault.FTS_QUERY_PARAM_VALUE_JSON2), StandardCharsets.UTF_8))) {
            JSONArray accountSnapshotArray = new JSONArray(br.lines().collect(Collectors.joining("\n")));
            return Base64.getEncoder().encodeToString(accountSnapshotArray.get(0).toString().getBytes());
        } catch (Exception e) {
            log.info("SWK : Failed fetching accountSnapshot for transactionId {} ", transactionId);
            loanApplicationAuditTrailService.saveApplicationError(applicationId, e.getMessage(), Bank.SWK_BANK.label);
            return null;
        }
    }

}
