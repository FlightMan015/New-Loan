package de.joonko.loan.messaging;

import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.avro.dto.dls_reports_data.DigitalLoansReportsDataTopic;
import de.joonko.loan.avro.dto.dls_reports_data.Offer;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import de.joonko.loan.avro.dto.user_details.delete.UserDeleteEvent;
import de.joonko.loan.common.messaging.KafkaTopicNames;
import de.joonko.loan.integrations.domain.integrationhandler.fts.UserTransactionalDataIngester;
import de.joonko.loan.messaging.config.KafkaTestConfig;
import de.joonko.loan.offer.domain.OffersStatusSyncingService;
import de.joonko.loan.user.service.UserAdditionalInformationService;

import lombok.SneakyThrows;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import reactor.core.publisher.Mono;

import java.util.UUID;

import static de.joonko.loan.messaging.MessageTestData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageConsumerIT extends KafkaTestConfig {

    @MockBean
    private UserTransactionalDataIngester userTransactionalDataIngester;

    @MockBean
    private UserAdditionalInformationService userAdditionalInformationService;

    @MockBean
    private OffersStatusSyncingService offersStatusSyncingService;

    @SneakyThrows
    @Test
    void consume_salary_account() {
        // given
        String userUuid = UUID.randomUUID().toString();
        when(userTransactionalDataIngester.handleQuerySalaryAccountResponseFromDS(any(QuerySalaryAccountResponse.class))).thenReturn(Mono.empty());
        QuerySalaryAccountResponse salaryAccountResponse = getQuerySalaryAccountResponseTestData(userUuid);
        ProducerRecord<String, QuerySalaryAccountResponse> producerRecord = new ProducerRecord<>(KafkaTopicNames.SALARY_ACCOUNT_RESPONSE, userUuid, salaryAccountResponse);

        // when
        querySalaryAccountResponseProducer.send(producerRecord);

        // then
        verify(userTransactionalDataIngester, timeout(2000).times(1)).handleQuerySalaryAccountResponseFromDS(any(QuerySalaryAccountResponse.class));
    }

    @SneakyThrows
    @Test
    void consume_user_delete() {
        // given
        String userUuid = UUID.randomUUID().toString();
        when(userAdditionalInformationService.deleteUserData(userUuid)).thenReturn(Mono.empty());
        UserDeleteEvent userDeletion = getUserDeletionEventTestData(userUuid);
        ProducerRecord<String, UserDeleteEvent> producerRecord = new ProducerRecord<>(KafkaTopicNames.USER_DELETE_EVENT, userUuid, userDeletion);

        // when
        userDeletionProducer.send(producerRecord);

        //then
        verify(userAdditionalInformationService, timeout(2000).times(1)).deleteUserData(userUuid);
    }

    @SneakyThrows
    @Test
    void consume_dac_account_snapshot() {
        // given
        String userUuid = UUID.randomUUID().toString();
        when(userTransactionalDataIngester.handleDacResponse(any(DacAccountSnapshot.class))).thenReturn(Mono.empty());
        DacAccountSnapshot dacAccountSnapshot = getDacAccountSnapshotTestData(userUuid);
        ProducerRecord<String, DacAccountSnapshot> producerRecord = new ProducerRecord<>(KafkaTopicNames.DAC_ACCOUNT_SNAPSHOT, userUuid, dacAccountSnapshot);

        // when
        dacAccountSnapshotProducer.send(producerRecord);

        //then
        verify(userTransactionalDataIngester, timeout(2000).times(1)).handleDacResponse(any(DacAccountSnapshot.class));
    }


    @SneakyThrows
    @Test
    void consume_digital_loan_report() {
        // given
        String userUuid = UUID.randomUUID().toString();
        when(offersStatusSyncingService.syncFromDS(any(DigitalLoansReportsDataTopic.class))).thenReturn(Mono.empty());
        DigitalLoansReportsDataTopic digitalLoansReportsDataTopicTestData = getDigitalLoansReportsDataTopicTestData();
        ProducerRecord<String, DigitalLoansReportsDataTopic> producerRecord = new ProducerRecord<>(KafkaTopicNames.DIGITAL_LOANS_REPORT_DATA, null, digitalLoansReportsDataTopicTestData);

        // when
        digitalLoansReportsDataTopicProducer.send(producerRecord);

        // then
        verify(offersStatusSyncingService, timeout(2000).times(1)).syncFromDS(any(DigitalLoansReportsDataTopic.class));
    }
}
