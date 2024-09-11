package de.joonko.loan.reporting.api;

import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.reporting.api.model.GetOfferStatusResponse;
import de.joonko.loan.reporting.domain.OfferStatus;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface OfferStatusMapper {

    @Mapping(target = "offerProvider", source = "offerStatus.offerProvider", qualifiedByName = "extractOfferProvider")
    GetOfferStatusResponse from(OfferStatus offerStatus);

    @Named("extractOfferProvider")
    default String extractOfferProvider(LoanProvider offerProvider) {
        return Optional.ofNullable(offerProvider)
                .map(LoanProvider::getName)
                .orElse(null);
    }

    @Mapping(target = "userUUID", source = "userStatesStore.userUUID")
    @Mapping(target = "distributionChannelUUID", source = "userStatesStore.tenantId")
    @Mapping(target = "bankAccountAddedAt", source = "userStatesStore.transactionalDataStateDetails", qualifiedByName = "extractBankAccountAddedAt")
    @Mapping(target = "personalDataAddedAt", source = "userStatesStore.userPersonalInformationStateDetails", qualifiedByName = "extractPersonalDataAddedAt")
    @Mapping(target = "purpose", source = "stateDetails.purpose")
    @Mapping(target = "loanAmountRequested", source = "stateDetails.amount")
    @Mapping(target = "loanAmountRequestedAt", source = "stateDetails.requestDateTime")
    OfferStatus to(UserStatesStore userStatesStore, OfferDataStateDetails stateDetails);

    @Named("extractBankAccountAddedAt")
    default OffsetDateTime extractBankAccountAddedAt(TransactionalDataStateDetails stateDetails) {
        return Optional.ofNullable(stateDetails)
                .map(TransactionalDataStateDetails::getResponseDateTime)
                .orElse(null);
    }

    @Named("extractPersonalDataAddedAt")
    default OffsetDateTime extractPersonalDataAddedAt(StateDetails stateDetails) {
        return Optional.ofNullable(stateDetails)
                .map(StateDetails::getResponseDateTime)
                .orElse(null);
    }
}
