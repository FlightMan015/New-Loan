package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.partner.creditplus.auth.CreditPlusAuthService;
import de.joonko.loan.config.CreditPlusConfig;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.exception.CreditPlusContractException;
import de.joonko.loan.exception.CreditPlusNoActionException;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditPlusContractService {

    private final CreditPlusAuthService creditPlusAuthService;
    private final CreditPlusConfig creditPlusConfig;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final CreditPlusStoreService creditPlusStoreService;

    @Retryable(value = {CreditPlusContractException.class}, maxAttempts = 15, backoff = @Backoff(delay = CreditPlusDefaults.WAIT_TIME_FREQ))
    public void checkForContractInLoop(String applicationId, List<String> dealerOrderNumbers) throws CreditPlusContractException, CreditPlusNoActionException {

        List<EfinComparerServiceStub.Contract> contracts = List.of(getContract(applicationId, dealerOrderNumbers));
        updateContractState(contracts);
        int processedCountGreen = (int) contracts.stream().filter(contract -> contract.getContractState() == CreditPlusDefaults.STATUS_APPROVED || contract.getContractState() == CreditPlusDefaults.STATUS_WITHDRAW).count();
        int processedCountNonGreen = (int) contracts.stream().filter(contract -> CreditPlusDefaults.NON_GREEN_STATUS_LIST.contains(contract.getContractState())).count();
        if (processedCountGreen + processedCountNonGreen == dealerOrderNumbers.size()) {
            log.info("Green Contracts: {}, Non-Green contracts: {} ", processedCountGreen, processedCountNonGreen);
            throw new CreditPlusNoActionException("Exiting the loop to chedk for contracts status");
        } else {
            log.info("{} contracts processed", processedCountGreen);
        }

        throw new CreditPlusContractException("Exiting the loop to check for contracts status");
    }

    public EfinComparerServiceStub.Contract[] getContract(String id, List<String> dealerOrderNumber) {
        try {
            log.info("Getting single offer from Credit Plus for {}", dealerOrderNumber);
            return getSingleOffer(dealerOrderNumber);
        } catch (Exception e) {
            loanApplicationAuditTrailService.saveApplicationError(id, e.getMessage(), Bank.CREDIT_PLUS.label);
            return null;
        }

    }

    private EfinComparerServiceStub.Contract[] getSingleOffer(List<String> dealerOrderNumber) throws Exception {
        log.info("Sending request for {} on CreditPlus", dealerOrderNumber);
        EfinComparerServiceStub efinComparerServiceStub = new EfinComparerServiceStub(creditPlusConfig.getHost().concat(creditPlusConfig.getService()));
        EfinComparerServiceStub.GetContractsE getContractsE = new EfinComparerServiceStub.GetContractsE();
        EfinComparerServiceStub.GetContracts getContracts = new EfinComparerServiceStub.GetContracts();
        EfinComparerServiceStub.Filter filter = new EfinComparerServiceStub.Filter();
        filter.setDealerOrderNumber(dealerOrderNumber.toArray(String[]::new));
        getContracts.setFilter(filter);
        getContractsE.setGetContracts(getContracts);
        efinComparerServiceStub._getServiceClient().addHeader(creditPlusAuthService.getServiceClient());
        EfinComparerServiceStub.GetContractsResponseE getContractsResponseE = efinComparerServiceStub.getContracts(getContractsE);

        return getContractsResponseE.getGetContractsResponse().get_return();
    }

    public void updateContractState(List<EfinComparerServiceStub.Contract> contracts) {
        contracts.forEach(contract -> {
            creditPlusStoreService.updateContractState(contract.getDealerOrderNumber(), contract.getContractState());
            log.info("Contract state {} {} {} ", contract.getContractState(), contract.getContractStateText(), contract.getDealerOrderNumber());
        });
    }
}
