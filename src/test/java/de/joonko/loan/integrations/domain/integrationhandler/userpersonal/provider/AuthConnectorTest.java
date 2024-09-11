package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.FusionauthUserMapper;
import de.joonko.loan.integrations.model.DistributionChannel;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.user.service.UserPersonalInformationStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import io.fusionauth.domain.api.UserRequest;
import io.fusionauth.domain.api.UserResponse;

import static de.joonko.loan.integrations.model.DistributionChannel.BONIFY;
import static de.joonko.loan.integrations.model.DistributionChannel.OTHERS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AuthConnectorTest {

    private AuthConnector authConnector;

    private FusionAuthClient fusionAuthClient;

    private FusionauthUserMapper mockMapper;

    private DistributionChannelManager distributionChannelManager;

    private static ApiMetric apiMetric;


    @BeforeEach
    void beforeAll() {
        fusionAuthClient = mock(FusionAuthClient.class);
        mockMapper = mock(FusionauthUserMapper.class);
        apiMetric = mock(ApiMetric.class);
        distributionChannelManager = mock(DistributionChannelManager.class);
        authConnector = new FusionAuthConnector(fusionAuthClient, mockMapper, apiMetric, distributionChannelManager);
    }

    @ValueSource(ints = {404, 401, 500})
    @ParameterizedTest
    void throw_exception_while_getting_user_data_when_received_unsuccessful_status(int status) {
        // given
        UUID userUuid = UUID.randomUUID();
        ClientResponse<UserResponse, Errors> clientResponse = new ClientResponse<>();
        clientResponse.status = status;
        when(fusionAuthClient.retrieveUser(userUuid)).thenReturn(clientResponse);

        // when
        Executable response = () -> authConnector.getUserData(userUuid.toString());

        // then
        assertAll(
                () -> assertThrows(RuntimeException.class, response),
                () -> verify(apiMetric).incrementStatusCounter(
                        HttpStatus.valueOf(status), ApiComponent.FUSION_AUTH, ApiName.GET_USER_DATA),
                () -> verify(fusionAuthClient).retrieveUser(userUuid)
        );
    }

    @Test
    void throw_exception_while_getting_user_data_when_received_error_response() {
        // given
        UUID userUuid = UUID.randomUUID();
        ClientResponse<UserResponse, Errors> clientResponse = new ClientResponse<>();
        clientResponse.errorResponse = new Errors();
        when(fusionAuthClient.retrieveUser(userUuid)).thenReturn(clientResponse);

        // when
        Executable response = () -> authConnector.getUserData(userUuid.toString());

        // then
        assertAll(
                () -> assertThrows(RuntimeException.class, response),
                () -> verify(apiMetric).incrementStatusCounter(
                        HttpStatus.INTERNAL_SERVER_ERROR, ApiComponent.FUSION_AUTH, ApiName.GET_USER_DATA),
                () -> verify(fusionAuthClient).retrieveUser(userUuid)
        );
    }

    @Test
    void throw_exception_while_getting_user_data_when_received_exception() {
        // given
        UUID userUuid = UUID.randomUUID();
        ClientResponse<UserResponse, Errors> clientResponse = new ClientResponse<>();
        clientResponse.exception = new UnknownHostException();
        when(fusionAuthClient.retrieveUser(userUuid)).thenReturn(clientResponse);

        // when
        Executable response = () -> authConnector.getUserData(userUuid.toString());

        // then
        assertAll(
                () -> assertThrows(RuntimeException.class, response),
                () -> verify(apiMetric).incrementStatusCounter(
                        HttpStatus.INTERNAL_SERVER_ERROR, ApiComponent.FUSION_AUTH, ApiName.GET_USER_DATA),
                () -> verify(fusionAuthClient).retrieveUser(userUuid)
        );
    }

    @Test
    void get_user_data_success_case() {
        // given
        UUID userUuid = UUID.randomUUID();
        ClientResponse<UserResponse, Errors> clientResponse = getSuccessfulResponseForRetrievingUserDataWithBonifyUserId(userUuid, true);
        when(fusionAuthClient.retrieveUser(userUuid)).thenReturn(clientResponse);
        when(distributionChannelManager.extractDistributionChannel(clientResponse.successResponse.user.tenantId.toString())).thenReturn(BONIFY);

        // when
        User user = authConnector.getUserData(userUuid.toString());

        // then
        assertAll(
                () -> verify(fusionAuthClient).retrieveUser(userUuid),
                () -> verify(distributionChannelManager).extractDistributionChannel(clientResponse.successResponse.user.tenantId.toString()),
                () -> verify(apiMetric).incrementStatusCounter(
                        HttpStatus.OK, ApiComponent.FUSION_AUTH, ApiName.GET_USER_DATA),
                () -> assertNotNull(user)
        );
    }

    @Test
    void get_user_data_without_bonify_id_case() {
        // given
        UUID userUuid = UUID.randomUUID();
        ClientResponse<UserResponse, Errors> clientResponse = getSuccessfulResponseForRetrievingUserDataWithoutBonifyUserId(userUuid);
        when(fusionAuthClient.retrieveUser(userUuid)).thenReturn(clientResponse);
        when(distributionChannelManager.extractDistributionChannel(clientResponse.successResponse.user.tenantId.toString())).thenReturn(BONIFY);

        // when
        Executable response = () -> authConnector.getUserData(userUuid.toString());

        // then
        assertAll(
                () -> assertThrows(RuntimeException.class, response),
                () -> verify(distributionChannelManager).extractDistributionChannel(clientResponse.successResponse.user.tenantId.toString()),
                () -> verify(apiMetric).incrementStatusCounter(
                        HttpStatus.OK, ApiComponent.FUSION_AUTH, ApiName.GET_USER_DATA)
        );
    }

    @Test
    void get_non_bonify_user_data() {
        // given
        UUID userUuid = UUID.randomUUID();
        ClientResponse<UserResponse, Errors> clientResponse = getSuccessfulResponseForRetrievingUserDataWithBonifyUserId(userUuid, false);
        when(fusionAuthClient.retrieveUser(userUuid)).thenReturn(clientResponse);
        when(distributionChannelManager.extractDistributionChannel(clientResponse.successResponse.user.tenantId.toString())).thenReturn(OTHERS);
        // when
        User user = authConnector.getUserData(userUuid.toString());

        // then
        assertAll(
                () -> verify(fusionAuthClient).retrieveUser(userUuid),
                () -> verify(distributionChannelManager).extractDistributionChannel(clientResponse.successResponse.user.tenantId.toString()),
                () -> verify(apiMetric).incrementStatusCounter(HttpStatus.OK, ApiComponent.FUSION_AUTH, ApiName.GET_USER_DATA),
                () -> assertNotNull(user)
        );
    }

    @Test
    void update_user_data() {
        // given
        UUID userUuid = UUID.randomUUID();
        UserPersonalInformationStore userPersonalInformationStore = getUserPersonalInformationStore(userUuid.toString());
        ClientResponse<UserResponse, Errors> clientResponse = getSuccessfulResponseForRetrievingUserDataWithBonifyUserId(userUuid, true);
        when(fusionAuthClient.retrieveUser(userUuid)).thenReturn(clientResponse);
        when(mockMapper.merge(any(User.class), any(UserPersonalInformationStore.class))).thenReturn(getMergedUser(userUuid));
        when(fusionAuthClient.updateUser(eq(userUuid), any(UserRequest.class))).thenReturn(clientResponse);
        ArgumentCaptor<UserRequest> captor = ArgumentCaptor.forClass(UserRequest.class);

        // when
        authConnector.updateUserData(userPersonalInformationStore);

        // then
        verify(fusionAuthClient).updateUser(eq(userUuid), captor.capture());
        User actual = captor.getValue().user;
        assertAll(
                () -> verify(fusionAuthClient).retrieveUser(userUuid),
                () -> assertNotNull(actual),
                () -> assertEquals("test", actual.firstName),
                () -> assertEquals(LocalDate.of(1950, 10, 11), actual.birthDate),
                () -> assertEquals("test@email.com", actual.email),
                () -> assertEquals("Berlin", actual.data.get("addressCity")),
                () -> verify(apiMetric).incrementStatusCounter(
                        HttpStatus.OK, ApiComponent.FUSION_AUTH, ApiName.UPDATE_USER_DATA)

        );
    }

    @Test
    void do_nothing_while_updating_user_data_when_received_not_successful() {
        // given
        UUID userUuid = UUID.randomUUID();
        UserPersonalInformationStore userPersonalInformationStore = getUserPersonalInformationStore(userUuid.toString());
        ClientResponse clientResponse = new ClientResponse<>();
        clientResponse.status = 500;
        when(fusionAuthClient.retrieveUser(userUuid)).thenReturn(clientResponse);

        // when
        authConnector.updateUserData(userPersonalInformationStore);

        // then
        assertAll(
                () -> verify(fusionAuthClient).retrieveUser(userUuid),
                () -> verifyNoMoreInteractions(fusionAuthClient),
                () -> verify(apiMetric).incrementStatusCounter(
                        HttpStatus.INTERNAL_SERVER_ERROR, ApiComponent.FUSION_AUTH, ApiName.UPDATE_USER_DATA)
        );
    }

    private UserPersonalInformationStore getUserPersonalInformationStore(String userUuid) {
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID(userUuid);
        userPersonalInformationStore.setAddressCity("Berlin");
        userPersonalInformationStore.setFirstName("test");
        userPersonalInformationStore.setLastName("name");
        userPersonalInformationStore.setBirthDate(LocalDate.of(1950, 10, 11));
        return userPersonalInformationStore;
    }

    private ClientResponse<UserResponse, Errors> getSuccessfulResponseForRetrievingUserDataWithBonifyUserId(UUID userUuid, boolean isBonify) {
        User user = isBonify ? getTestUser(userUuid) : getNonBonifyTestUser(userUuid);
        UserResponse userResponse = new UserResponse(user);
        ClientResponse<UserResponse, Errors> clientResponse = new ClientResponse<>();
        clientResponse.successResponse = userResponse;
        clientResponse.status = 200;

        return clientResponse;
    }

    private ClientResponse<UserResponse, Errors> getSuccessfulResponseForRetrievingUserDataWithoutBonifyUserId(UUID userUuid) {
        User user = getBonifyUserWithoutId(userUuid);
        UserResponse userResponse = new UserResponse(user);
        ClientResponse<UserResponse, Errors> clientResponse = new ClientResponse<>();
        clientResponse.successResponse = userResponse;
        clientResponse.status = 200;

        return clientResponse;
    }


    private User getTestUser(UUID uuid) {
        User user = new User();
        user.id = uuid;
        user.email = "test@email.com";
        user.tenantId = UUID.fromString("788cda86-e565-47a6-a9fc-5abefdb52ae9");
        user.data = Map.of("uid", "1234");
        return user;
    }

    private User getBonifyUserWithoutId(UUID uuid) {
        User user = new User();
        user.id = uuid;
        user.email = "test@email.com";
        user.tenantId = UUID.fromString("788cda86-e565-47a6-a9fc-5abefdb52ae9");
        user.data = Map.of();
        return user;
    }

    private User getNonBonifyTestUser(UUID uuid) {
        User user = new User();
        user.id = uuid;
        user.email = "test@email.com";
        user.tenantId = UUID.fromString("888cda86-e565-47a6-a9fc-5abefdb52ae0");
        return user;
    }

    private User getMergedUser(UUID uuid) {
        User user = new User();
        user.id = uuid;
        user.email = "test@email.com";
        user.firstName = "test";
        user.lastName = "name";
        user.birthDate = LocalDate.of(1950, 10, 11);
        user.data = Map.of("addressCity", "Berlin");
        return user;
    }
}
