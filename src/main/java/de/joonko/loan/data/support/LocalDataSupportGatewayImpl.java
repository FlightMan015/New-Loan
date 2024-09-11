package de.joonko.loan.data.support;

import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.avro.dto.dls_reports_data.DigitalLoansReportsDataTopic;
import de.joonko.loan.avro.dto.loan_demand.LoanDemandMessage;
import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.avro.dto.salary_account.FinleapToFtsTransactionalData;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountRequest;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import de.joonko.loan.avro.dto.user_additional_information.UserAdditionalInformationRequest;
import de.joonko.loan.avro.dto.user_details.create.UserCreateEvent;
import de.joonko.loan.avro.dto.user_details.delete.UserDeleteEvent;
import de.joonko.loan.avro.dto.user_details.login.UserLoginEvent;
import de.joonko.loan.avro.dto.user_details.update.UserUpdateEvent;
import de.joonko.loan.data.support.model.KycInitiationTopic;
import de.joonko.loan.data.support.model.KycStatusTopic;
import de.joonko.loan.data.support.model.OfferTopic;
import de.joonko.loan.data.support.model.PersonalDetailTopic;
import de.joonko.loan.data.support.model.RedOfferTopic;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "data.support.enabled",
        havingValue = "false")
public class LocalDataSupportGatewayImpl implements DataSupportGateway {

    @Override
    public void pushPersonalDetailsTopic(PersonalDetailTopic personalDetailTopic) {
        log.info("Personal details topic in data gateway with data {} is triggered", personalDetailTopic);
    }

    @Override
    public void pushGreenOffersTopic(OfferTopic offer) {
        log.info("Green Offers received topic in data gateway with data {} is triggered", offer);
    }

    @Override
    public void pushYellowOffersTopic(OfferTopic offer) {
        log.info("Yellow Offers received topic in data gateway with data {} is triggered", offer);
    }

    @Override
    public void pushRedOffersTopic(RedOfferTopic redOfferTopic) {
        log.info("Red Offers received topic in data gateway with data {} is triggered", redOfferTopic);
    }

    @Override
    public void pushKycInitiatedTopic(KycInitiationTopic kycInitiationTopic) {
        log.info("Kyc initiated topic in data gateway with data {} is triggered", kycInitiationTopic);
    }

    @Override
    public void pushKycStatusTopic(KycStatusTopic kycStatusTopic) {
        log.info("Kyc status topic in data gateway with data {} is triggered", kycStatusTopic);
    }

    @Override
    public void pushToQuerySalaryAccountTopic(QuerySalaryAccountRequest message, Object loggingIdentifier) {
        log.info("Salary account topic in data gateway with is triggered");
    }

    @Override
    public void sendToLoanDemandTopic(LoanDemandMessage message, Object loggingIdentifier) {
        log.info("loan demand topic in data gateway with is triggered");
    }

    @Override
    public void sendToLoanOffersTopic(LoanOffersMessage message, Object loggingIdentifier) {
        log.info("loan offers topic in data gateway with is triggered");
    }

    @Override
    public void listenToUserLoginTopic(ConsumerRecord<String, UserLoginEvent> record) {
        log.info("User login topic in data gateway with data {} is triggered", record.value());
    }

    @Override
    public void listenToUserUpdateTopic(ConsumerRecord<String, UserUpdateEvent> record) {
        log.info("User update topic in data gateway with data {} is triggered", record.value());

    }

    @Override
    public void listenToUserCreateTopic(ConsumerRecord<String, UserCreateEvent> record) {
        log.info("User create topic in data gateway with data {} is triggered", record.value());

    }

    @Override
    public void listenToUserDeleteTopic(ConsumerRecord<String, UserDeleteEvent> record) {
        log.info("User delete topic in data gateway with data {} is triggered", record.value());
    }

    @Override
    public void listenToDacAccountSnapshotTopic(ConsumerRecord<String, DacAccountSnapshot> record) {
        DacAccountSnapshot value = record.value();
        System.out.println();
    }

    @Override
    public void listenToQuerySalaryAccountResponseTopic(ConsumerRecord<String, QuerySalaryAccountResponse> record) {

    }

    @Override
    public void listenToDigitalLoansReportsDataTopic(ConsumerRecord<String, DigitalLoansReportsDataTopic> record) {

    }

    @Override
    public void sendToDacApiForClassification(FinleapToFtsTransactionalData finleapToFtsTransactionalData, String userUUID) {

    }

    @Override
    public void queryDataSolutionForUserAdditionalInformation(UserAdditionalInformationRequest userAdditionalInformationRequest, String loggingIdentifier) {

    }
}
