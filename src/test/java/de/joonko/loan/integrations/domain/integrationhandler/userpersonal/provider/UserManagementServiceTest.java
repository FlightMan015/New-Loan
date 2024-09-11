package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import io.fusionauth.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserManagementServiceTest {

    private UserPersonalDataProvider personalDataProvider;

    private AuthConnector authConnector;
    private UserPersonalDataMapper userPersonalDataMapper;

    private static final String USER_ID = "2f20a660-f0f2-4ca5-9fe6-b24b52cd1070";

    @BeforeEach
    void setUp() {
        authConnector = mock(AuthConnector.class);
        userPersonalDataMapper = mock(UserPersonalDataMapper.class);

        personalDataProvider = new UserManagementService(authConnector, userPersonalDataMapper);
    }

    @Test
    void failGettingUserPersonalData() {
        // given
        when(authConnector.getUserData(USER_ID)).thenThrow(new RuntimeException("Unable to connect to the server"));

        // when
        var personalDataMono = personalDataProvider.getUserPersonalData(USER_ID);

        // then
        StepVerifier.create(personalDataMono).verifyError();
    }

    @Test
    void getPersonalData() {
        // given
        User user = new User();
        when(authConnector.getUserData(USER_ID)).thenReturn(user);
        UserPersonalData userPersonalData = new UserPersonalData();
        userPersonalData.setUserUuid(USER_ID);
        when(userPersonalDataMapper.fromUserData(user)).thenReturn(userPersonalData);

        // when
        var personalDataMono = personalDataProvider.getUserPersonalData(USER_ID);

        // then
        StepVerifier.create(personalDataMono).expectNextMatches(
                personalData -> USER_ID.equals(personalData.getUserUuid())
        ).verifyComplete();
    }
}
