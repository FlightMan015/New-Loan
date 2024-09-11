package de.joonko.loan.messaging.config;

import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.avro.dto.dls_reports_data.DigitalLoansReportsDataTopic;
import de.joonko.loan.avro.dto.loan_demand.LoanDemandMessage;
import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.avro.dto.salary_account.FinleapToFtsTransactionalData;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountRequest;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import de.joonko.loan.avro.dto.user_additional_information.UserAdditionalInformation;
import de.joonko.loan.avro.dto.user_additional_information.UserAdditionalInformationRequest;
import de.joonko.loan.avro.dto.user_details.create.UserCreateEvent;
import de.joonko.loan.avro.dto.user_details.delete.UserDeleteEvent;
import de.joonko.loan.avro.dto.user_details.login.UserLoginEvent;
import de.joonko.loan.avro.dto.user_details.update.UserUpdateEvent;
import de.joonko.loan.common.messaging.KafkaTopicNames;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EmbeddedKafka(topics = {
        KafkaTopicNames.LOAN_OFFERS,
        KafkaTopicNames.LOAN_DEMAND,
        KafkaTopicNames.SALARY_ACCOUNT_RESPONSE,
        KafkaTopicNames.USER_LOGIN_EVENT,
        KafkaTopicNames.USER_DELETE_EVENT,
        KafkaTopicNames.USER_CREATE_EVENT,
        KafkaTopicNames.USER_UPDATE_EVENT,
        KafkaTopicNames.DAC_ACCOUNT_SNAPSHOT,
        KafkaTopicNames.DIGITAL_LOANS_REPORT_DATA,
        KafkaTopicNames.USER_ADDITIONAL_INFO_RESPONSE
}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "data.support.enabled=true")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("integration")
public class KafkaTestConfig {

    @Autowired
    private KafkaProperties kafkaProperties;

    @MockBean
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @Autowired
    private EmbeddedKafkaBroker broker;

    protected Consumer<String, QuerySalaryAccountRequest> querySalaryAccountRequestConsumer;
    protected Consumer<String, DigitalLoansReportsDataTopic> digitalLoansReportsDataTopicRequestConsumer;
    protected Consumer<String, UserAdditionalInformationRequest> userAdditionalInformationRequestConsumer;
    protected Consumer<String, FinleapToFtsTransactionalData> finleapToFtsTransactionalDataConsumer;
    protected Consumer<String, LoanDemandMessage> testLoanDemandConsumer;
    protected Consumer<String, LoanOffersMessage> testLoanOfferConsumer;

    protected Producer<String, QuerySalaryAccountResponse> querySalaryAccountResponseProducer;
    protected Producer<String, DigitalLoansReportsDataTopic> digitalLoansReportsDataTopicProducer;
    protected Producer<String, UserDeleteEvent> userDeletionProducer;
    protected Producer<String, UserCreateEvent> userCreateProducer;
    protected Producer<String, UserUpdateEvent> userUpdateProducer;
    protected Producer<String, UserLoginEvent> userLoginProducer;
    protected Producer<String, DacAccountSnapshot> dacAccountSnapshotProducer;
    protected Producer<String, UserAdditionalInformation> userAdditionalInformationProducer;

    public static final String AUTO_COMMIT = "false";

    @BeforeAll
    void beforeAll() {
        setUpConsumers();
        setUpProducers();
    }

    private void setUpConsumers() {
        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumerGroupId", AUTO_COMMIT, broker));
        configs.putAll(consumerProperties);

        configs.put("group.id", Instant.now().toString());
        querySalaryAccountRequestConsumer = new KafkaConsumer<>(configs);
        querySalaryAccountRequestConsumer.subscribe(List.of(KafkaTopicNames.SALARY_ACCOUNT_REQUEST));

        configs.put("group.id", Instant.now().toString());
        userAdditionalInformationRequestConsumer = new KafkaConsumer<>(configs);
        userAdditionalInformationRequestConsumer.subscribe(List.of(KafkaTopicNames.USER_ADDITIONAL_INFO_REQUEST));

        configs.put("group.id", Instant.now().toString());
        finleapToFtsTransactionalDataConsumer = new KafkaConsumer<>(configs);
        finleapToFtsTransactionalDataConsumer.subscribe(List.of(KafkaTopicNames.FINLEAP_TO_FTS));

        configs.put("group.id", Instant.now().toString());
        testLoanDemandConsumer = new KafkaConsumer<>(configs);
        testLoanDemandConsumer.subscribe(List.of(KafkaTopicNames.LOAN_DEMAND));

        configs.put("group.id", Instant.now().toString());
        testLoanOfferConsumer = new KafkaConsumer<>(configs);
        testLoanOfferConsumer.subscribe(List.of(KafkaTopicNames.LOAN_OFFERS));

        configs.put("group.id", Instant.now().toString());
        digitalLoansReportsDataTopicRequestConsumer = new KafkaConsumer<>(configs);
        digitalLoansReportsDataTopicRequestConsumer.subscribe(List.of(KafkaTopicNames.DIGITAL_LOANS_REPORT_DATA));
    }

    private void setUpProducers() {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();

        querySalaryAccountResponseProducer = new KafkaProducer<>(producerProperties);
        userDeletionProducer = new KafkaProducer<>(producerProperties);
        userCreateProducer = new KafkaProducer<>(producerProperties);
        userUpdateProducer = new KafkaProducer<>(producerProperties);
        userLoginProducer = new KafkaProducer<>(producerProperties);
        dacAccountSnapshotProducer = new KafkaProducer<>(producerProperties);
        userAdditionalInformationProducer = new KafkaProducer<>(producerProperties);
        digitalLoansReportsDataTopicProducer = new KafkaProducer<>(producerProperties);
    }

    @AfterAll
    void afterAll() {
        testLoanOfferConsumer.close();
        testLoanDemandConsumer.close();
        querySalaryAccountRequestConsumer.close();
        userAdditionalInformationRequestConsumer.close();
        finleapToFtsTransactionalDataConsumer.close();
        digitalLoansReportsDataTopicRequestConsumer.close();
    }
}
