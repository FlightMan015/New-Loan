package de.joonko.loan.user.service;

import de.joonko.loan.user.service.UserAdditionalInfoService;
import de.joonko.loan.user.service.UserAdditionalInfoServiceImpl;
import de.joonko.loan.user.service.UserAdditionalInformationRepository;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserAdditionalInfoServiceTest {

    private UserAdditionalInfoService userAdditionalInfoService;

    private UserAdditionalInformationRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(UserAdditionalInformationRepository.class);

        userAdditionalInfoService = new UserAdditionalInfoServiceImpl(repository);
    }

    @Test
    void getEmptyWhenDoesNotExists() {
        // given
        String userId = "123";
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // when
        var additionalInfo = userAdditionalInfoService.findById(userId);

        // then
        StepVerifier.create(additionalInfo)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findById() {
        // given
        String userId = "123";
        when(repository.findById(userId)).thenReturn(Optional.of(new UserAdditionalInformationStore()));

        // when
        var additionalInfo = userAdditionalInfoService.findById(userId);

        // then
        StepVerifier.create(additionalInfo)
                .expectNextCount(1)
                .verifyComplete();
    }
}
