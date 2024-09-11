package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalDataMerger;
import de.joonko.loan.offer.api.ContactData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserPersonalDataAggregatorTest {

    private UserPersonalDataAggregator personalDataAggregator;

    private static final String USER_ID = "2f20a660-f0f2-4ca5-9fe6-b24b52cd1070";

    private UserPersonalDataProvider fusionAuthUserPersonalDataProvider;
    private UserPersonalDataProvider segmentUserPersonalDataProvider;
    private UserPersonalDataMerger userPersonalDataMerger;

    @BeforeEach
    void setUp() {
        fusionAuthUserPersonalDataProvider = mock(UserManagementService.class);
        segmentUserPersonalDataProvider = mock(SegmentService.class);
        userPersonalDataMerger = new UserPersonalDataMerger();

        personalDataAggregator = new UserPersonalDataAggregatorImpl(fusionAuthUserPersonalDataProvider, segmentUserPersonalDataProvider, userPersonalDataMerger);
    }

    @Test
    void throwErrorWhenFailedGettingDataFromFirstProvider() {
        // given
        when(fusionAuthUserPersonalDataProvider.getUserPersonalData(USER_ID)).thenReturn(Mono.error(new RuntimeException("Failed getting data")));

        // when
        var aggregation = personalDataAggregator.getUserPersonalData(USER_ID);

        // then
        assertAll(
                () -> StepVerifier.create(aggregation).verifyError(),
                () -> verifyNoInteractions(segmentUserPersonalDataProvider)
        );
    }

    @Test
    void returnEmptyObjectWhenFailedGettingDataFromSecondProvider() {
        // given
        UserPersonalData userPersonalData = new UserPersonalData();
        userPersonalData.setContactData(ContactData.builder()
                .email("test@test")
                .build());
        userPersonalData.setBonifyUserId(123L);
        userPersonalData.setVerifiedViaBankAccount(true);
        when(fusionAuthUserPersonalDataProvider.getUserPersonalData(USER_ID)).thenReturn(Mono.just(userPersonalData));
        when(segmentUserPersonalDataProvider.getUserPersonalData(anyString())).thenReturn(Mono.error(new RuntimeException("Failed getting data")));

        // when
        var aggregation = personalDataAggregator.getUserPersonalData(USER_ID);

        // then
        StepVerifier.create(aggregation)
                .consumeNextWith(userPersonal ->
                        assertAll(
                                () -> assertEquals(userPersonalData.getContactData().getEmail(), userPersonal.getContactData().getEmail()),
                                () -> assertEquals(userPersonalData.getBonifyUserId(), userPersonal.getBonifyUserId()),
                                () -> assertEquals(userPersonalData.getVerifiedViaBankAccount(), userPersonal.getVerifiedViaBankAccount())
                        )
                ).verifyComplete();
    }

    @Test
    void mergeUserPersonalDataFromProviders() {
        // given
        UserPersonalData personalData = new UserPersonalData();
        personalData.setContactData(ContactData.builder()
                .email("test@test")
                .build());
        personalData.setUserUuid(USER_ID);
        when(fusionAuthUserPersonalDataProvider.getUserPersonalData(USER_ID)).thenReturn(Mono.just(personalData));
        when(segmentUserPersonalDataProvider.getUserPersonalData(anyString())).thenReturn(Mono.just(personalData));

        // when
        var aggregation = personalDataAggregator.getUserPersonalData(USER_ID);

        // then
        assertAll(
                () -> StepVerifier.create(aggregation)
                        .consumeNextWith(userPersonalData ->
                                assertEquals(USER_ID, userPersonalData.getUserUuid()))
                        .verifyComplete(),
                () -> verify(fusionAuthUserPersonalDataProvider).getUserPersonalData(USER_ID),
                () -> verify(segmentUserPersonalDataProvider).getUserPersonalData(anyString())
        );
    }
}
