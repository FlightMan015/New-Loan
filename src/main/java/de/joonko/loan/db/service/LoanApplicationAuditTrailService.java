package de.joonko.loan.db.service;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.repositories.LoanApplicationAuditTrailRepository;
import de.joonko.loan.db.vo.LoanApplicationAuditTrail;
import de.joonko.loan.db.vo.LoanApplicationAuditTrailStatus;
import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import de.joonko.loan.partner.solaris.model.SolarisAcceptOfferResponse;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import de.joonko.loan.partner.swk.stub.PreCheckServiceStub;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanApplicationAuditTrailService {

    private final LoanApplicationAuditTrailRepository loanApplicationAuditTrailRepository;

    public Flux<LoanApplicationAuditTrail> deleteByApplicationId(final String applicationId) {
        return Mono.fromCallable(() -> loanApplicationAuditTrailRepository.deleteByApplicationId(applicationId))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    @Async
    public void loanDemandReceived(String applicationId) {

        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_REQUEST_RECEIVED.name())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void sendingLoanDemandRequest(String applicationId, Bank bank) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .loanProvider(bank.name())
                .applicationId(applicationId)
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_REQUEST_SENT.name())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void receivedLoanDemandResponseAuxmoney(String applicationId, AuxmoneySingleCallResponse auxmoneySingleCallResponse) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.AUXMONEY.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_SUCCESS.name())
                .remark("Offer response  IsSuccess: " + auxmoneySingleCallResponse.getIsSuccess() + "  ManualQualityAssurance: " + auxmoneySingleCallResponse.getManualQualityAssurance() + ",Error: " + auxmoneySingleCallResponse.getIsError())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void receivedLoanDemandResponseError(String applicationId, String message, Bank bank) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(bank.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_ERROR.name())
                .remark("Error " + message)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void receivedAsGreenProfileConsors(String applicationId, String subscriptionStatus) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.CONSORS.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_GREEN_PROFILE.name())
                .remark("SubscriptionStatus :  " + subscriptionStatus)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedAsRedProfileConsors(String applicationId) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.CONSORS.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_RED_PROFILE.name())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedAsYellowProfileConsors(String applicationId, List<Integer> documents) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.CONSORS.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_YELLOW_PROFILE.name())
                .remark("List of documents needed : " + documents)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void acceptOfferRequestReceived(String applicationId, String bank) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(bank)
                .status(LoanApplicationAuditTrailStatus.ACCEPT_OFFER_REQUEST_RECEIVED.name())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void acceptOfferByInternalUserRequestReceived(String applicationId, String bank) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(bank)
                .status(LoanApplicationAuditTrailStatus.ACCEPT_OFFER_BY_INTERNAL_USER_REQUEST_RECEIVED.name())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void acceptOfferRequestSent(String applicationId, Bank bank) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(bank.name())
                .status(LoanApplicationAuditTrailStatus.ACCEPT_OFFER_REQUEST_SENT.name())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void acceptOfferResponseReceivedConsors(String applicationId, ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.CONSORS.name())
                .status(LoanApplicationAuditTrailStatus.ACCEPT_OFFER_RESPONSE_RECEIVED_SUCCESS.name())
                .remark("Accept offer contract identifier :" + consorsAcceptOfferResponse.getContractIdentifier() + ", Subscription status : " + consorsAcceptOfferResponse.getSubscriptionStatus())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void acceptOfferRequestServedSuccess(String applicationId, String offerId, String loanProvider) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(loanProvider)
                .remark("Offer Id " + offerId)
                .status(LoanApplicationAuditTrailStatus.ACCEPT_OFFER_REQUEST_SERVED_SUCCESS.name())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }


    @Async
    public void acceptOfferErrorResponseReceived(String applicationId, String message, Bank bank) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(bank.name())
                .status(LoanApplicationAuditTrailStatus.ACCEPT_OFFER_RESPONSE_RECEIVED_ERROR.name())
                .remark("Error " + message)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void dacEmailNotificationToConsorsSuccess(String applicationId, String contractIdentifier) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.CONSORS.name())
                .status(LoanApplicationAuditTrailStatus.DAC_EMAIL_NOTIFICATION_SUCCESS.name())
                .remark("contractIdentifier : " + contractIdentifier)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void dacEmailNotificationToConsorsError(String applicationId, String contractIdentifier, String message) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.CONSORS.name())
                .status(LoanApplicationAuditTrailStatus.DAC_EMAIL_NOTIFICATION_ERROR.name())
                .remark("contractIdentifier : " + contractIdentifier + ", Error : " + message)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void receivedLoanDemandResponseSwk(String id, CreditApplicationServiceStub.CreditOffer offer) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(id)
                .loanProvider(Bank.SWK_BANK.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED.name())
                .remark("Status : " + offer.getStatus() + ", Customer id :" + offer.getCustomerAccountNumber() + ", Success  " + offer.getSuccess() + ", Error :" + offer.getErrorDetails().getMessage())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void saveApplicationError(String applicationId, String error, String name) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(name)
                .status(LoanApplicationAuditTrailStatus.ERROR.name())
                .remark("Error: " + error)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void saveApplicationError(String applicationId, String error, Bank bank) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(bank.name())
                .status(LoanApplicationAuditTrailStatus.ERROR.name())
                .remark("Error: " + error)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void solarisKycInitiated(String applicationId) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.DEUTSCHE_FINANZ_SOZIETÄT.name())
                .status(LoanApplicationAuditTrailStatus.KYC_LINK_CREATED.name())
                .remark("KYC Pending")
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedAsRedProfileSolaris(String applicationId, String applicationStatus, String offerId) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.DEUTSCHE_FINANZ_SOZIETÄT.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_RED_PROFILE.name())
                .remark("applicationStatus :  " + applicationStatus + ", offerId :" + offerId)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedAsGreenProfileSolaris(String applicationId, String offerId) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.DEUTSCHE_FINANZ_SOZIETÄT.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_GREEN_PROFILE.name())
                .remark("Offer Id :" + offerId)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void acceptOfferResponseReceivedSolaris(String applicationId, SolarisAcceptOfferResponse solarisAcceptOfferResponse) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.DEUTSCHE_FINANZ_SOZIETÄT.name())
                .status(LoanApplicationAuditTrailStatus.ACCEPT_OFFER_RESPONSE_RECEIVED_SUCCESS.name())
                .remark("id :  " + solarisAcceptOfferResponse.getId() + ", loanStatus : " + solarisAcceptOfferResponse.getStatus().name())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedAsRedProfileSwk(String applicationId, int status, String customerAccountNumber) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.SWK_BANK.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_RED_PROFILE.name())
                .remark("applicationStatus Code:  " + status + ", customerAccountNumber :" + customerAccountNumber)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedAsGreenProfileSwk(String applicationId, int offerStatusCode, String customerAccountNumber) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.SWK_BANK.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_GREEN_PROFILE.name())
                .remark("offerStatusCode :" + offerStatusCode + ", customerAccountNumber :" + customerAccountNumber)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedLoanDemandResponseFromPreCheckServiceSwk(String id, PreCheckServiceStub.CheckForCreditResponse checkForCreditResponse) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(id)
                .loanProvider(Bank.SWK_BANK.name())
                .remark("checkForCreditResponse : " + checkForCreditResponse.get_return()
                        .getSuccess())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void remark(String id, Bank bank, String message) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(id)
                .loanProvider(bank.name())
                .status(LoanApplicationAuditTrailStatus.REMARK.name())
                .remark(message)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    public void receivedLoanDemanResponseSantander(String id, ScbCapsBcoWSStub.GetKreditvertragsangebotResponse offer) {
        ScbCapsBcoWSStub.AntragstatusType status = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getStatus();
        ScbCapsBcoWSStub.HinweisXO[] hinweise = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getHinweise();
        String reasonForRejection = (hinweise != null && hinweise.length > 0) ? String.valueOf(hinweise[0].getText()) : "";
        String scbAntragId = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getScbAntragId();
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(id)
                .loanProvider(Bank.SANTANDER.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED.name())
                .remark("Status : " + status + ", Scb Antrag Id :" + scbAntragId + ", Error :" + reasonForRejection)
                .build();
        if (!Strings.isNullOrEmpty(reasonForRejection)) {
            log.info("SANTANDER: loan Status : {} for Scb Antrag Id: {} applicationId: {}", status, scbAntragId, id);
        } else {
            log.info("SANTANDER: loan Status : {} for Scb Antrag Id: {} applicationId: {}, Error : {}", status, scbAntragId, id, reasonForRejection);
        }
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedAsGreenProfileSantander(String id, ScbCapsBcoWSStub.AntragstatusType status, String scbAntragId, BigInteger duration) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(id)
                .loanProvider(Bank.SANTANDER.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_GREEN_PROFILE.name())
                .remark("Status : " + status + ", Scb Antrag Id :" + scbAntragId + ", Duration :" + duration)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);

    }

    @Async
    public void receivedAsRedProfileSantander(String id, ScbCapsBcoWSStub.AntragstatusType status, String scbAntragId, BigInteger duration) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(id)
                .loanProvider(Bank.SANTANDER.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED_AS_RED_PROFILE.name())
                .remark("Status : " + status + ", Scb Antrag Id :" + scbAntragId + ", Duration :" + duration)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedLoanDemandResponseCreditPlus(String id, EfinComparerServiceStub.Contract offer) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(id)
                .loanProvider(Bank.CREDIT_PLUS.name())
                .status(LoanApplicationAuditTrailStatus.LOAN_DEMAND_RESPONSE_RECEIVED.name())
                .remark("Status : " + offer.getContractStateText() + ", DealerOrderNumber :" + offer.getDealerOrderNumber())
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }

    @Async
    public void receivedCreditPlusOffer(String applicationId, int contractState, String dealerOrderNumber, String status) {
        LoanApplicationAuditTrail loanApplicationAuditTrail = LoanApplicationAuditTrail.builder()
                .applicationId(applicationId)
                .loanProvider(Bank.CREDIT_PLUS.name())
                .status(status)
                .remark("contract state:  " + contractState + ", dealerOrderNumber :" + dealerOrderNumber)
                .build();
        loanApplicationAuditTrailRepository.save(loanApplicationAuditTrail);
    }
}


