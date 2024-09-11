package de.joonko.loan.integrations.domain;

import de.joonko.loan.offer.OfferDemandRequest;
import de.joonko.loan.user.states.UserStatesStore;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration")
@SpringBootTest
@ExtendWith(RandomBeansExtension.class)
class UserStateReducerIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserStateReducer userStateReducer;

    @Test
    void updateRequestedLoanAmountForExistingUser(@Random OfferDemandRequest offerDemandRequest) {
        // given
        offerDemandRequest.setInetAddress(Optional.empty());
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(offerDemandRequest.getUserUUID());
        userStatesStore.setLastRequestedLoanAmount(3000);
        mongoTemplate.insert(userStatesStore);

        // when
        var actualOfferReq = userStateReducer.deriveUserState(offerDemandRequest);

        // then
        assertAll(
                () -> StepVerifier.create(actualOfferReq).expectNextCount(1).verifyComplete(),
                () -> assertEquals(offerDemandRequest.getRequestedLoanAmount(), mongoTemplate.findById(offerDemandRequest.getUserUUID(), UserStatesStore.class).getLastRequestedLoanAmount())
        );
    }

    @Test
    void createNewUser(@Random OfferDemandRequest offerDemandRequest) {
        // when
        offerDemandRequest.setInetAddress(Optional.empty());
        var actualOfferReq = userStateReducer.deriveUserState(offerDemandRequest);

        // then
        assertAll(
                () -> StepVerifier.create(actualOfferReq).expectNextCount(1).verifyComplete(),
                () -> assertEquals(offerDemandRequest.getRequestedLoanAmount(), mongoTemplate.findById(offerDemandRequest.getUserUUID(), UserStatesStore.class).getLastRequestedLoanAmount())
        );
    }
}
