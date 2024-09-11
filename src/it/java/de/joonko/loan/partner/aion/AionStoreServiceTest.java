package de.joonko.loan.partner.aion;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.offer.domain.BestLoanOffer;
import de.joonko.loan.offer.domain.OfferCategory;
import de.joonko.loan.partner.aion.model.AionResponseValueType;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RandomBeansExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
class AionStoreServiceTest {


    @Autowired
    private AionStoreService aionStoreService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void saveCreditApplicationResponse() {
        // given
        final String applicationId = randomUUID().toString();
        final var creditApplicationResponseStore = CreditApplicationResponseStore.builder()
                .id("1")
                .applicationId(applicationId)
                .processId("2")
                .variables(List.of(
                        CreditApplicationResponseStore.Variable.builder().name(AionResponseValueType.DECISION).value("POSITIVE").build(),
                        CreditApplicationResponseStore.Variable.builder().name(AionResponseValueType.ERROR_MESSAGE).value("NONE").build()
                ))
                .build();

        // when
        final var result = aionStoreService.saveCreditApplicationResponse(creditApplicationResponseStore);

        // then
        StepVerifier.create(result)
                .consumeNextWith(response -> assertAll(
                        () -> assertEquals(applicationId, response.getApplicationId()),
                        () -> assertEquals(creditApplicationResponseStore.getProcessId(), response.getProcessId()),
                        () -> assertEquals(creditApplicationResponseStore.getVariables(), response.getVariables())
                ))
                .verifyComplete();
    }

    @Test
    void saveCreditApplicationResponse_secondTime_willUpdateExisting() {
        // given
        final String applicationId = randomUUID().toString();
        final var creditApplicationResponseStore = CreditApplicationResponseStore.builder()
                .id("2")
                .applicationId(applicationId)
                .processId("2")
                .variables(List.of(
                        CreditApplicationResponseStore.Variable.builder().name(AionResponseValueType.DECISION).value("POSITIVE").build(),
                        CreditApplicationResponseStore.Variable.builder().name(AionResponseValueType.ERROR_MESSAGE).value("NONE").build()
                ))
                .build();
        final var creditApplicationResponseStoreForUpdate = CreditApplicationResponseStore.builder()
                .id("2")
                .applicationId(applicationId)
                .processId("2")
                .variables(List.of(
                        CreditApplicationResponseStore.Variable.builder().name(AionResponseValueType.REASON).value("SOMETHING").build()
                ))
                .build();

        // when
        final var result = aionStoreService.saveCreditApplicationResponse(creditApplicationResponseStore)
                .flatMap(response -> aionStoreService.saveCreditApplicationResponse(creditApplicationResponseStoreForUpdate));

        final var finalVariables = Stream.concat(
                        creditApplicationResponseStore.getVariables().stream(), creditApplicationResponseStoreForUpdate.getVariables().stream())
                .collect(toList());
        // then
        StepVerifier.create(result)
                .consumeNextWith(response -> assertAll(
                        () -> assertEquals(applicationId, response.getApplicationId()),
                        () -> assertEquals(creditApplicationResponseStore.getProcessId(), response.getProcessId()),
                        () -> assertEquals(finalVariables, response.getVariables())
                ))
                .verifyComplete();
    }

    @Test
    void findByProcessId() {
        // given
        final var processId = "6c5cffa3-845d-4b1e-91c0-f12375132d2e";
        mongoTemplate.insertAll(List.of(
                CreditApplicationResponseStore.builder().processId("6c5cffa3-845d-4b1e-91c0-f12375132d2e").build()
        ));

        // when
        var foundMono = aionStoreService.findByProcessId(processId);

        // then
        StepVerifier.create(foundMono).expectNextCount(1).verifyComplete();
    }

    @Test
    void getEmptyWhenNotFoundByProcessId() {
        // given
        final var processId = "9cba248a-ac03-434a-880f-50bcc32d7e8a";

        // when
        var foundMono = aionStoreService.findByProcessId(processId);

        // then
        StepVerifier.create(foundMono).expectNextCount(0).verifyComplete();
    }

    @Test
    void findByApplicationId() {
        // given
        final String applicationId = randomUUID().toString();
        final var creditApplicationResponseStore = CreditApplicationResponseStore.builder()
                .id("3")
                .applicationId(applicationId)
                .processId("2")
                .variables(List.of(
                        CreditApplicationResponseStore.Variable.builder().name(AionResponseValueType.DECISION).value("POSITIVE").build(),
                        CreditApplicationResponseStore.Variable.builder().name(AionResponseValueType.ERROR_MESSAGE).value("NONE").build()
                ))
                .build();

        // when
        final var result = aionStoreService.saveCreditApplicationResponse(creditApplicationResponseStore)
                .flatMap(response -> aionStoreService.findByApplicationId(applicationId));

        // then
        StepVerifier.create(result)
                .consumeNextWith(response -> assertAll(
                        () -> assertThat(response).isNotEmpty(),
                        () -> assertEquals(applicationId, response.get().getApplicationId()),
                        () -> assertEquals(creditApplicationResponseStore.getProcessId(), response.get().getProcessId()),
                        () -> assertEquals(creditApplicationResponseStore.getVariables(), response.get().getVariables())
                ))
                .verifyComplete();
    }


    @Test
    void addBestOffers() {
        // given
        final String applicationId = randomUUID().toString();
        final var creditApplicationResponseStore = CreditApplicationResponseStore.builder()
                .id("4")
                .applicationId(applicationId)
                .processId("2")
                .variables(List.of(
                        CreditApplicationResponseStore.Variable.builder().name(AionResponseValueType.DECISION).value("POSITIVE").build(),
                        CreditApplicationResponseStore.Variable.builder().name(AionResponseValueType.ERROR_MESSAGE).value("NONE").build()
                ))
                .build();

        final var bestOffers = List.of(
                BestLoanOffer.builder().offerId(randomUUID().toString()).offerCategory(OfferCategory.APR).build(),
                BestLoanOffer.builder().offerId(randomUUID().toString()).offerCategory(OfferCategory.MONTHLY_INSTALLMENT_AMOUNT).build(),
                BestLoanOffer.builder().offerId(randomUUID().toString()).offerCategory(OfferCategory.TOTAL_REPAYMENT_AMOUNT).build());
        // when
        final var result = aionStoreService.saveCreditApplicationResponse(creditApplicationResponseStore)
                .flatMap(response -> aionStoreService.addBestOffers(applicationId, bestOffers));

        // then
        StepVerifier.create(result)
                .consumeNextWith(response -> assertAll(
                        () -> assertEquals(applicationId, response.getApplicationId()),
                        () -> assertEquals(creditApplicationResponseStore.getProcessId(), response.getProcessId()),
                        () -> assertEquals(creditApplicationResponseStore.getVariables(), response.getVariables()),
                        () -> assertThat(response.getOffersToBeat()).isEqualToComparingFieldByField(bestOffers)
                ))
                .verifyComplete();
    }


}