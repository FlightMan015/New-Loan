package de.joonko.loan.integrations.domain;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.OffersState;
import de.joonko.loan.integrations.model.PersonalDataState;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.UserStatesStore;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static de.joonko.loan.integrations.model.OffersState.ERROR;
import static de.joonko.loan.integrations.model.OffersState.IN_PROGRESS;
import static de.joonko.loan.integrations.model.OffersState.MISSING_OR_STALE;
import static de.joonko.loan.integrations.model.OffersState.OFFERS_EXIST;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserStateEvaluator {

    private final GetOffersConfigurations config;

    private final OffersStateValidator offersStateValidator;

    public OffersState getOffersState(int requestedAmount, String requestedPurpose, UserStatesStore userStatesStore) {
        if (areOffersValid(requestedAmount, requestedPurpose, userStatesStore)) {
            return OFFERS_EXIST;
        } else if (isOfferInErrorState(requestedAmount, requestedPurpose, userStatesStore)) {
            return ERROR;
        } else if (isOfferFetchingInProgress(userStatesStore)) {
            return IN_PROGRESS;
        } else if (isOfferFetchingStuckInInfiniteLoop(userStatesStore)) {
            return ERROR;
        }
        return MISSING_OR_STALE;
    }

    public DacDataState getTransactionalDataState(final UserStatesStore userStatesStore) {
        if (isFTSDataValid(userStatesStore)) {
            return DacDataState.FTS_DATA_EXISTS;
        } else if (isFTSInteractionInProgress(userStatesStore)) {
            //Waiting for FTS classification
            return DacDataState.FETCHING_FROM_FTS;
        } else if (isFTSInteractionLate(userStatesStore) || addedNonSalaryAccount(userStatesStore)) {
            return DacDataState.MISSING_SALARY_ACCOUNT;
        } else if (isDSRequestSuccessfullyFinished(userStatesStore)) {
            return DacDataState.MISSING_ACCOUNT_CLASSIFICATION;
        } else if (isDSRequestInProgress(userStatesStore)) {
            return DacDataState.FETCHING_FROM_DS;
        } else if (isDSRequestLate(userStatesStore)) {
            return DacDataState.MISSING_SALARY_ACCOUNT;
        } else if (fetchingUserDetailsFromFusionAuthIsInProgress(userStatesStore)) {
            return DacDataState.FETCHING_FROM_FUSIONAUTH;
        } else if (noBankAccountAdded(userStatesStore)) {
            return DacDataState.NO_ACCOUNT_ADDED;
        }
        return DacDataState.MISSING_OR_STALE;
    }

    private boolean noBankAccountAdded(final UserStatesStore userStatesStore) {
        return !userStatesStore.isBonifyUser() ||
                ofNullable(userStatesStore.getTransactionalDataStateDetails().getUserVerifiedByBankAccount())
                        .map(verifiedByBankAccount -> !verifiedByBankAccount)
                        .orElse(false); // TODO: test null case
    }

    private boolean fetchingUserDetailsFromFusionAuthIsInProgress(final UserStatesStore userStatesStore) {
        return Objects.isNull(userStatesStore.getDistributionChannel()) ||
                (userStatesStore.isBonifyUser() &&
                        (Objects.isNull(userStatesStore.getBonifyUserId()) ||
                                Objects.isNull(userStatesStore.getTransactionalDataStateDetails()) ||
                                Objects.isNull(userStatesStore.getTransactionalDataStateDetails().getUserVerifiedByBankAccount())));
    }

    public PersonalDataState getPersonalDataStateOld(UserStatesStore userStatesStore) {
        final var personalState = userStatesStore.getUserPersonalInformationStateDetails();
        if (personalState == null) {
            return PersonalDataState.MISSING_OR_STALE;
        } else if (personalState.getState() == Status.MISSING_USER_INPUT) {
            return PersonalDataState.USER_INPUT_REQUIRED;
        } else if (offersStateValidator.areStillValidPersonalDataOld(personalState)) {
            return PersonalDataState.EXISTS;
        } else {
            return PersonalDataState.MISSING_OR_STALE;
        }
    }

    public PersonalDataState getPersonalDataState(UserStatesStore userStatesStore) {
        final var personalState = userStatesStore.getUserPersonalInformationStateDetails();
        if (personalState == null) {
            return PersonalDataState.MISSING_OR_STALE;
        } else if (offersStateValidator.areStillValidPersonalData(personalState)) {
            return PersonalDataState.EXISTS;
        }
        return PersonalDataState.USER_INPUT_REQUIRED;
    }

    // TODO: when offers are set as expired
    // TODO: when offers are soft deleted
    private boolean areOffersValid(int requestedAmount, String requestedPurpose, UserStatesStore userStatesStore) {
        return Stream.ofNullable(userStatesStore.getOfferDateStateDetailsSet())
                .flatMap(Collection::stream)
                .filter(offersDetails -> Objects.nonNull(offersDetails.getResponseDateTime()))
                .filter(offersDetails -> offersDetails.getResponseDateTime().isAfter(OffsetDateTime.now().minusDays(config.getOffersValidityInDays())))
                .filter(offersDetails -> requestedAmount == offersDetails.getAmount())
                .filter(offersDetails -> requestedPurpose.equals(offersDetails.getPurpose()))
                .filter(OfferDataStateDetails::isSuccess)
                .map(OfferDataStateDetails::isNotExpired)
                .peek(o -> log.debug("Offer details with same amount {} and same purpose exist for userId: {}", requestedAmount, userStatesStore.getUserUUID()))
                .reduce(false, (x, y) -> x || y);
    }

    private boolean isOfferInErrorState(int requestedAmount, String requestedPurpose, final UserStatesStore userStatesStore) {
        return Stream.ofNullable(userStatesStore.getOfferDateStateDetailsSet())
                .flatMap(Collection::stream)
                .filter(offersDetails -> Objects.nonNull(offersDetails.getResponseDateTime()))
                .filter(offersDetails -> offersDetails.getResponseDateTime().isAfter(OffsetDateTime.now().minusDays(config.getOffersValidityInDays())))
                .filter(offersDetails -> requestedAmount == offersDetails.getAmount())
                .filter(offersDetails -> requestedPurpose.equals(offersDetails.getPurpose()))
                .filter(OfferDataStateDetails::isError)
                .map(OfferDataStateDetails::isNotExpired)
                .peek(o -> log.debug("Offer details with same amount {} and same purpose exist for userId: {}", requestedAmount, userStatesStore.getUserUUID()))
                .reduce(false, (x, y) -> x || y);
    }

    private boolean isOfferFetchingInProgress(UserStatesStore userStatesStore) {
        return getInProgressOffers(userStatesStore)
                .count() == 1;
    }

    private boolean isOfferFetchingStuckInInfiniteLoop(UserStatesStore userStatesStore) {
        return getInProgressOffers(userStatesStore)
                .count() > 1;
    }

    private Stream<OfferDataStateDetails> getInProgressOffers(UserStatesStore userStatesStore) {
        return Stream.ofNullable(userStatesStore.getOfferDateStateDetailsSet())
                .flatMap(Collection::stream)
                .filter(offersDetails -> Objects.isNull(offersDetails.getResponseDateTime()))
                .filter(offersDetails -> Objects.nonNull(offersDetails.getRequestDateTime()))
                .filter(offersDetails -> offersDetails.getRequestDateTime().isAfter(OffsetDateTime.now().minusSeconds(config.getStaleFetchingOffersRequestDurationInSeconds())));
    }


    private boolean isFTSDataValid(final UserStatesStore userStatesStore) {
        return ofNullable(userStatesStore.getTransactionalDataStateDetails())
                .map(transactionalDataStateDetails ->
                        transactionalDataStateDetails.isSuccess() &&
                                Objects.nonNull(transactionalDataStateDetails.getResponseDateTime()) &&
                                transactionalDataStateDetails.getResponseDateTime().isAfter(OffsetDateTime.now().minusDays(config.getTransactionalDataValidityInDays())))
                .orElse(false);
    }

    private boolean isDSRequestInProgress(final UserStatesStore userStatesStore) {
        return ofNullable(userStatesStore.getTransactionalDataStateDetails())
                .map(transactionalDataStateDetails ->
                        Objects.isNull(transactionalDataStateDetails.getResponseFromDataSolution()) && Objects.nonNull(transactionalDataStateDetails.getRequestFromDataSolution())
                                && transactionalDataStateDetails.getRequestFromDataSolution().isAfter(OffsetDateTime.now().minusSeconds(config.getStaleFetchingSalaryAccountInSeconds())))
                .orElse(false);
    }

    private boolean isDSRequestSuccessfullyFinished(final UserStatesStore userStatesStore) {
        return ofNullable(userStatesStore.getTransactionalDataStateDetails())
                .map(transactionalDataStateDetails ->
                        Objects.nonNull(transactionalDataStateDetails.getResponseFromDataSolution())
                                && Status.WAITING_TO_SEND_FOR_CLASSIFICATION.equals(transactionalDataStateDetails.getState()))
                .orElse(false);
    }

    private boolean isFTSInteractionInProgress(final UserStatesStore userStatesStore) {
        return ofNullable(userStatesStore.getTransactionalDataStateDetails())
                .map(transactionalDataStateDetails ->
                        Status.SENT_FOR_CLASSIFICATION.equals(transactionalDataStateDetails.getState()) &&
                                Objects.isNull(transactionalDataStateDetails.getResponseDateTime()) &&
                                transactionalDataStateDetails.getSentForClassification().isAfter(OffsetDateTime.now().minusSeconds(config.getStaleFetchingTransactionsClassificationInSeconds())))
                .orElse(false);
    }

    private boolean isFTSInteractionLate(final UserStatesStore userStatesStore) {
        return ofNullable(userStatesStore.getTransactionalDataStateDetails())
                .map(transactionalDataStateDetails ->
                        Status.SENT_FOR_CLASSIFICATION.equals(transactionalDataStateDetails.getState()) &&
                                Objects.isNull(transactionalDataStateDetails.getResponseDateTime()) &&
                                transactionalDataStateDetails.getSentForClassification().isBefore(OffsetDateTime.now().minusSeconds(config.getStaleFetchingTransactionsClassificationInSeconds())))
                .orElse(false);
    }

    private boolean isDSRequestLate(final UserStatesStore userStatesStore) {
        return ofNullable(userStatesStore.getTransactionalDataStateDetails())
                .map(transactionalDataStateDetails ->
                        Objects.isNull(transactionalDataStateDetails.getResponseFromDataSolution()) && Objects.nonNull(transactionalDataStateDetails.getRequestFromDataSolution())
                                && transactionalDataStateDetails.getRequestFromDataSolution().isBefore(OffsetDateTime.now().minusSeconds(config.getStaleFetchingSalaryAccountInSeconds())))
                .orElse(false);
    }

    private boolean addedNonSalaryAccount(final UserStatesStore userStatesStore) {
        return ofNullable(userStatesStore.getTransactionalDataStateDetails())
                .map(transactionalDataStateDetails ->
                        (Objects.nonNull(transactionalDataStateDetails.getResponseDateTime()) ||
                                Objects.nonNull(transactionalDataStateDetails.getResponseFromDataSolution())) &&
                                transactionalDataStateDetails.getState().equals(Status.MISSING_SALARY_ACCOUNT))
                .orElse(false);
    }
}
