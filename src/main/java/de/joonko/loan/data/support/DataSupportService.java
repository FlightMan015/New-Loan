package de.joonko.loan.data.support;

import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountRequest;
import de.joonko.loan.common.utils.DateTimeConverter;
import de.joonko.loan.data.support.mapper.DataOfferMapper;
import de.joonko.loan.data.support.mapper.PersonalDetailsMapper;
import de.joonko.loan.data.support.model.DataLoanOffer;
import de.joonko.loan.data.support.model.KycInitiationTopic;
import de.joonko.loan.data.support.model.KycStatusTopic;
import de.joonko.loan.data.support.model.OfferTopic;
import de.joonko.loan.data.support.model.RedOfferTopic;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.offer.domain.LoanDemand;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Async
public class DataSupportService {

    private final DataSupportGateway dataSupportGateway;
    private final PersonalDetailsMapper personalDetailsMapper;
    private final DataOfferMapper dataOfferMapper;
    private final LoanOfferStoreService loanOfferStoreService;
    private final LoanDemandStoreService loanDemandStoreService;

    @Async
    public void pushPersonalDetailsTopic(LoanDemand loanDemand, boolean internalUse) {
        try {
            dataSupportGateway.pushPersonalDetailsTopic(personalDetailsMapper.mapToPersonalDetails(loanDemand, internalUse));
        } catch (Exception ex) {
            log.error("Error pushing personal detail to data kafka", ex);
        }
    }

    @Async
    public void pushGreenOffersReceivedTopic(String applicationId, String dacId, boolean internalUse) {
        try {
            loanOfferStoreService.findAllByLoanApplicationId(applicationId)
                    .forEach(loanOfferStore -> {
                        DataLoanOffer dataLoanOffer = dataOfferMapper.mapLoanOffer(loanOfferStore.getLoanOfferId(), loanOfferStore.getOffer());
                        String loanProvider = loanOfferStore.getOffer().getLoanProvider().getName();
                        dataSupportGateway.pushGreenOffersTopic(mapDataLoanOfferEvent(applicationId, dacId, internalUse, dataLoanOffer, loanProvider, null));
                    });
        } catch (Exception ex) {
            log.error("Error pushing green offers to data kafka", ex);
        }
    }

    @Async
    public void pushYellowOffersReceivedTopic(List<de.joonko.loan.offer.domain.LoanOffer> loanOfferList, String applicationId, String remark) {
        LoanDemandStore loanDemandStore = loanDemandStoreService.findById(applicationId).orElseThrow(() -> new RuntimeException("Failed to get loanDemandStore while pushing yellow offer topic for applicationId : " + applicationId));

        loanOfferList.forEach(loanOffer -> {
            String loanProvider = loanOffer.getLoanProvider().getName();
            DataLoanOffer dataLoanOffer = DataLoanOffer.builder()
                    .amount(loanOffer.getAmount())
                    .durationInMonth(loanOffer.getDurationInMonth())
                    .effectiveInterestRate(loanOffer.getEffectiveInterestRate())
                    .nominalInterestRate(loanOffer.getNominalInterestRate())
                    .monthlyRate(loanOffer.getMonthlyRate())
                    .totalPayment(loanOffer.getTotalPayment())
                    .build();
            dataSupportGateway.pushYellowOffersTopic(mapDataLoanOfferEvent(applicationId, loanDemandStore.getDacId(), loanDemandStore.getInternalUse(), dataLoanOffer, loanProvider, remark));
        });
    }

    @Async
    public void pushRedOffersReceivedTopic(String applicationId, String loanProvider, String rejectReason, String rejectCode, String fullResponse) {
        LoanDemandStore loanDemandStore = loanDemandStoreService.findById(applicationId).orElseThrow(() -> new RuntimeException("Failed to get loanDemandStore while pushing red offer topic for applicationId : " + applicationId));
        RedOfferTopic redOfferTopic = RedOfferTopic.builder()
                .dacId(loanDemandStore.getDacId())
                .internalUse(loanDemandStore.getInternalUse())
                .applicationId(applicationId)
                .rejectCode(rejectCode)
                .rejectReason(rejectReason)
                .loanProvider(loanProvider)
                .fullResponse(fullResponse)
                .build();
        dataSupportGateway.pushRedOffersTopic(redOfferTopic);

    }

    @Async
    public void pushKycInitiationTopic(CreateIdentRequest createIdentRequest, CreateIdentResponse createIdentResponse, boolean internalUse, String dacId) {
        try {
            KycInitiationTopic kycInitiationTopic = KycInitiationTopic.builder()
                    .dacId(dacId)
                    .request(createIdentRequest)
                    .response(createIdentResponse)
                    .internalUse(internalUse)
                    .build();
            dataSupportGateway.pushKycInitiatedTopic(kycInitiationTopic);
        } catch (Exception ex) {
            log.error("Error pushing accept offer to data kafka", ex);
        }
    }

    @Async
    public void pushKycStatus(String dacId, String applicationId, String loanOfferId,
                              String url, String status, String loanProviderReferenceNumber, String kycReason) {
        try {
            KycStatusTopic kycStatusTopic = KycStatusTopic.builder()
                    .dacId(dacId)
                    .applicationId(applicationId)
                    .loanOfferId(loanOfferId)
                    .url(url)
                    .status(status)
                    .reason(kycReason)
                    .loanProviderReferenceNumber(loanProviderReferenceNumber)
                    .build();

            dataSupportGateway.pushKycStatusTopic(kycStatusTopic);
        } catch (Exception ex) {
            log.error("Error pushing kyc status data to kafka", ex);
        }
    }

    @Async
    public void pushSalaryAccountTopic(OffsetDateTime maxLastUpdateDate, OffsetDateTime minTransactionBookingDate, long userId, UUID userUUID,  String dacId) {
        try {
            QuerySalaryAccountRequest salaryAccountRequest = QuerySalaryAccountRequest.newBuilder()
                    .setUserId(userId)
                    .setUserUUID(userUUID.toString())
                    .setMaxLastUpdateDate(DateTimeConverter.toLong(maxLastUpdateDate))
                    .setMinTransactionBookingDate(DateTimeConverter.toLong(minTransactionBookingDate))
                    .build();

            dataSupportGateway.pushToQuerySalaryAccountTopic(salaryAccountRequest, dacId);
        } catch (Exception ex) {
            log.error("Error pushing salary account request to kafka", ex);
        }
    }

    private OfferTopic mapDataLoanOfferEvent(String id, String dacId, boolean internalUse, DataLoanOffer dataLoanOffer, String loanProvider, String remark) {
        return OfferTopic.builder()
                .dacId(dacId)
                .applicationId(id)
                .loanProvider(loanProvider)
                .offer(dataLoanOffer)
                .internalUse(internalUse)
                .remark(remark)
                .build();
    }
}
