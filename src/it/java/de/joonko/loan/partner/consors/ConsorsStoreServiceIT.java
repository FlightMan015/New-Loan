package de.joonko.loan.partner.consors;

import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.Link;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class})
@SpringBootTest
class ConsorsStoreServiceIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ConsorsStoreService consorsStoreService;

    @Test
    void deleteByApplicationId() {
        // given
        final var applicationId = "delete123";
        mongoTemplate.insertAll(getTestData());

        // when
        var deletedDocumentsMono = consorsStoreService.deletePersonalizedCalculationByApplicationId(applicationId);

        // then
        assertAll(
                () -> StepVerifier.create(deletedDocumentsMono).expectNextCount(2).verifyComplete(),
                () -> assertTrue(findByApplicationId(applicationId).isEmpty()),
                () -> assertEquals(1, findByApplicationId("ignore124").size())
        );
    }

    @Test
    void getKYCLinkForApplicationId_noLinkAvailable() {
        final List<ConsorsAcceptedOfferStore> consorsAcceptedOfferStores = List.of(
                ConsorsAcceptedOfferStore.builder()
                        .loanApplicationId("1")
                        .consorsAcceptOfferResponse(ConsorsAcceptOfferResponse.builder()
                                .links(List.of(Link.builder()
                                        .href("a")
                                        .rel("a")
                                        .build()))
                                .build())
                        .build(),
                ConsorsAcceptedOfferStore.builder()
                        .loanApplicationId("2")
                        .consorsAcceptOfferResponse(ConsorsAcceptOfferResponse.builder()
                                .links(List.of(Link.builder()
                                        .href("a")
                                        .rel("_onlineIdent")
                                        .build()))
                                .build())
                        .build()
        );

        mongoTemplate.insertAll(consorsAcceptedOfferStores);

        Mono<String> result = consorsStoreService.getKYCLinkForApplicationId("1");

        StepVerifier.create(result).verifyError();
    }

    @Test
    void getKYCLinkForApplicationId_linkAvailable() {
        final List<ConsorsAcceptedOfferStore> consorsAcceptedOfferStores = List.of(
                ConsorsAcceptedOfferStore.builder()
                        .loanApplicationId("3")
                        .consorsAcceptOfferResponse(ConsorsAcceptOfferResponse.builder()
                                .links(List.of(Link.builder()
                                        .href("a")
                                        .rel("_onlineIdent")
                                        .build()))
                                .build())
                        .build(),
                ConsorsAcceptedOfferStore.builder()
                        .loanApplicationId("4")
                        .consorsAcceptOfferResponse(ConsorsAcceptOfferResponse.builder()
                                .links(List.of(Link.builder()
                                        .href("a")
                                        .rel("_onlineIdent")
                                        .build()))
                                .build())
                        .build()
        );

        mongoTemplate.insertAll(consorsAcceptedOfferStores);

        Mono<String> result = consorsStoreService.getKYCLinkForApplicationId("3");

        assertAll(
                () -> StepVerifier.create(result).expectNextMatches(link -> link.equals("a")).verifyComplete()
        );
    }

    private List<PersonalizedCalculationsStore> findByApplicationId(String applicationId) {
        var findQuery = new Query(Criteria.where("applicationId").is(applicationId));
        return mongoTemplate.find(findQuery, PersonalizedCalculationsStore.class);
    }

    private List<PersonalizedCalculationsStore> getTestData() {
        return List.of(
                PersonalizedCalculationsStore.builder().applicationId("ignore124").build(),
                PersonalizedCalculationsStore.builder().applicationId("delete123").build(),
                PersonalizedCalculationsStore.builder().applicationId("delete123").build()
        );
    }
}
