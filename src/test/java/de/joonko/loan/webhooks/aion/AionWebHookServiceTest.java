package de.joonko.loan.webhooks.aion;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.ResourceNotFoundException;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.offer.api.model.FundingPurpose;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.webhooks.aion.model.AionWebhookType;
import de.joonko.loan.webhooks.aion.repositories.AionWebhookStore;
import de.joonko.loan.webhooks.aion.testdata.AionWebHookServiceTestData;

import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import reactor.test.StepVerifier;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("integration")
@SpringBootTest
class AionWebHookServiceTest {

    @Autowired
    private AionWebHookService aionWebHookService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private AionWebHookServiceTestData testData;

    @BeforeEach
    void setUp() {
        testData = new AionWebHookServiceTestData();
    }

    @Test
    void ignoreWebHookWithSoftCollectionType() {
        // given
        final var request = testData.getRequestWithSuccessStatus(AionWebhookType.CASHLOAN_OPEN);
        request.setType("credits.limit.softcollection");
        request.setId("ignore-this");
        var queryFindByAionId = new Query(Criteria.where("aionWebhookId").is(request.getId()));

        // when
        var savedWebhookMono = aionWebHookService.save(request);

        // then
        assertAll(
                () -> StepVerifier.create(savedWebhookMono).expectNextCount(0).verifyComplete(),
                () -> assertEquals(0, mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).size())
        );
    }

    @Test
    void saveWebHookWithFailedStatusForOnboardingWebhook() {
        // given
        final String loanOfferId = randomUUID().toString();
        final String applicationId = randomUUID().toString();
        final var request = testData.getRequestWithFailureStatus(AionWebhookType.ONBOARDING);
        var queryFindByAionId = new Query(Criteria.where("aionWebhookId").is(request.getId()));
        var queryFindByLoanOfferId = new Query(Criteria.where("loanOfferId").is(loanOfferId));
        mongoTemplate.insertAll(getCreditApplications(request.getPayload().getProcessInstanceId(), applicationId));
        mongoTemplate.insertAll(getLoanOffers(loanOfferId, applicationId));

        // when
        var savedWebhookMono = aionWebHookService.save(request);

        // then
        assertAll(
                () -> StepVerifier.create(savedWebhookMono).expectNextCount(0).verifyComplete(),
                () -> assertEquals(1, mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).size()),
                () -> assertEquals(LoanApplicationStatus.REJECTED.name(), mongoTemplate.find(queryFindByLoanOfferId, LoanOfferStore.class).get(0).getOfferStatus()),
                () -> assertEquals(request.getPayload().getOfferId(), mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).get(0).getPayload().getOfferId())
        );
    }

    @Test
    void saveWebHookWithOnboardingSuccessStatus() {
        // given
        final String loanOfferId = randomUUID().toString();
        final String applicationId = randomUUID().toString();
        final var request = testData.getRequestWithSuccessStatus(AionWebhookType.ONBOARDING);
        var queryFindByAionId = new Query(Criteria.where("aionWebhookId").is(request.getId()));
        var queryFindByLoanOfferId = new Query(Criteria.where("loanOfferId").is(loanOfferId));
        mongoTemplate.insertAll(getCreditApplications(request.getPayload().getProcessInstanceId(), applicationId));
        mongoTemplate.insertAll(getLoanOffers(loanOfferId, applicationId));

        // when
        var savedWebhookMono = aionWebHookService.save(request);

        // then
        assertAll(
                () -> StepVerifier.create(savedWebhookMono).expectNextCount(0).verifyComplete(),
                () -> assertEquals(1, mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).size()),
                () -> assertEquals(LoanApplicationStatus.PENDING.name(), mongoTemplate.find(queryFindByLoanOfferId, LoanOfferStore.class).get(0).getOfferStatus()),
                () -> assertEquals(request.getPayload().getOfferId(), mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).get(0).getPayload().getOfferId())
        );
    }

    @Test
    void saveWebHookWithOnboardingManualAMLStatus() {
        // given
        final String loanOfferId = randomUUID().toString();
        final String applicationId = randomUUID().toString();
        final var request = testData.getRequestWithManualAMLStatus(AionWebhookType.ONBOARDING);
        var queryFindByAionId = new Query(Criteria.where("aionWebhookId").is(request.getId()));
        var queryFindByLoanOfferId = new Query(Criteria.where("loanOfferId").is(loanOfferId));
        mongoTemplate.insertAll(getCreditApplications(request.getPayload().getProcessInstanceId(), applicationId));
        mongoTemplate.insertAll(getLoanOffers(loanOfferId, applicationId));

        // when
        var savedWebhookMono = aionWebHookService.save(request);

        // then
        assertAll(
                () -> StepVerifier.create(savedWebhookMono).expectNextCount(0).verifyComplete(),
                () -> assertEquals(1, mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).size()),
                () -> assertEquals(LoanApplicationStatus.PENDING.name(), mongoTemplate.find(queryFindByLoanOfferId, LoanOfferStore.class).get(0).getOfferStatus()),
                () -> assertEquals(request.getPayload().getOfferId(), mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).get(0).getPayload().getOfferId())
        );
    }

    @Test
    void saveWebHookWithCashLoanOpenSuccessStatus() {
        // given
        final String loanOfferId = randomUUID().toString();
        final String applicationId = randomUUID().toString();
        final var request = testData.getRequestWithSuccessStatus(AionWebhookType.CASHLOAN_OPEN);
        var queryFindByAionId = new Query(Criteria.where("aionWebhookId").is(request.getId()));
        var queryFindByLoanOfferId = new Query(Criteria.where("loanOfferId").is(loanOfferId));
        mongoTemplate.insertAll(getCreditApplications(request.getPayload().getProcessInstanceId(), applicationId));
        mongoTemplate.insertAll(getLoanOffers(loanOfferId, applicationId));

        // when
        var savedWebhookMono = aionWebHookService.save(request);

        // then
        assertAll(
                () -> StepVerifier.create(savedWebhookMono).expectNextCount(0).verifyComplete(),
                () -> assertEquals(1, mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).size()),
                () -> assertEquals(LoanApplicationStatus.PAID_OUT.name(), mongoTemplate.find(queryFindByLoanOfferId, LoanOfferStore.class).get(0).getOfferStatus()),
                () -> assertEquals(request.getPayload().getOfferId(), mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).get(0).getPayload().getOfferId())
        );
    }

    @Test
    void saveWebHookWithFailedStatusForCashLoanOpenWebhook() {
        // given
        final String loanOfferId = randomUUID().toString();
        final String applicationId = randomUUID().toString();
        final var request = testData.getRequestWithFailureStatus(AionWebhookType.CASHLOAN_OPEN);
        var queryFindByAionId = new Query(Criteria.where("aionWebhookId").is(request.getId()));
        var queryFindByLoanOfferId = new Query(Criteria.where("loanOfferId").is(loanOfferId));
        mongoTemplate.insertAll(getCreditApplications(request.getPayload().getProcessInstanceId(), applicationId));
        mongoTemplate.insertAll(getLoanOffers(loanOfferId, applicationId));

        // when
        var savedWebhookMono = aionWebHookService.save(request);

        // then
        assertAll(
                () -> StepVerifier.create(savedWebhookMono).expectNextCount(0).verifyComplete(),
                () -> assertEquals(1, mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).size()),
                () -> assertEquals(LoanApplicationStatus.REJECTED.name(), mongoTemplate.find(queryFindByLoanOfferId, LoanOfferStore.class).get(0).getOfferStatus()),
                () -> assertEquals(request.getPayload().getOfferId(), mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).get(0).getPayload().getOfferId())
        );
    }


    @ParameterizedTest
    @EnumSource(value = LoanApplicationStatus.class, names = {"PAID_OUT", "REJECTED", "CANCELED"})
    void doNotUpdateOfferInFinalStateForCashLoanOpenWebhook(final LoanApplicationStatus status) {
        // given
        final String loanOfferId = randomUUID().toString();
        final String applicationId = randomUUID().toString();
        final var request = testData.getRequestWithFailureStatus(AionWebhookType.CASHLOAN_OPEN);
        var queryFindByAionId = new Query(Criteria.where("aionWebhookId").is(request.getId()));
        var queryFindByLoanOfferId = new Query(Criteria.where("loanOfferId").is(loanOfferId));
        mongoTemplate.insertAll(getCreditApplications(request.getPayload().getProcessInstanceId(), applicationId));
        mongoTemplate.insertAll(getLoanOffers(loanOfferId, applicationId, status));

        // when
        var savedWebhookMono = aionWebHookService.save(request);

        // then
        assertAll(
                () -> StepVerifier.create(savedWebhookMono).expectNextCount(0).verifyComplete(),
                () -> assertEquals(1, mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).size()),
                () -> assertEquals(status.name(), mongoTemplate.find(queryFindByLoanOfferId, LoanOfferStore.class).get(0).getOfferStatus()),
                () -> assertEquals(request.getPayload().getOfferId(), mongoTemplate.find(queryFindByAionId, AionWebhookStore.class).get(0).getPayload().getOfferId())
        );
    }


    @Test
    void getErrorWhenProcessIdIsMissingInDb() {
        // given
        final var request = testData.getRequest("99ec11d6-b07a-4bdb-91c4-3030f5911682");

        // when
        var savedWebhookMono = aionWebHookService.save(request);

        // then
        StepVerifier.create(savedWebhookMono).verifyError(ResourceNotFoundException.class);
    }

    @Test
    void getErrorWhenOfferNotFound() {
        // given
        final var request = testData.getRequest("7adf46e0-129e-4610-acd9-99517a848a90");
        mongoTemplate.insertAll(getCreditApplications(request.getPayload().getProcessInstanceId(), "applicationId125"));

        // when
        var savedWebhookMono = aionWebHookService.save(request);

        // then
        StepVerifier.create(savedWebhookMono).verifyError(ResourceNotFoundException.class);
    }

    private List<LoanOfferStore> getLoanOffers(String loanOfferId, String applicationId) {
        return List.of(
                LoanOfferStore.builder()
                        .loanOfferId(loanOfferId)
                        .isAccepted(true)
                        .offerStatus("PENDING")
                        .userUUID("77a1dc11-9947-43f3-bee7-c6c21e849356")
                        .applicationId(applicationId)
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.AION.name())).build()).build()
        );
    }

    private List<LoanOfferStore> getLoanOffers(final String loanOfferId, final String applicationId, final LoanApplicationStatus offerStatus) {
        return List.of(
                LoanOfferStore.builder()
                        .loanOfferId(loanOfferId)
                        .isAccepted(true)
                        .offerStatus(offerStatus.name())
                        .userUUID("77a1dc11-9947-43f3-bee7-c6c21e849356")
                        .applicationId(applicationId)
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.AION.name())).build()).build()
        );
    }

    private List<CreditApplicationResponseStore> getCreditApplications(String processId, String applicationId) {
        return List.of(
                CreditApplicationResponseStore.builder()
                        .processId(processId)
                        .applicationId(applicationId)
                        .build()
        );
    }
}
