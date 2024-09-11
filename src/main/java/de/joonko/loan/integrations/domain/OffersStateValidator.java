package de.joonko.loan.integrations.domain;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.TransactionalDataStateDetails;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Component
public class OffersStateValidator {

    private final GetOffersConfigurations config;

    public boolean isStaleGettingTransactionData(TransactionalDataStateDetails transactionalDataStateDetails) {
        return ofNullable(transactionalDataStateDetails)
                .map(TransactionalDataStateDetails::getRequestFromDataSolution)
                .map(dateTime -> dateTime.isBefore(OffsetDateTime.now().minusSeconds(config.getStaleFetchingTransactionalDataRequestInSeconds())))
                .orElse(true);
    }

    public boolean isStaleGettingPersonalData(StateDetails stateDetails) {
        return ofNullable(stateDetails)
                .map(StateDetails::getRequestDateTime)
                .map(dateTime -> dateTime.isBefore(OffsetDateTime.now().minusSeconds(config.getStaleFetchingPersonalDetailsInSeconds())))
                .orElse(true);
    }

    public boolean isStaleGettingAdditionalInformation(StateDetails stateDetails) {
        return ofNullable(stateDetails)
                .map(StateDetails::getRequestDateTime)
                .map(dateTime -> dateTime.isBefore(OffsetDateTime.now().minusSeconds(config.getStaleFetchingAdditionalUserInformationRequestInSeconds())))
                .orElse(true);
    }

    public boolean isStaleGettingOffers(OfferDataStateDetails stateDetails) {
        return ofNullable(stateDetails)
                .map(OfferDataStateDetails::getRequestDateTime)
                .map(dateTime -> dateTime.isBefore(OffsetDateTime.now().minusSeconds(config.getStaleFetchingOffersRequestDurationInSeconds())))
                .orElse(true);
    }

    public boolean isStaleGettingSalaryAccount(TransactionalDataStateDetails transactionalDataStateDetails) {
        return ofNullable(transactionalDataStateDetails)
                .map(TransactionalDataStateDetails::getRequestFromDataSolution)
                .map(dateTime -> dateTime.isBefore(OffsetDateTime.now().minusSeconds(config.getStaleFetchingSalaryAccountInSeconds())))
                .orElse(true);
    }

    public boolean isStaleGettingTransactionsClassification(TransactionalDataStateDetails transactionalDataStateDetails) {
        return ofNullable(transactionalDataStateDetails)
                .map(TransactionalDataStateDetails::getSentForClassification)
                .map(dateTime -> dateTime.isBefore(OffsetDateTime.now().minusSeconds(config.getStaleFetchingTransactionsClassificationInSeconds())))
                .orElse(true);
    }

    public boolean areStillValidTransactionalData(TransactionalDataStateDetails transactionalDataStateDetails) {
        return ofNullable(transactionalDataStateDetails)
                .map(TransactionalDataStateDetails::getResponseDateTime)
                .map(dateTime -> dateTime.isAfter(OffsetDateTime.now().minusDays(config.getTransactionalDataValidityInDays())))
                .orElse(false);
    }

    public boolean areStillValidPersonalDataOld(StateDetails stateDetails) {
        return ofNullable(stateDetails)
                .filter(StateDetails::isSuccess)
                .map(StateDetails::getResponseDateTime)
                .map(dateTime -> dateTime.isAfter(OffsetDateTime.now().minusHours(config.getUserPersonalInformationValidityInHours())))
                .orElse(false);
    }

    public boolean areStillValidPersonalData(StateDetails stateDetails) {
        return ofNullable(stateDetails)
                .filter(StateDetails::isSuccess)
                .map(StateDetails::getResponseDateTime)
                .map(dateTime -> dateTime.isAfter(OffsetDateTime.now().minusDays(config.getUserPersonalInformationValidityInDays())))
                .orElse(false);
    }

    public boolean validUserDataForRequestedAmount(final int requestedAmount, final StateDetails userDataStateDetails) {
        if (requestedAmount < config.getUserAdditionalInputRequiredMinAmount()) {
            return true;
        }
        return ofNullable(userDataStateDetails.getAdditionalFieldsForHighAmountAdded())
                .orElse(false);
    }

    public boolean areStillValidOffers(OfferDataStateDetails stateDetails) {
        return ofNullable(stateDetails)
                .map(OfferDataStateDetails::getResponseDateTime)
                .map(dateTime -> dateTime.isAfter(OffsetDateTime.now().minusDays(config.getOffersValidityInDays())))
                .orElse(false);
    }
}
