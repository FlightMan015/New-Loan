package de.joonko.loan.data.support;

import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.avro.dto.dls_reports_data.DigitalLoansReportsDataTopic;
import de.joonko.loan.avro.dto.loan_demand.LoanDemandMessage;
import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.avro.dto.salary_account.FinleapToFtsTransactionalData;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountRequest;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import de.joonko.loan.avro.dto.user_additional_information.UserAdditionalInformationRequest;
import de.joonko.loan.avro.dto.user_details.User;
import de.joonko.loan.avro.dto.user_details.create.UserCreateEvent;
import de.joonko.loan.avro.dto.user_details.delete.UserDeleteEvent;
import de.joonko.loan.avro.dto.user_details.login.UserLoginEvent;
import de.joonko.loan.avro.dto.user_details.update.UserUpdateEvent;
import de.joonko.loan.common.messaging.KafkaTopicNames;
import de.joonko.loan.config.annotation.LoadAndClearLoggingContext;
import de.joonko.loan.data.support.model.KycInitiationTopic;
import de.joonko.loan.data.support.model.KycStatusTopic;
import de.joonko.loan.data.support.model.OfferTopic;
import de.joonko.loan.data.support.model.PersonalDetailTopic;
import de.joonko.loan.data.support.model.RedOfferTopic;
import de.joonko.loan.integrations.domain.integrationhandler.fts.UserTransactionalDataIngester;
import de.joonko.loan.offer.domain.OffersStatusSyncingService;
import de.joonko.loan.user.service.UserAdditionalInformationService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(
        value = "data.support.enabled",
        havingValue = "true",
        matchIfMissing = true)
@LoadAndClearLoggingContext(prefix = "kafka")
public class DataSupportGatewayImpl implements DataSupportGateway {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private UserAdditionalInformationService userAdditionalInformationService;

    @Autowired
    private UserTransactionalDataIngester userTransactionalDataIngester;

    @Autowired
    private OffersStatusSyncingService offersStatusSyncingService;

    @Override
    public void pushPersonalDetailsTopic(PersonalDetailTopic personalDetailTopic) {
//        sendMessage(KafkaTopicNames.PERSONAL_USER_DETAILS, stringifyMessage(personalDetailTopic), personalDetailTopic.getDacId(), personalDetailTopic.getId());
    }

    @Override
    public void pushGreenOffersTopic(OfferTopic offer) {
//        sendMessage(KafkaTopicNames.GREEN_OFFERS, stringifyMessage(offer), offer.getDacId(), offer.getId());
    }

    @Override
    public void pushYellowOffersTopic(OfferTopic offer) {
//        sendMessage(KafkaTopicNames.YELLOW_OFFERS, stringifyMessage(offer), offer.getDacId(), offer.getId());
    }

    @Override
    public void pushRedOffersTopic(RedOfferTopic offer) {
//        sendMessage(KafkaTopicNames.RED_OFFERS, stringifyMessage(offer), offer.getDacId(), offer.getId());
    }

    @Override
    public void pushKycInitiatedTopic(KycInitiationTopic kycInitiationTopic) {
//        sendMessage(KafkaTopicNames.KYC_INITIALIZATION, stringifyMessage(kycInitiationTopic), kycInitiationTopic.getDacId(), kycInitiationTopic.getId());
    }

    @Override
    public void pushKycStatusTopic(KycStatusTopic kycStatusTopicModel) {
//        sendMessage(KafkaTopicNames.KYC_STATUS, stringifyMessage(kycStatusTopicModel), kycStatusTopicModel.getDacId(), kycStatusTopicModel.getId());
    }

    private void sendMessage(String topic, String message, String dacId, String id) {

        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, id, message);
        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("Sent message to data team with dacId=[" + dacId + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message to data team with dacId=[" + dacId + "] due to : " + ex.getMessage());
            }
        });
    }

    private <T> void sendMessage(ProducerRecord<String, T> producerRecord, Object loggingIdentifier) {
        ListenableFuture<SendResult<String, T>> future = kafkaTemplate.send(producerRecord);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, T> result) {
                log.info("successfully sent message to topic {}, id = {}", producerRecord.topic(), loggingIdentifier);
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("unable to sent message to topic {}, id = {}", producerRecord.topic(), loggingIdentifier, ex);
            }
        });

    }

    @Override
    public void pushToQuerySalaryAccountTopic(QuerySalaryAccountRequest message, Object loggingIdentifier) {
        ProducerRecord<String, QuerySalaryAccountRequest> producerRecord = new ProducerRecord<>(KafkaTopicNames.SALARY_ACCOUNT_REQUEST, message.getUserUUID(), message);
        sendMessage(producerRecord, loggingIdentifier);
    }

    @Override
    public void sendToLoanDemandTopic(LoanDemandMessage message, Object loggingIdentifier) {
        ProducerRecord<String, LoanDemandMessage> producerRecord = new ProducerRecord<>(KafkaTopicNames.LOAN_DEMAND, message.getUserUUID(), message);
        sendMessage(producerRecord, loggingIdentifier);
    }

    @Override
    public void sendToLoanOffersTopic(LoanOffersMessage message, Object loggingIdentifier) {
        ProducerRecord<String, LoanOffersMessage> producerRecord = new ProducerRecord<>(KafkaTopicNames.LOAN_OFFERS, message.getUserUUID(), message);
        sendMessage(producerRecord, loggingIdentifier);
    }

    @Override
    @KafkaListener(topics = {KafkaTopicNames.USER_LOGIN_EVENT})
    public void listenToUserLoginTopic(ConsumerRecord<String, UserLoginEvent> record) {
        String userUuid = record.value().getEvent().getUser().getId();
        // offerRequestManager.getOffers(userUuid);

    }

    @Override
    // @KafkaListener(topics = {KafkaTopicNames.USER_UPDATE_EVENT})
    // needs different handling
    public void listenToUserUpdateTopic(ConsumerRecord<String, UserUpdateEvent> record) {
        User user = record.value().getEvent().getUser();
//        offerRequestManager.refreshUserPersonalInformation(user.getId());
    }

    @Override
    // @KafkaListener(topics = {KafkaTopicNames.USER_CREATE_EVENT})
    public void listenToUserCreateTopic(ConsumerRecord<String, UserCreateEvent> record) {
        User user = record.value().getEvent().getUser();
        // offerRequestManager.getOffers(user.getId());
    }

    @Override
    @KafkaListener(topics = {KafkaTopicNames.USER_DELETE_EVENT})
    public void listenToUserDeleteTopic(ConsumerRecord<String, UserDeleteEvent> record) {
        log.info("received delete response message from DS with key {}", record.key());
        String userUuid = record.value().getEvent().getUser().getId();
        userAdditionalInformationService.deleteUserData(userUuid).subscribe();
    }


    @KafkaListener(topics = {KafkaTopicNames.DAC_ACCOUNT_SNAPSHOT})
    public void listenToDacAccountSnapshotTopic(ConsumerRecord<String, DacAccountSnapshot> record) {
        log.info("received dac snapshot message from DAC with key {}", record.key());
        userTransactionalDataIngester.handleDacResponse(record.value()).subscribe();
    }

    @Override
    @KafkaListener(topics = KafkaTopicNames.SALARY_ACCOUNT_RESPONSE)
    public void listenToQuerySalaryAccountResponseTopic(ConsumerRecord<String, QuerySalaryAccountResponse> record) {
        log.info("received salary account response message from DS with key {}", record.key());
        userTransactionalDataIngester.handleQuerySalaryAccountResponseFromDS(record.value()).subscribe();
    }

    @Override
    @KafkaListener(topics = KafkaTopicNames.DIGITAL_LOANS_REPORT_DATA)
    public void listenToDigitalLoansReportsDataTopic(ConsumerRecord<String, DigitalLoansReportsDataTopic> record) {
        log.info("received digital loan report message from DS with offers count - {}", record.value().getOffers().size());
        offersStatusSyncingService.syncFromDS(record.value()).subscribe();
    }

    @Override
    public void sendToDacApiForClassification(FinleapToFtsTransactionalData finleapToFtsTransactionalData, String userUUID) {
        ProducerRecord<String, FinleapToFtsTransactionalData> producerRecord = new ProducerRecord<>(KafkaTopicNames.FINLEAP_TO_FTS, finleapToFtsTransactionalData.getUserUUID(), finleapToFtsTransactionalData);
        sendMessage(producerRecord, userUUID);
    }

    @Override
    public void queryDataSolutionForUserAdditionalInformation(UserAdditionalInformationRequest userAdditionalInformationRequest, String loggingIdentifier) {
        ProducerRecord<String, UserAdditionalInformationRequest> producerRecord = new ProducerRecord<>(KafkaTopicNames.USER_ADDITIONAL_INFO_REQUEST, userAdditionalInformationRequest.getUuid(), userAdditionalInformationRequest);
        sendMessage(producerRecord, loggingIdentifier);
    }
}
