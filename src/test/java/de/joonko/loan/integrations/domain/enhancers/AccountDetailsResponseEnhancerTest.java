package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.UserState;
import de.joonko.loan.offer.api.model.CustomErrorMessageKey;

import de.joonko.loan.offer.api.model.OfferResponseState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtension.class)
class AccountDetailsResponseEnhancerTest {

    private AccountDetailsResponseEnhancer accountDetailsResponseEnhancer = new AccountDetailsResponseEnhancer();

    @Test
    void buildResponseData_whenNoAccountAdded(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder().dacDataState(DacDataState.NO_ACCOUNT_ADDED).build());

        // when
        final var result = accountDetailsResponseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response -> assertAll(
                () -> assertEquals(OfferResponseState.MISSING_SALARY_ACCOUNT, response.getState()),
                () -> assertNull(response.getData())
        )).verifyComplete();
    }

    @Test
    void buildResponseData_whenNonSalaryAccountAdded(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder().dacDataState(DacDataState.MISSING_SALARY_ACCOUNT).build());

        // when
        final var result = accountDetailsResponseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response -> assertAll(
                () -> assertEquals(OfferResponseState.MISSING_SALARY_ACCOUNT, response.getState()),
                () -> assertEquals(CustomErrorMessageKey.NON_SALARY_ACCOUNT_ADDED, response.getData().getMessageKey())
        )).verifyComplete();
    }
}
