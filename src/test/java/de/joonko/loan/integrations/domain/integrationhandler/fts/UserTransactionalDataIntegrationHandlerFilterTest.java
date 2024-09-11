package de.joonko.loan.integrations.domain.integrationhandler.fts;

import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.UserState;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RandomBeansExtension.class)
public class UserTransactionalDataIntegrationHandlerFilterTest {

    private UserTransactionalDataIntegrationHandlerFilter userTransactionalDataIntegrationHandlerFilter;

    @BeforeEach
    void setUp() {
        userTransactionalDataIntegrationHandlerFilter = new UserTransactionalDataIntegrationHandlerFilter();
    }

    @Test
    void test_withNullUserState_returnsFalse(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(null);

        // when
        final var result = userTransactionalDataIntegrationHandlerFilter.test(offerRequest);

        // then
        assertFalse(result);
    }

    @Test
    void test_withNullDacDataState_returnsFalse(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder().dacDataState(null).build());

        // when
        final var result = userTransactionalDataIntegrationHandlerFilter.test(offerRequest);

        // then
        assertFalse(result);
    }

    @Test
    void test_withInvalidState_returnsFalse(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder().dacDataState(DacDataState.FTS_DATA_EXISTS).build());

        // when
        final var result = userTransactionalDataIntegrationHandlerFilter.test(offerRequest);

        // then
        assertFalse(result);
    }

    @Test
    void test_withValidState_returnsTrue(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder().dacDataState(DacDataState.MISSING_OR_STALE).build());

        // when
        final var result = userTransactionalDataIntegrationHandlerFilter.test(offerRequest);

        // then
        assertTrue(result);
    }

    @Test
    void test_withOtherValidState_returnsTrue(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder().dacDataState(DacDataState.MISSING_ACCOUNT_CLASSIFICATION).build());

        // when
        final var result = userTransactionalDataIntegrationHandlerFilter.test(offerRequest);

        // then
        assertTrue(result);
    }


}
