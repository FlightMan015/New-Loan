package de.joonko.loan.data.support.mapper;

import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.avro.dto.loan_offers.offers_record;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.OfferUpdateType;
import de.joonko.loan.offer.api.LoanProvider;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface LoanOfferMapper extends BaseMapper{

    @Mapping(target = "userUUID", source = "offerRequest.userUUID")
    @Mapping(target = "userId", source = "offerRequest.bonifyUserId")
    @Mapping(target = "applicationId", source = "applicationId")
    @Mapping(target = "timestamp", expression = "java(generateTimestamp())")
    @Mapping(target = "bonifyLoansCount", source = "bonifyLoanOffers")
    @Mapping(target = "otherLoansCount", source = "otherLoanOffers")
    @Mapping(target = "askedForBonifyLoans", source = "offerRequest", qualifiedByName = "mapRequestedBonifyLoans")
    @Mapping(target = "requestedLoanAmount", source = "offerRequest.requestedAmount")
    LoanOffersMessage mapLoanOffer(OfferRequest offerRequest, String applicationId, Collection<LoanOfferStore> offers, int bonifyLoanOffers, int otherLoanOffers);

    @Mapping(target = "userUUID", source = "userUUID")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "applicationId", source = "applicationId")
    @Mapping(target = "timestamp", expression = "java(generateTimestamp())")
    @Mapping(target = "updateType", expression = "java(updateType.name())")
    LoanOffersMessage mapLoanOffer(Long userId, String userUUID, String applicationId, Collection<LoanOfferStore> offers, OfferUpdateType updateType);

    @Mapping(target = "amount", source = "offer.amount")
    @Mapping(target = "durationInMonth", source = "offer.durationInMonth")
    @Mapping(target = "effectiveInterestRate", source = "offer.effectiveInterestRate")
    @Mapping(target = "nominalInterestRate", source = "offer.nominalInterestRate")
    @Mapping(target = "loanProvider", source = "offer.loanProvider")
    @Mapping(target = "monthlyRate", source = "offer.monthlyRate")
    @Mapping(target = "totalPayment", source = "offer.totalPayment")
    @Mapping(target = "offerId", source = "loanOfferId")
    offers_record map(LoanOfferStore offerStore);

    @Named("mapRequestedBonifyLoans")
    default boolean mapRequestedBonifyLoans(OfferRequest offerRequest) {
        return offerRequest.isRequestedBonifyLoans();
    }

    default String map(LoanProvider loanProvider) {
        return loanProvider == null ? "N/A" : loanProvider.getName();
    }
}