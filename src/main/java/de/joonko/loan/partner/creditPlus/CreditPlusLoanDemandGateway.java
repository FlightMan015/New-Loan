package de.joonko.loan.partner.creditPlus;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.common.partner.creditplus.auth.CreditPlusAuthService;
import de.joonko.loan.config.CreditPlusConfig;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.vo.LoanApplicationAuditTrailStatus;
import de.joonko.loan.exception.CreditPlusContractException;
import de.joonko.loan.exception.CreditPlusNoActionException;
import de.joonko.loan.offer.domain.*;
import de.joonko.loan.partner.creditPlus.mapper.model.CreditPlusOffer;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "creditplus.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class CreditPlusLoanDemandGateway implements LoanDemandGateway<CreditPlusLoanProviderApiMapper, EfinComparerServiceStub.CreateCreditOfferDacE, List<EfinComparerServiceStub.Contract>> {

    private final CreditPlusLoanProviderApiMapper mapper;
    private final CreditPlusConfig creditPlusConfig;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final CreditPlusStoreService creditPlusStoreService;
    private final DataSupportService dataSupportService;
    private final CreditPlusAuthService creditPlusAuthService;
    private final CreditPlusContractService creditPlusContractService;

    @Override
    public LoanProvider getLoanProvider() {
        return LoanProvider.builder().name(Bank.CREDIT_PLUS.getLabel()).build();
    }

    @Override
    public CreditPlusLoanProviderApiMapper getMapper() {
        return mapper;
    }

    @Override
    public Mono<List<EfinComparerServiceStub.Contract>> callApi(EfinComparerServiceStub.CreateCreditOfferDacE createCreditOfferE, String id) {
        loanApplicationAuditTrailService.sendingLoanDemandRequest(id, Bank.CREDIT_PLUS);
        log.info("Sending loan demand request to CreditPlus for {} ", id);
        List<String> dealerOrderNumbers = getDurationsForAmount(createCreditOfferE.getCreateCreditOfferDac().getEfinComparerCreditOffer().getAmount()).stream()
                .map(duration -> createOfferAndReturnDealerNumber(createCreditOfferE, id, String.valueOf(duration.value)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return getCreditPlusOffers(id, dealerOrderNumbers)
                .flatMap(creditPlusOffers -> downStreamProcessOrders(id, creditPlusOffers));
    }

    @Override
    public Boolean filterGateway(LoanDemand loanDemand) {
        if (loanDemand.getEmploymentDetails().getEmploymentType().equals(EmploymentType.OTHER) || !loanDemand.getPersonalDetails().getNationality().equals(Nationality.DE) || loanDemand.getDuration().getValue() >= 84 || loanDemand.getLoanAsked() > 10000) {
            return true;
        }
        return false;
    }

    @Override
    public List<LoanDuration> getDurations(Integer loanAsked) {
        //return dummy duration
        return List.of(de.joonko.loan.offer.domain.LoanDuration.FORTY_EIGHT);
    }

    private String createOfferAndReturnDealerNumber(EfinComparerServiceStub.CreateCreditOfferDacE createCreditOfferE, String id, String duration) {
        try {
            return createCreditOffer(createCreditOfferE, id, duration);
        } catch (Exception e) {
            loanApplicationAuditTrailService.saveApplicationError(id, e.getMessage(), Bank.CREDIT_PLUS.label);
        }
        return null;
    }

    private Mono<List<EfinComparerServiceStub.Contract>> getCreditPlusOffers(String id, List<String> dealerOrderNumbers) {
        log.info("Starting async check for dealerNumbers {} ", dealerOrderNumbers);
        try {
            creditPlusContractService.checkForContractInLoop(id, dealerOrderNumbers);
        } catch (CreditPlusContractException e) {

        } catch (CreditPlusNoActionException e) {
            //No action Needed. This exception is thrown to come out of Retryable loop in CreditPlusContractService.checkForContractInLoop
        } catch (Exception e) {
            final String exceptionMessage = "Error occurred while checking for updates in contracts";
            log.error(exceptionMessage, id);
            loanApplicationAuditTrailService.receivedLoanDemandResponseError(id, exceptionMessage.concat(e.getMessage() != null ? e.getMessage() : "null"), Bank.CREDIT_PLUS);
        }
        log.info("All checks done. Now getting the final list of contracts");

        List<EfinComparerServiceStub.Contract> contracts = List.of(creditPlusContractService.getContract(id, dealerOrderNumbers));
        creditPlusContractService.updateContractState(contracts);
        return Mono.just(contracts);
    }

    @Async
    Mono<List<EfinComparerServiceStub.Contract>> downStreamProcessOrders(String id, List<EfinComparerServiceStub.Contract> creditPlusOffers) {
        updateOffersToStore(id, creditPlusOffers);
        pushOfferRecord(id, creditPlusOffers);

        return Mono.just(filterApprovedContracts(creditPlusOffers, id));
    }

    private List<EfinComparerServiceStub.Contract> filterApprovedContracts(List<EfinComparerServiceStub.Contract> contracts, String applicationId) {
        List<EfinComparerServiceStub.Contract> filteredContracts = new ArrayList();
        List<CreditPlusOffer> offers = creditPlusStoreService.findByApplicationId(applicationId);
        offers.forEach(offer -> {
            List<Integer> contractState = offer.getContractState();
            if (offer.getCreditOffer().getContractState() == CreditPlusDefaults.STATUS_APPROVED) {
                filteredContracts.add(offer.getCreditOffer());
            }
            if (contractState.get(contractState.size() - 1) == CreditPlusDefaults.STATUS_WITHDRAW) {
                for (int i = contractState.size() - 1; i >= 1; i--) {
                    if (contractState.get(i) == CreditPlusDefaults.STATUS_WITHDRAW && contractState.get(i-1) == CreditPlusDefaults.STATUS_APPROVED) {
                        filteredContracts.add(offer.getCreditOffer());
                        break;
                    }
                }
            }
        });
        return filteredContracts;
    }

    @Async
    void updateOffersToStore(String id, List<EfinComparerServiceStub.Contract> creditPlusOffers) {
        creditPlusOffers.forEach(offer -> {
            creditPlusStoreService.updateOffer(CreditPlusOffer.builder()
                    .applicationId(id)
                    .dealerOrderNumber(offer.getDealerOrderNumber())
                    .creditOffer(offer)
                    .build());
            loanApplicationAuditTrailService.receivedLoanDemandResponseCreditPlus(id, offer);
        });
    }

    @Async
    void pushOfferRecord(String id, List<EfinComparerServiceStub.Contract> offers) {
        offers.forEach(offer -> {
            String status = "";
            Integer contractState = offer.getContractState();
            switch (contractState) {
                case CreditPlusDefaults.STATUS_WITHDRAW:
                case CreditPlusDefaults.STATUS_APPROVED:
                    status = LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_GREEN_PROFILE.name();
                    break;
                case CreditPlusDefaults.STATUS_DOCS_ISSUE:
                case CreditPlusDefaults.STATUS_IN_PROGRESS:
                    status = LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_YELLOW_PROFILE.name();
                    dataSupportService.pushYellowOffersReceivedTopic(mapper.fromLoanProviderResponse(List.of(offer)), id, offer.getContractStateText());
                    break;
                case CreditPlusDefaults.STATUS_DECLINE:
                case CreditPlusDefaults.STATUS_SOFT_DECLINE:
                case CreditPlusDefaults.STATUS_94:
                    status = LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_RED_PROFILE.name();
                    pushRedOffersReceivedToDataAnalytics(id, offer);
                    break;
            }
            loanApplicationAuditTrailService.receivedCreditPlusOffer(id, offer.getContractState(), offer.getDealerOrderNumber(), status);
        });
    }

    private String createCreditOffer(EfinComparerServiceStub.CreateCreditOfferDacE createCreditOfferE, String id, String duration) {
        log.info("Sending loan demand request to CreditPlus For {} and duration {}", id, duration);
        try {
            EfinComparerServiceStub efinComparerServiceStub = new EfinComparerServiceStub(creditPlusConfig.getHost().concat(creditPlusConfig.getService()));
            efinComparerServiceStub._getServiceClient().addHeader(creditPlusAuthService.getServiceClient());
            createCreditOfferE.getCreateCreditOfferDac().getEfinComparerCreditOffer().setDealerOrderNumber(id.concat(duration));
            createCreditOfferE.getCreateCreditOfferDac().getEfinComparerCreditOffer().setDuration(Integer.valueOf(duration));
            EfinComparerServiceStub.CreateCreditOfferDacResponseE creditOffer = efinComparerServiceStub.createCreditOfferDac(createCreditOfferE);

            log.info("Response from Credit Plus for {} is Success {}"
                    , id.concat(duration)
                    , creditOffer.getCreateCreditOfferDacResponse().getConfirmation().getIsSucceed());

            if (creditOffer.getCreateCreditOfferDacResponse().getConfirmation().getIsSucceed()) {
                creditPlusStoreService.saveOffer(CreditPlusOffer.builder()
                        .applicationId(id)
                        .dealerOrderNumber(id.concat(duration))
                        .contractState(List.of())
                        .applicationStatusUpdated(false)
                        .build());
                return createCreditOfferE.getCreateCreditOfferDac().getEfinComparerCreditOffer().getDealerOrderNumber();

            } else {
                loanApplicationAuditTrailService.saveApplicationError(id, getErrorMessages(creditOffer.getCreateCreditOfferDacResponse().getConfirmation().getErrorItems()), Bank.CREDIT_PLUS.label);
            }
        } catch (Exception e) {
            loanApplicationAuditTrailService.saveApplicationError(id, e.getMessage(), Bank.CREDIT_PLUS.label);
        }
        return null;
    }

    private void pushRedOffersReceivedToDataAnalytics(String id, EfinComparerServiceStub.Contract offer) {
        if (offer.getContractState() == CreditPlusDefaults.STATUS_DECLINE
                || offer.getContractState() == CreditPlusDefaults.STATUS_SOFT_DECLINE) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                dataSupportService.pushRedOffersReceivedTopic(id, Bank.CREDIT_PLUS.toString(), offer.getContractStateText(), offer.getContractStateText(), mapper.writeValueAsString(offer));
            } catch (Exception e) {
                log.info("failed to send red offers topic to data analytics team for loanApplicationId : {} , error {}", id, e.getMessage());
            }
        }
    }

    private static String getErrorMessages(EfinComparerServiceStub.ErrorItem[] errors) {
        return Arrays.stream(errors).map(
                errorItem -> errorItem.getField().concat("-").concat(errorItem.getMessage())
        ).collect(Collectors.joining("--"));
    }


    private List<LoanDuration> getDurationsForAmount(BigDecimal amount) {
        if (amount.intValue() == 1000) {
            return List.of(LoanDuration.TWELVE, LoanDuration.TWENTY_FOUR, LoanDuration.THIRTY_SIX, LoanDuration.FORTY_EIGHT);
        } else if (amount.intValue() > 1000 && amount.intValue() <= 1500) {
            return List.of(LoanDuration.TWENTY_FOUR, LoanDuration.TWELVE, LoanDuration.THIRTY_SIX, LoanDuration.FORTY_EIGHT);
        } else if (amount.intValue() > 1500 && amount.intValue() <= 2500) {
            return List.of(LoanDuration.TWENTY_FOUR, LoanDuration.THIRTY_SIX, LoanDuration.FORTY_EIGHT, LoanDuration.TWELVE);
        } else if (amount.intValue() > 2500 && amount.intValue() <= 4000) {
            return List.of(LoanDuration.FORTY_EIGHT, LoanDuration.THIRTY_SIX, LoanDuration.TWENTY_FOUR, LoanDuration.EIGHTY_FOUR, LoanDuration.SIXTY);
        } else if (amount.intValue() > 4000 && amount.intValue() <= 6000) {
            return List.of(LoanDuration.EIGHTY_FOUR, LoanDuration.SIXTY, LoanDuration.FORTY_EIGHT, LoanDuration.THIRTY_SIX);
        } else if (amount.intValue() > 6000 && amount.intValue() <= 10000) {
            return List.of(LoanDuration.EIGHTY_FOUR, LoanDuration.SIXTY, LoanDuration.FORTY_EIGHT, LoanDuration.SEVENTY_TWO);
        } else
            return List.of(LoanDuration.SIXTY, LoanDuration.SEVENTY_TWO, LoanDuration.EIGHTY_FOUR);
    }
}
