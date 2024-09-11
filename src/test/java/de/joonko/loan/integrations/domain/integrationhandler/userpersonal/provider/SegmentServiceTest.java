package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.integrations.segment.CustomerData;
import de.joonko.loan.integrations.segment.SegmentClient;
import de.joonko.loan.util.HttpUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SegmentServiceTest {

    private SegmentService segmentService;

    private SegmentClient segmentClient;
    private UserPersonalDataMapper userPersonalDataMapper;

    @BeforeEach
    void setUp() {
        segmentClient = mock(SegmentClient.class);
        userPersonalDataMapper = mock(UserPersonalDataMapper.class);

        segmentService = new SegmentService(segmentClient, userPersonalDataMapper);
    }

    @Test
    void getUserPersonalData() {
        // given
        String email = "test@test.com";
        when(segmentClient.getUserTraits("email:" + HttpUtil.encodeValue(email))).thenReturn(Mono.just(new CustomerData()));
        when(userPersonalDataMapper.fromCustomerData(any(CustomerData.class))).thenReturn(new UserPersonalData());

        // when
        var monoUserPersonalData = segmentService.getUserPersonalData(email);

        // then
        StepVerifier.create(monoUserPersonalData).expectNextCount(1).verifyComplete();
    }

    @Test
    void throwErrorWhenGetting500FromClient() {
        String email = "test@test.com";
        when(segmentClient.getUserTraits("email:" + HttpUtil.encodeValue(email))).thenReturn(Mono.error(WebClientResponseException.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null, null, null)));

        // when
        var monoUserPersonalData = segmentService.getUserPersonalData(email);

        // then
        StepVerifier.create(monoUserPersonalData).verifyError();
    }

    @Test
    void getEmptyUserPersonalDataWhenGetting404FromClient() {
        String email = "test@test.com";
        when(segmentClient.getUserTraits("email:" + HttpUtil.encodeValue(email))).thenReturn(Mono.error(WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found exception", null, null, null)));
        when(userPersonalDataMapper.fromCustomerData(any(CustomerData.class))).thenReturn(new UserPersonalData());

        // when
        var monoUserPersonalData = segmentService.getUserPersonalData(email);

        // then
        StepVerifier.create(monoUserPersonalData).expectNextCount(1).verifyComplete();
    }
}
