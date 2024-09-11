package de.joonko.loan.messaging;

import de.joonko.loan.avro.dto.salary_account.FinleapToFtsTransactionalData;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountRequest;
import de.joonko.loan.avro.dto.user_additional_information.UserAdditionalInformationRequest;
import de.joonko.loan.common.messaging.KafkaTopicNames;
import de.joonko.loan.data.support.DataSupportGateway;
import de.joonko.loan.messaging.config.KafkaTestConfig;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.UUID;

import static de.joonko.loan.messaging.MessageTestData.getQuerySalaryAccountRequestTestData;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MessageProducerIT extends KafkaTestConfig {

    public static final String DAC_ID = "71aec813-2039-40e2-a327-a7f5aad8f2fe";

    @Autowired
    private DataSupportGateway dataSupportGateway;

    @Test
    void publish_to_salary_account_request_topic() {
        //given
        String userUuid = UUID.randomUUID().toString();
        QuerySalaryAccountRequest salaryAccountRequest = getQuerySalaryAccountRequestTestData(userUuid);

        //when
        dataSupportGateway.pushToQuerySalaryAccountTopic(salaryAccountRequest, DAC_ID);

        //then
        ConsumerRecord<String, QuerySalaryAccountRequest> singleRecord = KafkaTestUtils.getSingleRecord(querySalaryAccountRequestConsumer, KafkaTopicNames.SALARY_ACCOUNT_REQUEST);

        assertAll(
                () -> assertNotNull(singleRecord),
                () -> assertEquals(salaryAccountRequest.getUserUUID(), singleRecord.key()),
                () -> assertEquals(salaryAccountRequest.getUserUUID(), singleRecord.value().getUserUUID()),
                () -> assertEquals(salaryAccountRequest.getUserId(), singleRecord.value().getUserId()),
                () -> assertEquals(salaryAccountRequest.getMaxLastUpdateDate(), singleRecord.value().getMaxLastUpdateDate()),
                () -> assertEquals(salaryAccountRequest.getMinTransactionBookingDate(), singleRecord.value().getMinTransactionBookingDate())
        );
    }

    @Test
    void publish_to_finleap_to_fts_topic() {
        //given
        String userUuid = UUID.randomUUID().toString();
        FinleapToFtsTransactionalData finleapToFtsTransactionalData = FinleapToFtsTransactionalData.newBuilder()
                .setUserUUID(userUuid)
                .setInternalUse(false)
                .build();

        //when
        dataSupportGateway.sendToDacApiForClassification(finleapToFtsTransactionalData, userUuid);

        //then
        ConsumerRecord<String, FinleapToFtsTransactionalData> singleRecord = KafkaTestUtils.getSingleRecord(finleapToFtsTransactionalDataConsumer, KafkaTopicNames.FINLEAP_TO_FTS);


        assertAll(
                () -> assertNotNull(singleRecord),
                () -> assertEquals(finleapToFtsTransactionalData.getUserUUID(), singleRecord.key()),
                () -> assertEquals(finleapToFtsTransactionalData.getUserUUID(), singleRecord.value().getUserUUID()),
                () -> assertEquals(finleapToFtsTransactionalData.getInternalUse(), singleRecord.value().getInternalUse())
        );
    }

    @Test
    void publish_to_user_additional_info_request_topic() {
         //given
        String userUuid = "35004d2f-ee8a-45fe-97e9-0542e1a0160a";
        Long bonifyUserId = 7236987363L;
        UserAdditionalInformationRequest userAdditionalInformationRequest = UserAdditionalInformationRequest.newBuilder()
                .setUuid(userUuid)
                .setBonifyUserId(bonifyUserId)
                .build();
        String loggingIdentifier = String.format("uuid %s, bonify user id %d", userUuid, bonifyUserId);

         //when
        dataSupportGateway.queryDataSolutionForUserAdditionalInformation(userAdditionalInformationRequest, loggingIdentifier);

         //then
        ConsumerRecord<String, UserAdditionalInformationRequest> singleRecord = KafkaTestUtils.getSingleRecord(userAdditionalInformationRequestConsumer, KafkaTopicNames.USER_ADDITIONAL_INFO_REQUEST);


        assertAll(
                () -> assertNotNull(singleRecord),
                () -> assertEquals(userAdditionalInformationRequest.getUuid(), singleRecord.key()),
                () -> assertEquals(userAdditionalInformationRequest.getUuid(), singleRecord.value().getUuid()),
                () -> assertEquals(userAdditionalInformationRequest.getBonifyUserId(), singleRecord.value().getBonifyUserId())
        );
    }


}
