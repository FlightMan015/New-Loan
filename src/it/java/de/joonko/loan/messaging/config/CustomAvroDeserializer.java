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

import org.apache.avro.Schema;

import java.util.Map;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

public class CustomAvroDeserializer extends KafkaAvroDeserializer {

    private static final Map<String, Schema> TOPIC_SCHEMA_MAP = Map.ofEntries(
            Map.entry(KafkaTopicNames.SALARY_ACCOUNT_REQUEST, QuerySalaryAccountRequest.SCHEMA$),
            Map.entry(KafkaTopicNames.SALARY_ACCOUNT_RESPONSE, QuerySalaryAccountResponse.SCHEMA$),
            Map.entry(KafkaTopicNames.LOAN_DEMAND, LoanDemandMessage.SCHEMA$),
            Map.entry(KafkaTopicNames.LOAN_OFFERS, LoanOffersMessage.SCHEMA$),
            Map.entry(KafkaTopicNames.USER_UPDATE_EVENT, UserUpdateEvent.SCHEMA$),
            Map.entry(KafkaTopicNames.USER_DELETE_EVENT, UserDeleteEvent.SCHEMA$),
            Map.entry(KafkaTopicNames.USER_LOGIN_EVENT, UserLoginEvent.SCHEMA$),
            Map.entry(KafkaTopicNames.USER_CREATE_EVENT, UserCreateEvent.SCHEMA$),
            Map.entry(KafkaTopicNames.DAC_ACCOUNT_SNAPSHOT, DacAccountSnapshot.SCHEMA$),
            Map.entry(KafkaTopicNames.DIGITAL_LOANS_REPORT_DATA, DigitalLoansReportsDataTopic.SCHEMA$),
            Map.entry(KafkaTopicNames.USER_ADDITIONAL_INFO_RESPONSE, UserAdditionalInformation.SCHEMA$),
            Map.entry(KafkaTopicNames.FINLEAP_TO_FTS, FinleapToFtsTransactionalData.SCHEMA$),
            Map.entry(KafkaTopicNames.USER_ADDITIONAL_INFO_REQUEST, UserAdditionalInformationRequest.SCHEMA$)
    );

    @Override
    public Object deserialize(String topic, byte[] bytes) {

        Schema schema = TOPIC_SCHEMA_MAP.get(topic);

        this.schemaRegistry = getMockClient(schema);

        return super.deserialize(topic, bytes);
    }

    private static SchemaRegistryClient getMockClient(final Schema schema$) {
        return new MockSchemaRegistryClient() {
            @Override
            public synchronized Schema getById(int id) {
                return schema$;
            }
        };
    }
}
