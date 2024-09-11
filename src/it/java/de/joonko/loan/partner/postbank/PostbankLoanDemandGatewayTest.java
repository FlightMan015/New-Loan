package de.joonko.loan.partner.postbank;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.dac.fts.FtsAccountSnapshotGatewayMocks;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestSoapEnvelope;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStoreService;
import de.joonko.loan.webhooks.postbank.model.ContractState;
import de.joonko.loan.webhooks.postbank.model.CreditResult;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

@ExtendWith(RandomBeansExtension.class)
@AutoConfigureWebTestClient
@SpringBootTest(properties = {"postbank.offer-response-retry-max-attempts=2","postbank.offer-response-retry-max-delay=3"})
@ContextConfiguration(initializers = WireMockInitializer.class)
@ActiveProfiles("integration")
class PostbankLoanDemandGatewayTest {

    @Autowired
    private PostbankLoanDemandGateway postbankLoanDemandGateway;

    @Autowired
    private PostbankLoanDemandStoreService postbankLoanDemandStoreService;

    @Autowired
    private WireMockServer mockServer;
    private static PostbankClientMocks postbankClientMocks;
    private static FtsAccountSnapshotGatewayMocks ftsAccountSnapshotGatewayMocks;

    @Autowired
    MongoTemplate mongoTemplate;

    private static final String TRANSACTION_ID = "63859235";

    @BeforeEach
    void setUp() {
        postbankClientMocks = new PostbankClientMocks(mockServer);
        ftsAccountSnapshotGatewayMocks = new FtsAccountSnapshotGatewayMocks(mockServer);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should successfully submit the data for Postbank credit demand endpoint")
    void callApi_successCase(@Random LoanDemandRequest loanDemandRequest, @Random LoanDemandPostbankRequestSoapEnvelope request) {
        loanDemandRequest.setApplicationId("gf7329f8h329");
        loanDemandRequest.setFtsTransactionId(TRANSACTION_ID);
        mongoTemplate.insert(loanDemandRequest);
        ftsAccountSnapshotGatewayMocks.fake200WhenAskingForFtsData(TRANSACTION_ID);
        postbankClientMocks.fake200WhenAskingForLoan(loanDemandRequest.getApplicationId());

        // when
        final var response = Mono.zip(
                postbankLoanDemandGateway.callApi(request, loanDemandRequest.getApplicationId()),
                addOffers(loanDemandRequest.getApplicationId(), ContractState.IM_SYSTEM_GESPEICHERT_10, 2),
                addOffers(loanDemandRequest.getApplicationId(), ContractState.ONLINE_GENEHMIGT_24, 4)
        ).map(Tuple2::getT1);

        // then
        StepVerifier.create(response).expectNextCount(1).verifyComplete();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should fail to submit the data for Postbank credit demand endpoint")
    void callApi_missingSuccessContractState(@Random LoanDemandRequest loanDemandRequest, @Random LoanDemandPostbankRequestSoapEnvelope request) {
        loanDemandRequest.setApplicationId("gf7329f8h3213");
        loanDemandRequest.setFtsTransactionId(TRANSACTION_ID);
        mongoTemplate.insert(loanDemandRequest);
        ftsAccountSnapshotGatewayMocks.fake200WhenAskingForFtsData(TRANSACTION_ID);
        postbankClientMocks.fake200WhenAskingForLoan(loanDemandRequest.getApplicationId());

        // when
        final var response = Mono.zip(
                postbankLoanDemandGateway.callApi(request, loanDemandRequest.getApplicationId()),
                addOffers(loanDemandRequest.getApplicationId(), ContractState.IM_SYSTEM_GESPEICHERT_10, 2),
                addOffers(loanDemandRequest.getApplicationId(), ContractState.MANUELL_ABGEWIESEN_94, 4)
        ).map(Tuple2::getT1);

        // then
        StepVerifier.create(response).verifyError();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should fail when getting dac error")
    void callApi_dac_error(@Random LoanDemandRequest loanDemandRequest, @Random LoanDemandPostbankRequestSoapEnvelope request) {
        loanDemandRequest.setApplicationId("h329f8h329");
        loanDemandRequest.setFtsTransactionId(TRANSACTION_ID);
        mongoTemplate.insert(loanDemandRequest);
        ftsAccountSnapshotGatewayMocks.fake200WhenAskingForFtsData(TRANSACTION_ID);
        postbankClientMocks.fake200WithDacErrorWhenAskingForLoan(loanDemandRequest.getApplicationId());

        // when
        final var response = postbankLoanDemandGateway.callApi(request, loanDemandRequest.getApplicationId());

        // then
        StepVerifier.create(response).verifyError();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should fail when invalid iban")
    void callApi_invalid_iban(@Random LoanDemandRequest loanDemandRequest, @Random LoanDemandPostbankRequestSoapEnvelope request) {
        loanDemandRequest.setApplicationId("27f9128hr912");
        loanDemandRequest.setFtsTransactionId(TRANSACTION_ID);
        mongoTemplate.insert(loanDemandRequest);
        ftsAccountSnapshotGatewayMocks.fake200WhenAskingForFtsData(TRANSACTION_ID);
        postbankClientMocks.fake200WithInvalidIbanWhenAskingForLoan(loanDemandRequest.getApplicationId());

        // when
        final var response = postbankLoanDemandGateway.callApi(request, loanDemandRequest.getApplicationId());

        // then
        StepVerifier.create(response).verifyError();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should get an error when there was no CreditResult received")
    void callApi_noOfferCase(@Random LoanDemandRequest loanDemandRequest, @Random LoanDemandPostbankRequestSoapEnvelope request) {
        loanDemandRequest.setApplicationId("j28d19f8h2");
        loanDemandRequest.setFtsTransactionId(TRANSACTION_ID);
        mongoTemplate.insert(loanDemandRequest);
        ftsAccountSnapshotGatewayMocks.fake200WhenAskingForFtsData(TRANSACTION_ID);
        postbankClientMocks.fake400WhenAskingForLoan();

        final var response = postbankLoanDemandGateway.callApi(request, loanDemandRequest.getApplicationId());

        StepVerifier.create(response).verifyError();
    }

    private Mono<PostbankLoanDemandStore> addOffers(String applicationId, ContractState contractState, int delayInSeconds) {
        final var creditResult = CreditResult.builder().contractState(contractState).build();

        return Mono.just(creditResult)
                .delayElement(Duration.ofSeconds(delayInSeconds))
                .flatMap(result ->
                        postbankLoanDemandStoreService.addOffersResponse(applicationId, result))
                .subscribeOn(Schedulers.elastic());
    }


}