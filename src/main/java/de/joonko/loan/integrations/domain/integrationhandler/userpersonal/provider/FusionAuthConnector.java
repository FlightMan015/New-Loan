package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.FusionauthUserMapper;
import de.joonko.loan.integrations.model.DistributionChannel;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.user.service.UserPersonalInformationStore;

import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.client.FusionAuthClientException;
import io.fusionauth.domain.User;
import io.fusionauth.domain.api.UserRequest;
import io.fusionauth.domain.api.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FusionAuthConnector implements AuthConnector {

    private final FusionAuthClient fusionAuthClient;

    private final FusionauthUserMapper fusionauthUserMapper;

    private final ApiMetric apiMetric;

    private final DistributionChannelManager distributionChannelManager;

    private HttpStatus getHttpStatus(ClientResponse<UserResponse, Errors> clientResponse) {
        return clientResponse.status != 0 ? HttpStatus.valueOf(clientResponse.status) :
                HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    @Retryable(value = FusionAuthClientException.class, maxAttemptsExpression = "${fusionAuthConnector.retry.maxAttempts}", backoff = @Backoff(delayExpression = "${fusionAuthConnector.retry.maxDelay}"))
    public User getUserData(String userUUID) {
        ClientResponse<UserResponse, Errors> clientResponse = fusionAuthClient.retrieveUser(UUID.fromString(userUUID));
        User user = null;
        apiMetric.incrementStatusCounter(getHttpStatus(clientResponse), ApiComponent.FUSION_AUTH, ApiName.GET_USER_DATA);
        if (clientResponse.wasSuccessful()) {
            user = clientResponse.successResponse.user;
            if (DistributionChannel.BONIFY.equals(distributionChannelManager.extractDistributionChannel(user.tenantId.toString())) && Objects.isNull(user.data.get("uid"))) {
                throw new FusionAuthClientException("FusionAuth data is incomplete for bonify user " + userUUID + ". No bonifyUserId was found. Going to retry ");
            }
            log.debug("successfully retrieved {} from FusionAuth", userUUID);
        } else {
            handleFusionAuthError(userUUID, clientResponse);
        }
        return user;
    }

    @Async
    @Override
    public void updateUserData(UserPersonalInformationStore userPersonalInformationStore) {
        ClientResponse<UserResponse, Errors> clientResponse = fusionAuthClient.retrieveUser(UUID.fromString(userPersonalInformationStore.getUserUUID()));
        apiMetric.incrementStatusCounter(getHttpStatus(clientResponse), ApiComponent.FUSION_AUTH,
                ApiName.UPDATE_USER_DATA);

        if (clientResponse.wasSuccessful()) {
            User user = clientResponse.successResponse.user;
            user = fusionauthUserMapper.merge(user, userPersonalInformationStore);
            clientResponse = fusionAuthClient.updateUser(user.id, new UserRequest(user));
            if (clientResponse.wasSuccessful()) {
                log.debug("successfully updated {} details to FusionAuth", userPersonalInformationStore.getUserUUID());
            } else {
                log.error("Failed to update {} details to Fusionauth with error: {}", userPersonalInformationStore.getUserUUID(), clientResponse.errorResponse);
            }
        }
    }

    private void handleFusionAuthError(String userUUID, ClientResponse<UserResponse, Errors> clientResponse) {
        String cause;
        if (clientResponse.errorResponse != null) {
            cause = clientResponse.errorResponse.toString();
        } else if (clientResponse.exception != null) {
            cause = clientResponse.exception.getClass().toString();
        } else {
            cause = String.valueOf(clientResponse.status);
        }
        throw new FusionAuthClientException("unable to retrieve user data for: " + userUUID + " from FusionAuth caused by: " + cause);
    }

}
