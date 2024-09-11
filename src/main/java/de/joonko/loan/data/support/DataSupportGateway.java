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

public interface DataSupportGateway {

    void pushPersonalDetailsTopic(PersonalDetailTopic personalDetailTopic);

    void pushGreenOffersTopic(OfferTopic offer);

    void pushYellowOffersTopic(OfferTopic offer);

    void pushRedOffersTopic(RedOfferTopic redOfferTopic);

    void pushKycInitiatedTopic(KycInitiationTopic kycInitiationTopic);

    void pushKycStatusTopic(KycStatusTopic kycStatusTopic);

    void pushToQuerySalaryAccountTopic(QuerySalaryAccountRequest message, Object loggingIdentifier);

    void sendToLoanDemandTopic(LoanDemandMessage message, Object loggingIdentifier);

    void sendToLoanOffersTopic(LoanOffersMessage message, Object loggingIdentifier);

    void listenToUserLoginTopic(ConsumerRecord<String, UserLoginEvent> record);

    void listenToUserUpdateTopic(ConsumerRecord<String, UserUpdateEvent> record);

    void listenToUserCreateTopic(ConsumerRecord<String, UserCreateEvent> record);

    void listenToUserDeleteTopic(ConsumerRecord<String, UserDeleteEvent> record);

    void listenToDacAccountSnapshotTopic(ConsumerRecord<String, DacAccountSnapshot> record);

    void listenToQuerySalaryAccountResponseTopic(ConsumerRecord<String, QuerySalaryAccountResponse> record);

    void listenToDigitalLoansReportsDataTopic(ConsumerRecord<String, DigitalLoansReportsDataTopic> record);

    void sendToDacApiForClassification(FinleapToFtsTransactionalData finleapToFtsTransactionalData, String userUUID);

    void queryDataSolutionForUserAdditionalInformation(UserAdditionalInformationRequest userAdditionalInformationRequest, String loggingIdentifier);
}
