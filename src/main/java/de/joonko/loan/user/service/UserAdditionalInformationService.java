package de.joonko.loan.user.service;

import de.joonko.loan.offer.api.model.UserPersonalDetails;
import de.joonko.loan.user.api.model.Consent;

import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface UserAdditionalInformationService {

    Mono<Optional<UserAdditionalInformationStore>> findById(final String userUUID);

    Mono<Void> handleUserInput(final String userUUID, final UserPersonalDetails userPersonalDetails);

    Mono<Void> deleteUserData(final String userUUID);

    List<Consent> saveConsents(final String userUUID, final List<Consent> consentList, final String clientIP);

    List<Consent> getUserConsents(final String userUUID);

    Mono<List<UserAdditionalInformationStore>> removeFtsData(@NotNull List<String> userUuids);
}
