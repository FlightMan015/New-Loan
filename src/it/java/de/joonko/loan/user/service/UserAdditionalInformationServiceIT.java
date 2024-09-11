package de.joonko.loan.user.service;

import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.user.api.model.Consent;
import de.joonko.loan.user.api.model.ConsentApiState;
import de.joonko.loan.user.api.model.ConsentApiType;
import de.joonko.loan.user.service.persistence.domain.ConsentData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static de.joonko.loan.offer.testdata.StorageTestData.*;
import static de.joonko.loan.offer.testdata.StorageTestData.getloanOfferStoreTestData;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class})
@SpringBootTest
public class UserAdditionalInformationServiceIT {

    private final static String IP = "192.168.0.104";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserAdditionalInformationService userAdditionalInformationService;

    @Autowired
    private UserAdditionalInformationRepository userAdditionalInformationRepository;

    @Test
    void save_consent_works_correctly() {
        // given
        final var userUUID = randomUUID().toString();
        final var emailConsent = Consent.builder()
                .type(ConsentApiType.EMAIL)
                .consent(ConsentApiState.ACCEPTED)
                .build();
        final var phoneConsent = Consent.builder()
                .type(ConsentApiType.PHONE)
                .consent(ConsentApiState.DECLINED)
                .build();
        final var consents = List.of(emailConsent, phoneConsent);

        // when
        final var results = userAdditionalInformationService.saveConsents(userUUID, consents, IP);

        // then
        assertEquals(2, results.size());
        assertEquals(ConsentApiState.ACCEPTED, results.stream()
                .filter(consent -> consent.getType().equals(ConsentApiType.EMAIL)).map(Consent::getConsent).findFirst().get());
        assertEquals(ConsentApiState.DECLINED, results.stream()
                .filter(consent -> consent.getType().equals(ConsentApiType.PHONE)).map(Consent::getConsent).findFirst().get());

        final var user = userAdditionalInformationRepository.findById(userUUID);
        assertEquals(Set.of(IP), user.get().getConsentData().stream().map(ConsentData::getClientIP).collect(toSet()));
    }

    @Test
    void get_consents_works_correctly() {
        // given
        final var userUUID = randomUUID().toString();
        final var emailConsent = Consent.builder()
                .type(ConsentApiType.EMAIL)
                .consent(ConsentApiState.ACCEPTED)
                .build();
        final var phoneConsent = Consent.builder()
                .type(ConsentApiType.PHONE)
                .consent(ConsentApiState.DECLINED)
                .build();
        final var consents = List.of(emailConsent, phoneConsent);

        // when
        userAdditionalInformationService.saveConsents(userUUID, consents, IP);

        final var results = userAdditionalInformationService.getUserConsents(userUUID);

        // then
        assertEquals(4, results.size());
        assertEquals(ConsentApiState.ACCEPTED, results.stream()
                .filter(consent -> consent.getType().equals(ConsentApiType.EMAIL)).map(Consent::getConsent).findFirst().get());
        assertEquals(ConsentApiState.DECLINED, results.stream()
                .filter(consent -> consent.getType().equals(ConsentApiType.PHONE)).map(Consent::getConsent).findFirst().get());
        assertEquals(Set.of(ConsentApiState.NONE), results.stream()
                .filter(consent -> !Set.of(ConsentApiType.EMAIL, ConsentApiType.PHONE).contains(consent.getType()))
                .map(Consent::getConsent)
                .collect(toSet())
        );
    }


    @Test
    void deleteUserData() {
        // given
        String userUuid = UUID.randomUUID().toString();
        Query queryFindByUserUuid = getQueryFindByUserUuid(userUuid);
        populateStorageForGetOffers(userUuid);

        // when
        var deletedMono = userAdditionalInformationService.deleteUserData(userUuid);

        // then
        assertAll(
                () -> StepVerifier.create(deletedMono).expectNextCount(0).verifyComplete(),
                () -> assertNull(mongoTemplate.findById(userUuid, UserPersonalInformationStore.class)),
                () -> assertNull(mongoTemplate.findById(userUuid, UserAdditionalInformationStore.class)),
                () -> assertNull(mongoTemplate.findById(userUuid, UserTransactionalDataStore.class)),
                () -> assertNull(mongoTemplate.findById(userUuid, UserTransactionalDraftDataStore.class)),
                () -> assertNull(mongoTemplate.findById(userUuid, UserStatesStore.class)),
                () -> assertTrue(mongoTemplate.find(queryFindByUserUuid, LoanOfferStore.class).isEmpty())
        );
    }

    @Test
    void doNotThrowExceptionWhenUserDataIsAlreadyRemoved() {
        // given
        String userUuid = UUID.randomUUID().toString();
        Query queryFindByUserUuid = getQueryFindByUserUuid(userUuid);

        // when
        userAdditionalInformationService.deleteUserData(userUuid);

        // then
        assertAll(
                () -> assertNull(mongoTemplate.findById(userUuid, UserPersonalInformationStore.class)),
                () -> assertNull(mongoTemplate.findById(userUuid, UserAdditionalInformationStore.class)),
                () -> assertNull(mongoTemplate.findById(userUuid, UserTransactionalDataStore.class)),
                () -> assertNull(mongoTemplate.findById(userUuid, UserTransactionalDraftDataStore.class)),
                () -> assertNull(mongoTemplate.findById(userUuid, UserStatesStore.class)),
                () -> assertTrue(mongoTemplate.find(queryFindByUserUuid, LoanOfferStore.class).isEmpty())
        );
    }

    private void populateStorageForGetOffers(String userUuid) {
        mongoTemplate.insert(getUserPersonalInformationStoreTestData(userUuid));
        mongoTemplate.insert(getUserAdditionalInformationStoreTestData(userUuid));
        mongoTemplate.insert(getUserTransactionalDataStoreTestData(userUuid));
        mongoTemplate.insert(getUserTransactionalDraftDataStoreTestData(userUuid));
        mongoTemplate.insert(getUserStatesStoreTestData(userUuid));
        mongoTemplate.insert(getloanOfferStoreTestData(userUuid, LoanOffer.builder().amount(4000).build()));
        mongoTemplate.insert(getloanOfferStoreTestData(userUuid, LoanOffer.builder().amount(5000).build()));
        mongoTemplate.insert(getloanOfferStoreTestData(userUuid, LoanOffer.builder().amount(6000).build()));
    }
}
