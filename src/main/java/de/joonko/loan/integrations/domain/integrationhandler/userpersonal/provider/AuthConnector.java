package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.user.service.UserPersonalInformationStore;

import io.fusionauth.domain.User;

public interface AuthConnector {
    User getUserData(String userUUID);

    void updateUserData(UserPersonalInformationStore mappedUserPersonalInformationStore);
}
