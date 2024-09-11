package de.joonko.loan.data.support;

import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.avro.dto.salary_account.FinleapToFtsTransactionalData;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.offer.api.LoanDemandRequest;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface DataSolutionCommunicationManager {

    void sendLoanDemandRequest(Long bonifyUserId, LoanDemandRequest loanDemandRequest);

    Mono<LoanOffersMessage> sendLoanOffer(Long bonifyUserId, LoanOfferStore loanOfferStore, OfferUpdateType updateType);

    void sendLoanOffers(OfferRequest offerRequest, String applicationId, Collection<LoanOfferStore> offers, int bonifyLoansCount, int otherLoansCount);

    void updateLoanOffers(String userUUID, String applicationId, String updatedOfferId, OfferUpdateType updateType);

    void queryDataSolutionForSalaryAccount(String userUUID, Long bonifyUserId);

    void sendToDacApiForClassification(FinleapToFtsTransactionalData finleapToFtsTransactionalData);

    void queryDataSolutionForUserAdditionalInformation(String userUUID, Long bonifyUserId);
}
