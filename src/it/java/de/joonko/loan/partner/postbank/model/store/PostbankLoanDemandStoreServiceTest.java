package de.joonko.loan.partner.postbank.model.store;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.webhooks.postbank.model.ContractState;
import de.joonko.loan.webhooks.postbank.model.CreditResult;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.test.StepVerifier;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
class PostbankLoanDemandStoreServiceTest {


    @Autowired
    private PostbankLoanDemandStoreService postbankLoanDemandStoreService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void save(@Random PostbankLoanDemandStore postbankLoanDemandStore) {
        // when
        final var saved = postbankLoanDemandStoreService.save(postbankLoanDemandStore);

        // then
        StepVerifier.create(saved)
                .consumeNextWith(response -> assertAll(
                        () -> assertEquals(postbankLoanDemandStore.getApplicationId(), response.getApplicationId()),
                        () -> assertEquals(postbankLoanDemandStore.getContractNumber(), response.getContractNumber()),
                        () -> assertEquals(postbankLoanDemandStore.getCreditResults(), response.getCreditResults())
                ))
                .verifyComplete();
    }

    @Test
    void findByApplicationId() {
        // given
        final var applicationId = "3";
        mongoTemplate.insertAll(List.of(
                PostbankLoanDemandStore.builder().applicationId("1").build(),
                PostbankLoanDemandStore.builder().applicationId("2").build(),
                PostbankLoanDemandStore.builder().applicationId("3").build(),
                PostbankLoanDemandStore.builder().applicationId("4").build()
        ));

        // when
        var foundMono = postbankLoanDemandStoreService.findByApplicationId(applicationId);

        // then
        StepVerifier.create(foundMono)
                .consumeNextWith(response -> assertAll(
                        () -> assertEquals(applicationId, response.getApplicationId())
                ))
                .verifyComplete();
    }

    @Test
    void addOffersResponse() {
        // given
        final var applicationId = "3";

        final var postbankLoanDemandStore = PostbankLoanDemandStore.builder()
                .applicationId(applicationId)
                .contractNumber("123")
                .creditResults(Set.of(CreditResult.builder().contractState(ContractState.IM_SYSTEM_GESPEICHERT_10).build()))
                .build();
        mongoTemplate.insertAll(List.of(
                PostbankLoanDemandStore.builder().applicationId("1").build(),
                PostbankLoanDemandStore.builder().applicationId("2").build(),
                postbankLoanDemandStore,
                PostbankLoanDemandStore.builder().applicationId("4").build()
        ));
        final var creditResult1 = CreditResult.builder().contractState(ContractState.ONLINE_GENEHMIGT_24).build();
        final var creditResult2 = CreditResult.builder().contractState(ContractState.UNTERLAGEN_EINGEGANGEN_25).build();

        // when
        var foundMono = postbankLoanDemandStoreService
                .addOffersResponse(applicationId, creditResult1)
                .flatMap(result -> postbankLoanDemandStoreService.addOffersResponse(applicationId, creditResult2));

        // then
        StepVerifier.create(foundMono)
                .consumeNextWith(response -> assertAll(
                        () -> assertEquals(applicationId, response.getApplicationId()),
                        () -> assertEquals(postbankLoanDemandStore.getContractNumber(), response.getContractNumber()),
                        () -> assertEquals(3, response.getCreditResults().size()),
                        () -> assertEquals(EnumSet.of(ContractState.IM_SYSTEM_GESPEICHERT_10, ContractState.ONLINE_GENEHMIGT_24, ContractState.UNTERLAGEN_EINGEGANGEN_25), response.getCreditResults().stream().map(CreditResult::getContractState).collect(toSet()))
                ))
                .verifyComplete();
    }
}