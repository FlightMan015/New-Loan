package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.integrations.domain.integrationhandler.loandemand.LoanDemandRequestBuilder;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.model.OfferResponseState;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
class PersonalDetailsResponseEnhancerTest {

    @InjectMocks
    @Resource
    private PersonalDetailsResponseEnhancer personalDetailsResponseEnhancer;

    @Mock
    private LoanDemandRequestBuilder loanDemandRequestBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void buildResponseData_buildResponseCorrectly(@Random OfferRequest offerRequest) {
        // given
        final var loanDemandRequest = LoanDemandRequest.builder().build();
        // when
        when(loanDemandRequestBuilder.build(offerRequest)).thenReturn(Mono.just(loanDemandRequest));

        final var result = personalDetailsResponseEnhancer.buildResponseData(offerRequest);

        StepVerifier.create(result).consumeNextWith(response -> assertAll(
                () -> assertEquals(OfferResponseState.MISSING_PERSONAL_DATA, response.getState()),
                () -> assertEquals(loanDemandRequest, response.getData())
        )).verifyComplete();
    }

    // TODO: impl what should be returned when builder throws error

    // TODO: impl what should be returned when builder return empty mono
}
