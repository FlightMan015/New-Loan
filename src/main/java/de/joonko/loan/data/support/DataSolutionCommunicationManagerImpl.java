package de.joonko.loan.data.support;

import de.joonko.loan.avro.dto.loan_demand.LoanDemandMessage;
import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.avro.dto.salary_account.FinleapToFtsTransactionalData;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountRequest;
import de.joonko.loan.avro.dto.user_additional_information.UserAdditionalInformationRequest;
import de.joonko.loan.common.utils.DateTimeConverter;
import de.joonko.loan.data.support.mapper.LoanDemandMapper;
import de.joonko.loan.data.support.mapper.LoanOfferMapper;
import de.joonko.loan.db.repositories.LoanOfferStoreRepository;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.offer.api.LoanDemandRequest;

import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Lazy
@Service
@Slf4j
public class DataSolutionCommunicationManagerImpl implements DataSolutionCommunicationManager {

    private static final int MIN_TRANSACTION_BOOKING_DATE_IN_DAYS = 90;
    private static final int MAX_LAST_UPDATE_DATE_IN_DAYS = 1;

    @Autowired
    private DataSupportGateway dataSupportGateway;
    @Autowired
    private LoanDemandMapper loanDemandMapper;
    @Autowired
    private LoanOfferMapper loanOfferMapper;
    @Autowired
    private LoanOfferStoreRepository loanOfferStoreRepository;
    @Autowired
    private UserStatesStoreService userStatesStoreService;
    @Autowired
    private DataSolutionPropertiesConfig dataSolutionPropertiesConfig;

    @Override
    public Mono<LoanOffersMessage> sendLoanOffer(Long bonifyUserId, LoanOfferStore loanOfferStore, OfferUpdateType updateType) {
        return Mono.just(bonifyUserId)
                .map(userId -> loanOfferMapper.mapLoanOffer(userId, loanOfferStore.getUserUUID(), loanOfferStore.getApplicationId(), List.of(loanOfferStore), updateType))
                .doOnNext(message -> log.info("bonify user id {} with uuid {} received update {}", bonifyUserId, loanOfferStore.getUserUUID(), updateType))
                .doOnNext(message -> dataSupportGateway.sendToLoanOffersTopic(message, loanOfferStore.getApplicationId()));
    }

    @Override
    @Async
    public void sendLoanDemandRequest(Long bonifyUserId, LoanDemandRequest loanDemandRequest) {
        LoanDemandMessage message = loanDemandMapper.mapLoanDemand(bonifyUserId, loanDemandRequest);
        log.info("Sending loan demand request to Kafka topic for application - {}", loanDemandRequest.getApplicationId());
        dataSupportGateway.sendToLoanDemandTopic(message, loanDemandRequest.getApplicationId());
    }

    @Override
    public void sendLoanOffers(OfferRequest offerRequest, String applicationId, Collection<LoanOfferStore> offers, int bonifyLoansCount, int otherLoansCount) {
        Mono.just(offerRequest.getUserUUID())
                .map(userId -> loanOfferMapper.mapLoanOffer(offerRequest, applicationId, offers, bonifyLoansCount, otherLoansCount))
                .delayElement(Duration.ofSeconds(dataSolutionPropertiesConfig.getDelayLoanOffer()))
                .doOnNext(message -> log.info("bonify user id {} with uuid {} received {} loan offers", message.getUserId(), message.getUserUUID(), message.getOffers().size()))
                //Adding delay to give enough time on DS side to process loan demand message before processing loan offer message
                .doOnNext(message -> dataSupportGateway.sendToLoanOffersTopic(message, applicationId))
                .subscribe();
    }

    @Override
    public void updateLoanOffers(String userUUID, String applicationId, String updatedOfferId, OfferUpdateType updateType) {
        userStatesStoreService.findById(userUUID)
                .map(UserStatesStore::getBonifyUserId)
                .doOnNext(bonifyUserId -> {
                    // Why should we send all offers again when only one single offer is updated?
                    // This is agreement reached with DS to avoid too much change on their side - to send all offers information in every update
                    Collection<LoanOfferStore> offerStoreList = loanOfferStoreRepository.getNotDeletedOffers(userUUID, applicationId);
                    LoanOffersMessage message = loanOfferMapper.mapLoanOffer(bonifyUserId, userUUID, applicationId, offerStoreList, updateType);
                    message.setUpdatedOfferId(updatedOfferId);
                    log.info("bonify user id {} with uuid {} received update for loan offer {} ", bonifyUserId, userUUID, offerStoreList.size());
                    dataSupportGateway.sendToLoanOffersTopic(message, applicationId);
                }).subscribe();
    }

    @Override
    public void queryDataSolutionForSalaryAccount(String userUUID, Long bonifyUserId) {
        OffsetDateTime now = OffsetDateTime.now();
        String identifier = getUserIdentifier(userUUID, bonifyUserId);
        QuerySalaryAccountRequest querySalaryAccountRequest = QuerySalaryAccountRequest.newBuilder()
                .setUserUUID(userUUID)
                .setUserId(Optional.ofNullable(bonifyUserId).orElse(-1L))
                .setMinTransactionBookingDate(DateTimeConverter.toLong(now.minusDays(MIN_TRANSACTION_BOOKING_DATE_IN_DAYS)))
                .setMaxLastUpdateDate(DateTimeConverter.toLong(now.minusDays(MAX_LAST_UPDATE_DATE_IN_DAYS)))
                .build();
        dataSupportGateway.pushToQuerySalaryAccountTopic(querySalaryAccountRequest, identifier);
    }

    @Override
    public void sendToDacApiForClassification(FinleapToFtsTransactionalData finleapToFtsTransactionalData) {
        dataSupportGateway.sendToDacApiForClassification(finleapToFtsTransactionalData, finleapToFtsTransactionalData.getUserUUID());
    }

    @Override
    public void queryDataSolutionForUserAdditionalInformation(String userUUID, Long bonifyUserId) {
        String userIdentifier = getUserIdentifier(userUUID, bonifyUserId);
        UserAdditionalInformationRequest userAdditionalInformationRequest = UserAdditionalInformationRequest.newBuilder()
                .setUuid(userUUID)
                .setBonifyUserId(Optional.ofNullable(bonifyUserId).orElse(-1L))
                .build();
        dataSupportGateway.queryDataSolutionForUserAdditionalInformation(userAdditionalInformationRequest, userIdentifier);
    }

    private String getUserIdentifier(String userUuid, Long bonifyUserId) {
        return String.format("uuid %s, bonify user id %s", userUuid, bonifyUserId);
    }
}
