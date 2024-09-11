package de.joonko.loan.db.service;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.db.vo.OfferAcceptedEnum;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.user.states.OfferDataStateDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.test.StepVerifier;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ActiveProfiles("integration")
@SpringBootTest
@ExtendWith(RandomBeansExtension.class)
class LoanOfferStoreServiceTest {

    @Autowired
    private LoanOfferStoreService loanOfferStoreService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @MockBean
    private DateTimeProvider dateTimeProvider;

    @SpyBean
    private AuditingHandler auditingHandler;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("loanOfferStore");
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }

    @Test
    void save_all_loan_offers(@Random(type = de.joonko.loan.offer.domain.LoanOffer.class) List<de.joonko.loan.offer.domain.LoanOffer> loanOffers) {
        // given
        String userUuid = UUID.randomUUID().toString();
        String applicationId = "applicationId";
        String parentApplicationId = "parentApplicationId";
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));

        // when
        var savedLoanOffers = loanOfferStoreService.saveAll(loanOffers, userUuid, applicationId, parentApplicationId);

        // then
        StepVerifier.create(savedLoanOffers)
                .consumeNextWith(consume -> assertAll(
                        () -> assertEquals(userUuid, consume.getUserUUID()),
                        () -> assertEquals(applicationId, consume.getApplicationId()),
                        () -> assertEquals(parentApplicationId, consume.getParentApplicationId()),
                        () -> assertNotNull(consume.getLoanProviderReferenceNumber()),
                        () -> assertNotNull(consume.getLastModifiedTS())
                ))
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void do_not_save_loan_offers_when_missing() {
        // given
        String userUuid = UUID.randomUUID().toString();
        String applicationId = "applicationId";
        String parentApplicationId = "parentApplicationId";

        // when
        var savedLoanOffers = loanOfferStoreService.saveAll(List.of(), userUuid, applicationId, parentApplicationId);

        // then
        StepVerifier.create(savedLoanOffers)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void update_accepted_status_when_offer_already_exist(@Random LoanOffer loanOffer) {
        // given
        String loanOfferId = "123456";
        String loanProviderReferenceNumber = "654321";
        String userUuid = UUID.randomUUID().toString();
        LoanOfferStore loanOfferStore = LoanOfferStore.builder()
                .loanOfferId(loanOfferId)
                .userUUID(userUuid)
                .offer(loanOffer)
                .build();
        mongoTemplate.save(loanOfferStore);

        // when
        final var savedLoanOffer = loanOfferStoreService.updateAcceptedStatus(loanOfferId, loanProviderReferenceNumber, OfferAcceptedEnum.USER);

        // then

        StepVerifier.create(savedLoanOffer)
                .consumeNextWith(offer -> assertAll(
                        () -> assertEquals(loanProviderReferenceNumber, offer.getLoanProviderReferenceNumber()),
                        () -> assertEquals(OfferAcceptedEnum.USER, offer.getAcceptedBy()),
                        () -> assertTrue(offer.getIsAccepted()),
                        () -> assertNotNull(offer.getAcceptedDate())
                )).verifyComplete();
    }

    @Test
    void updateOffer() {
        // given
        final var offerId = "38962395";
        final var loanProviderOfferId = "h3829fociw";
        mongoTemplate.save(LoanOfferStore.builder().loanOfferId(offerId).build());

        // when
        var savedLoanOffer = loanOfferStoreService.updateOffer(offerId, loanProviderOfferId, LoanApplicationStatus.REJECTED);

        // then
        StepVerifier.create(savedLoanOffer)
                .consumeNextWith(offer -> assertAll(
                        () -> assertEquals(loanProviderOfferId, offer.getLoanProviderReferenceNumber()),
                        () -> assertEquals(LoanApplicationStatus.REJECTED.name(), offer.getOfferStatus()),
                        () -> assertNotNull(offer.getStatusLastUpdateDate()))
                ).verifyComplete();
    }

    @Test
    void find_by_id_when_exists() {
        // given
        String loanOfferId = "128456";
        mongoTemplate.save(LoanOfferStore.builder().loanOfferId(loanOfferId).build());

        // when
        var loanOffer = loanOfferStoreService.findById(loanOfferId);

        // then
        StepVerifier.create(loanOffer).expectNextCount(1).verifyComplete();
    }

    @Test
    void throw_exception_when_does_not_exist() {
        // given
        String loanOfferId = "123456";

        // when
        var loanOffer = loanOfferStoreService.findById(loanOfferId);

        // then
        StepVerifier.create(loanOffer).verifyError();
    }

    @Test
    void saveNewOffer() {
        // given
        var loanOfferStore = LoanOfferStore.builder().loanOfferId("39463").build();

        // when
        var loanOffer = loanOfferStoreService.saveOffer(loanOfferStore);

        // then
        StepVerifier.create(loanOffer).consumeNextWith(offer -> assertEquals("39463", offer.getLoanOfferId())).verifyComplete();
    }

    @Test
    void updateExistingOffer() {
        // given
        var loanOfferStore = LoanOfferStore.builder().loanOfferId("39763957").build();
        mongoTemplate.save(loanOfferStore);
        loanOfferStore.setOfferStatus("SUCCESS");

        // when
        var loanOffer = loanOfferStoreService.saveOffer(loanOfferStore);

        // then
        StepVerifier.create(loanOffer).consumeNextWith(offer -> assertEquals("SUCCESS", offer.getOfferStatus())).verifyComplete();
    }

    @Test
    void throw_not_found_for_update_accepted_status_when_missing() {
        // given
        String loanOfferId = "123456";
        String loanProviderReferenceNumber = "654321";

        // when
        final var loanOffer = loanOfferStoreService.updateAcceptedStatus(loanOfferId, loanProviderReferenceNumber, OfferAcceptedEnum.USER);

        // then
        StepVerifier.create(loanOffer).verifyError();
    }

    @Test
    void find_by_loan_offer_id() {
        // given
        String loanOfferId = "123456";
        mongoTemplate.save(LoanOfferStore.builder().loanOfferId(loanOfferId).build());

        // when
        LoanOfferStore result = loanOfferStoreService.findByLoanOfferId(loanOfferId);

        // then
        assertNotNull(result);
    }

    @Test
    void throw_not_found_for_find_by_loan_offer_id_when_missing() {
        // given
        String loanOfferId = "123456";

        // when
        Executable executable = () -> loanOfferStoreService.findByLoanOfferId(loanOfferId);


        // then
        Exception exception = assertThrows(RuntimeException.class, executable);
        assertEquals("LoanOfferStore not found for offer id" + loanOfferId, exception.getMessage());
    }


    @Test
    void get_no_offers_by_application_id_when_none_exist() {
        // given
        String applicationId = "123456";

        // when
        List<LoanOfferStore> result = loanOfferStoreService.findAllByLoanApplicationId(applicationId);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void get_all_offers_by_application_id() {
        // given
        String applicationId = "123456";
        mongoTemplate.save(LoanOfferStore.builder().loanOfferId("12").applicationId(applicationId).build());
        mongoTemplate.save(LoanOfferStore.builder().loanOfferId("13").applicationId(applicationId).build());

        // when
        List<LoanOfferStore> result = loanOfferStoreService.findAllByLoanApplicationId(applicationId);

        // then
        assertEquals(2, result.size());
    }

    @Test
    void find_not_deleted_offers_with_recommendations() {
        // given
        String userUuid = "123456";
        String applicationId = "111";
        int requestedLoanAmount = 10000;
        List<LoanOfferStore> offers = List.of(
                LoanOfferStore.builder().applicationId(applicationId).loanOfferId("21").offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.SANTANDER.label)).amount(requestedLoanAmount).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId(applicationId).loanOfferId("23").offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.SWK_BANK.label)).amount(requestedLoanAmount).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId("222").loanOfferId("134").offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.SWK_BANK.label)).amount(12000).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId("333").parentApplicationId(applicationId).loanOfferId("15").offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.SANTANDER.label)).amount(8500).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId("444").parentApplicationId(applicationId).loanOfferId("16").offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.SANTANDER.label)).amount(5000).build()).userUUID(userUuid).build());
        mongoTemplate.insertAll(offers);
        Set<OfferDataStateDetails> offersStates = Set.of(
                OfferDataStateDetails.builder().applicationId(applicationId).amount(9500).build(),
                OfferDataStateDetails.builder().applicationId("333").parentApplicationId(applicationId).amount(9000).build(),
                OfferDataStateDetails.builder().applicationId("444").parentApplicationId(applicationId).amount(8000).build());

        // when
        var result = loanOfferStoreService.findNotDeletedOffersWithRecommendations(userUuid, applicationId, offersStates)
                .stream()
                .map(LoanOfferStore::getLoanOfferId)
                .collect(Collectors.toList());

        // then
        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertTrue(result.containsAll(List.of("21", "23", "15")))
        );
    }

    @Test
    void get_loan_offers_only_bonify_loans() {
        // given
        String userUuid = "123456";
        String applicationId = "111";
        var offers = List.of(
                LoanOfferStore.builder().applicationId(applicationId).offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.CONSORS.label)).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId(applicationId).offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.AION.label)).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId("222").offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.CONSORS.label)).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId("333").parentApplicationId(applicationId).offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.AION.label)).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId("444").parentApplicationId(applicationId).offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.SANTANDER.label)).build()).userUUID(userUuid).build());
        mongoTemplate.insertAll(offers);

        // when
        var result = loanOfferStoreService.getLoanOffers(userUuid, applicationId, true);

        // then
        StepVerifier.create(result)
                .consumeNextWith(res -> assertEquals(2, res.size()))
                .verifyComplete();
    }

    @Test
    void get_loan_offers_from_external_loan_providers() {
        // given
        String userUuid = "123456";
        String applicationId = "111";
        var offers = List.of(
                LoanOfferStore.builder().applicationId(applicationId).offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.CONSORS.label)).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId(applicationId).offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.AION.label)).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId("222").offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.CONSORS.label)).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId("333").parentApplicationId(applicationId).offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.SANTANDER.label)).build()).userUUID(userUuid).build(),
                LoanOfferStore.builder().applicationId("444").parentApplicationId(applicationId).offer(LoanOffer.builder().loanProvider(new LoanProvider(Bank.SANTANDER.label)).build()).userUUID(userUuid).build());
        mongoTemplate.insertAll(offers);

        // when
        var result = loanOfferStoreService.getLoanOffers(userUuid, applicationId, false);

        // then
        StepVerifier.create(result)
                .consumeNextWith(res -> assertEquals(3, res.size()))
                .verifyComplete();
    }

    @Test
    void find_all_offers_should_not_return_deleted_offers_for_user_id() {
        // given
        String userUuid = "123456";
        String applicationId = "any application id";
        int requestedLoanAmount = 6000;
        mongoTemplate.save(LoanOfferStore.builder().loanOfferId("12").applicationId(applicationId).deleted(true).userUUID(userUuid).build());
        mongoTemplate.save(LoanOfferStore.builder().loanOfferId("13").applicationId(applicationId).deleted(true).userUUID(userUuid).build());

        // when
        Set<LoanOfferStore> result = loanOfferStoreService.findNotDeletedOffersWithRecommendations(userUuid, applicationId, Set.of());

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_all_offers_by_user_id() {
        // given
        String userUuid = "123456";
        mongoTemplate.save(LoanOfferStore.builder().loanOfferId("12").userUUID(userUuid).build());
        mongoTemplate.save(LoanOfferStore.builder().loanOfferId("13").userUUID(userUuid).build());
        Query queryFindByUserUuid = new Query(Criteria.where("userUUID").is(userUuid));

        // when
        var deletedOffersMono = loanOfferStoreService.deleteAllByUserId(userUuid);

        // then
        assertAll(
                () -> StepVerifier.create(deletedOffersMono).expectNextCount(2).verifyComplete(),
                () -> assertTrue(mongoTemplate.find(queryFindByUserUuid, LoanOfferStore.class).isEmpty())
        );
    }

    @Test
    void updateOfferStatus_statusChanged() {
        // given
        final var offerId = "123";
        final var newStatus = "PAID_OUT";
        final var existingStatus = "APPROVED";
        final var offer = mongoTemplate.save(LoanOfferStore.builder()
                .loanOfferId(offerId)
                .userUUID("userUuid")
                .offerStatus(existingStatus)
                .statusLastUpdateDate(OffsetDateTime.now().minusHours(5))
                .build());

        // when
        var updatedOffer = loanOfferStoreService.updateOfferStatus(offerId, newStatus);

        // then
        StepVerifier.create(updatedOffer).consumeNextWith(res -> assertAll(
                () -> assertThat(res.getStatusLastUpdateDate()).isAfter(offer.getStatusLastUpdateDate()),
                () -> assertEquals(newStatus, res.getOfferStatus())
        )).verifyComplete();
    }

    @Test
    void updateOfferStatus_statusNotChanged() {
        // given
        final var offerId = "123";
        final var existingStatus = "APPROVED";
        final var offer = mongoTemplate.save(LoanOfferStore.builder()
                .loanOfferId(offerId)
                .userUUID("userUuid")
                .offerStatus(existingStatus)
                .statusLastUpdateDate(OffsetDateTime.now().minusHours(5))
                .build());

        // when
        var updatedOffer = loanOfferStoreService.updateOfferStatus(offerId, existingStatus);

        // then
        StepVerifier.create(updatedOffer).expectNextCount(0).verifyComplete();
    }

    @Test
    void updateOfferStatus_throwErrorWhenOfferMissing() {
        // given
        final var offerId = "123";
        final var status = "APPROVED";

        // when
        var updatedOffer = loanOfferStoreService.updateOfferStatus(offerId, status);

        // then
        StepVerifier.create(updatedOffer).verifyError();
    }

    @Test
    void updateKycStatus_statusChanged() {
        // given
        final var offerId = "123";
        final var newStatus = "SUCCESS";
        final var existingStatus = "INITIATED";
        final var offer = mongoTemplate.save(LoanOfferStore.builder()
                .loanOfferId(offerId)
                .userUUID("userUuid")
                .kycStatus(existingStatus)
                .kycStatusLastUpdateDate(OffsetDateTime.now())
                .build());

        // when
        var updatedOffer = loanOfferStoreService.updateKycStatus(offerId, newStatus);

        // then

        StepVerifier.create(updatedOffer).consumeNextWith(res -> assertAll(
                () -> assertThat(res.getKycStatusLastUpdateDate()).isAfter(offer.getKycStatusLastUpdateDate()),
                () -> assertEquals(newStatus, res.getKycStatus())
        )).verifyComplete();
    }

    @Test
    void updateKycStatus_statusNotChanged() {
        // given
        final var offerId = "123";
        final var existingStatus = "SUCCESS";
        final var offer = mongoTemplate.save(LoanOfferStore.builder()
                .loanOfferId(offerId)
                .userUUID("userUuid")
                .kycStatus(existingStatus)
                .build());

        // when
        var updatedOffer = loanOfferStoreService.updateKycStatus(offerId, existingStatus);

        // then
        StepVerifier.create(updatedOffer).expectNextCount(0).verifyComplete();
    }

    @Test
    void updateKycStatus_throwErrorWhenOfferMissing() {
        // given
        final var offerId = "123";
        final var status = "APPROVED";

        // when
        var updatedOffer = loanOfferStoreService.updateKycStatus(offerId, status);

        // then
        StepVerifier.create(updatedOffer).verifyError();
    }

    @Test
    void findByOfferStatusAndLoanProvider() {
        // given
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder()
                        .isAccepted(true)
                        .loanOfferId("19")
                        .offerStatus("PENDING")
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build())
                        .build(),
                LoanOfferStore.builder()
                        .isAccepted(true)
                        .loanOfferId("29")
                        .offerStatus("SUCCESS")
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build())
                        .build(),
                LoanOfferStore.builder()
                        .isAccepted(true)
                        .loanOfferId("21")
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build())
                        .build(),
                LoanOfferStore.builder()
                        .isAccepted(false)
                        .loanOfferId("22")
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build())
                        .build(),
                LoanOfferStore.builder()
                        .isAccepted(null)
                        .loanOfferId("23")
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build())
                        .build()
        ));
        var statuses = EnumSet.of(LoanApplicationStatus.PENDING);

        // when
        var actualOffers = loanOfferStoreService.findByOfferStatusAndLoanProvider(Set.of(Bank.SANTANDER), statuses);

        // then
        StepVerifier.create(actualOffers)
                .consumeNextWith(offer -> assertEquals("19", offer.getLoanOfferId()))
                .consumeNextWith(offer -> assertEquals("21", offer.getLoanOfferId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findByOfferStatusAndLoanProviderForNoOfferStatus() {
        // given
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder()
                        .loanOfferId("21")
                        .isAccepted(true)
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build())
                        .build()
        ));
        var statuses = EnumSet.of(LoanApplicationStatus.PENDING);

        // when
        var actualOffers = loanOfferStoreService.findByOfferStatusAndLoanProvider(Set.of(Bank.SANTANDER), statuses);

        // then
        StepVerifier.create(actualOffers)
                .consumeNextWith(offer -> assertEquals("21", offer.getLoanOfferId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findMultipleByOfferStatusAndLoanProvider() {
        // given
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder().isAccepted(true).loanOfferId("111").offerStatus("PENDING").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder().isAccepted(false).loanOfferId("888").offerStatus("PENDING").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder().isAccepted(true).loanOfferId("222").offerStatus("SUCCESS").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder().isAccepted(true).loanOfferId("333").offerStatus("PAID_OUT").offer(LoanOffer.builder().loanProvider(new LoanProvider("SWK_BANK")).build()).build(),
                LoanOfferStore.builder().isAccepted(true).loanOfferId("444").offerStatus("SUCCESS").offer(LoanOffer.builder().loanProvider(new LoanProvider("SWK_BANK")).build()).build(),
                LoanOfferStore.builder().isAccepted(true).loanOfferId("555").offerStatus("PAID_OUT").offer(LoanOffer.builder().loanProvider(new LoanProvider("Consors Finanz")).build()).build(),
                LoanOfferStore.builder().isAccepted(true).loanOfferId("666").offerStatus("SUCCESS").offer(LoanOffer.builder().loanProvider(new LoanProvider("Consors Finanz")).build()).build(),
                LoanOfferStore.builder().isAccepted(true).loanOfferId("777").offerStatus(null).offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder().loanOfferId("999").offerStatus(null).offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build()
        ));
        var statuses = EnumSet.of(LoanApplicationStatus.PENDING, LoanApplicationStatus.PAID_OUT);

        // when
        var actualOffers = loanOfferStoreService.findByOfferStatusAndLoanProvider(Set.of(Bank.SANTANDER, Bank.SWK_BANK), statuses);

        // then
        StepVerifier.create(actualOffers)
                .consumeNextWith(offer -> assertEquals("111", offer.getLoanOfferId()))
                .consumeNextWith(offer -> assertEquals("333", offer.getLoanOfferId()))
                .consumeNextWith(offer -> assertEquals("777", offer.getLoanOfferId()))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void doNotFindByOfferStatusAndLoanProviderWhenNotMatching() {
        // given
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder().loanOfferId("10").offerStatus("CANCELLED").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder().loanOfferId("20").offerStatus("SUCCESS").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build()
        ));
        var statuses = EnumSet.of(LoanApplicationStatus.PENDING);

        // when
        var actualOffers = loanOfferStoreService.findByOfferStatusAndLoanProvider(Set.of(Bank.SANTANDER), statuses);

        // then
        StepVerifier.create(actualOffers).expectNextCount(0).verifyComplete();
    }

    @Test
    void doNotFindByOfferStatusAndLoanProviderWhenEmptyStatusList() {
        // given
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder().loanOfferId("1").offerStatus("CANCELLED").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder().loanOfferId("2").offerStatus("SUCCESS").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build()
        ));
        var emptySet = EnumSet.noneOf(LoanApplicationStatus.class);

        // when
        var actualOffers = loanOfferStoreService.findByOfferStatusAndLoanProvider(Set.of(Bank.SANTANDER), emptySet);

        // then
        StepVerifier.create(actualOffers).expectNextCount(0).verifyComplete();
    }

    @Test
    void doNotFindByOfferStatusAndLoanProviderWhenEmptyProviderList() {
        // given
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder().loanOfferId("1").offerStatus("PENDING").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build(),
                LoanOfferStore.builder().loanOfferId("2").offerStatus("SUCCESS").offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build()).build()
        ));
        var statuses = EnumSet.of(LoanApplicationStatus.PENDING);

        // when
        var actualOffers = loanOfferStoreService.findByOfferStatusAndLoanProvider(Set.of(), statuses);

        // then
        StepVerifier.create(actualOffers).expectNextCount(0).verifyComplete();
    }

    @Test
    void findAcceptedByApplicationIdAndBankProvider() {
        // given
        final var applicationId = "32f89d3cj29";
        final var loanProvider = Bank.CONSORS;
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder()
                        .applicationId(applicationId)
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .build()).build()
        ));

        // when
        var foundOffer = loanOfferStoreService.findAcceptedByApplicationIdAndBankProvider(applicationId, loanProvider);

        // then
        StepVerifier.create(foundOffer).expectNextCount(1).verifyComplete();
    }

    @Test
    void emptyWhenFindingAcceptedByApplicationIdAndBankProvider() {
        // given
        final var applicationId = "32f89dj29";
        final var loanProvider = Bank.AION;

        // when
        var foundOffer = loanOfferStoreService.findAcceptedByApplicationIdAndBankProvider(applicationId, loanProvider);

        // then
        StepVerifier.create(foundOffer).expectNextCount(0).verifyComplete();
    }

    @Test
    void getSingleLoanOfferForLoanProviderReferenceNumber_noOfferCase() {
        final var loanProviderReferenceNumber = "3";
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .build()).build(),
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("2")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .build()).build()
        ));

        var foundOffer = loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(loanProviderReferenceNumber);
        StepVerifier.create(foundOffer).expectNextCount(0).verifyComplete();
    }

    @Test
    void getSingleLoanOfferForLoanProviderReferenceNumber_oneOfferCase() {
        final var loanProviderReferenceNumber = "1";
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .build()).build(),
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("2")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .build()).build()
        ));

        var foundOffer = loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(loanProviderReferenceNumber);
        StepVerifier.create(foundOffer).expectNextCount(1).verifyComplete();
    }

    @Test
    void getSingleLoanOfferForLoanProviderReferenceNumber_multipleOfferCase() {
        final var loanProviderReferenceNumber = "1";
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .build()).build(),
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .build()).build()
        ));

        var foundOffer = loanOfferStoreService.getSingleLoanOfferForLoanProviderReferenceNumber(loanProviderReferenceNumber);
        StepVerifier.create(foundOffer).expectNextCount(0).verifyComplete();
    }

    @Test
    void getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDuration_noOfferCase() {
        final var loanProviderReferenceNumber = "3";
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .durationInMonth(12)
                                .build()).build(),
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("2")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .durationInMonth(12)
                                .build()).build()
        ));

        var foundOffer = loanOfferStoreService.getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDurationAndLoanProvider(loanProviderReferenceNumber, 12, "Consors Finanz");
        StepVerifier.create(foundOffer).expectNextCount(0).verifyComplete();
    }

    @Test
    void getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDuration_oneOfferCase() {
        final var loanProviderReferenceNumber = "1";
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .durationInMonth(12)
                                .build()).build(),
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("SWK_BANK"))
                                .durationInMonth(12)
                                .build()).build(),
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .durationInMonth(18)
                                .build()).build()
        ));

        var foundOffer = loanOfferStoreService.getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDurationAndLoanProvider(loanProviderReferenceNumber, 12, "Consors Finanz");
        StepVerifier.create(foundOffer).expectNextCount(1).verifyComplete();
    }

    @Test
    void getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDuration_multipleOfferCase() {
        final var loanProviderReferenceNumber = "1";
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .durationInMonth(12)
                                .build()).build(),
                LoanOfferStore.builder()
                        .loanProviderReferenceNumber("1")
                        .isAccepted(true)
                        .offer(LoanOffer.builder()
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .durationInMonth(12)
                                .build()).build()
        ));

        var foundOffer = loanOfferStoreService.getSingleLoanOffersByLoanProviderReferenceNumberAndOfferDurationAndLoanProvider(loanProviderReferenceNumber, 12, "Consors Finanz");
        StepVerifier.create(foundOffer).expectNextCount(0).verifyComplete();
    }

    void findAbandonedOffersWithAnyOffer() {
        // given
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(80)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("2eb498cb-ec0c-4cd8-8381-9751b42bf17f").build());

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(7)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("3704060c-2a3d-40ae-8842-d51660c41937").build());

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(7)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("adb1d947-a944-4aaf-87a0-079640fbd033").build());
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(6)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("adb1d947-a944-4aaf-87a0-079640fbd033").build());

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(7)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("7e88c06e-6d19-47cb-8dfa-5076092ba74e").build());
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(3)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("7e88c06e-6d19-47cb-8dfa-5076092ba74e").kycStatus("CANCELED").build());

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(7)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("f58ec0d6-037e-423f-80a6-b62dde585570").build());
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(3)));
        mongoTemplate.insert(LoanOfferStore.builder().userUUID("f58ec0d6-037e-423f-80a6-b62dde585570").offerStatus("SUCCESS").build());

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now().minusDays(7)));
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder().userUUID("756cfb42-eb34-415f-9cbb-a0144ee3d10b").kycStatus("CANCELED").build(),
                LoanOfferStore.builder().userUUID("5f1e80f1-a0a7-4279-bc84-b47b5aa3d972").kycStatus("CANCELLED").build(),
                LoanOfferStore.builder().userUUID("cd6e6545-3556-4b6b-ade4-13563c6ef377").kycStatus("FAILURE").build(),
                LoanOfferStore.builder().userUUID("9960794e-00fb-4da5-8ab6-f0050fe3d8c2").kycStatus("ABORTED").build(),
                LoanOfferStore.builder().userUUID("2d918d78-55be-488e-8831-d17fefefd586").kycStatus("REJECTED").build(),
                LoanOfferStore.builder().userUUID("c8902314-3091-4860-a9fb-296b7b0f3ac0").kycStatus("APPROVED").build(),
                LoanOfferStore.builder().userUUID("439c13cf-6a3f-424b-92f7-3f480fca2b62").kycStatus("PAID_OUT").build(),
                LoanOfferStore.builder().userUUID("5f124b47-d591-4596-b8bd-b70d3848e3fd").kycStatus("SUCCESS").build(),
                LoanOfferStore.builder().userUUID("5dee85a8-e2b9-4422-a3c6-d701250c8ee2").kycStatus("SUCCESS_DATA_CHANGED").build(),
                LoanOfferStore.builder().userUUID("d176ee73-f65b-48f5-890c-1709f0111f20").offerStatus("CANCELED").build(),
                LoanOfferStore.builder().userUUID("3a94cdc7-6e74-4232-848e-12c87871b05b").offerStatus("CANCELLED").build(),
                LoanOfferStore.builder().userUUID("beb89ebf-eb73-4600-9ebd-daca6900259f").offerStatus("FAILURE").build(),
                LoanOfferStore.builder().userUUID("0da582ae-e831-4a62-90fc-cc43f41ccb2b").offerStatus("ABORTED").build(),
                LoanOfferStore.builder().userUUID("848d0485-9031-4130-ac8b-819f33d59122").offerStatus("REJECTED").build(),
                LoanOfferStore.builder().userUUID("ad027ce5-afe6-496d-8342-2523c93e6ebd").offerStatus("APPROVED").build(),
                LoanOfferStore.builder().userUUID("e62370a3-27df-46c4-9747-8fd67e1c8312").offerStatus("PAID_OUT").build(),
                LoanOfferStore.builder().userUUID("fbf0cb84-9eb1-41b8-88db-b1746e515ab0").offerStatus("SUCCESS").build(),
                LoanOfferStore.builder().userUUID("8f0a5edb-78b3-4244-bf5c-2cc3476c9216").offerStatus("SUCCESS_DATA_CHANGED").build(),

                LoanOfferStore.builder().userUUID("ff0f26d1-332b-419a-b0ee-b7eec3a72dca").kycStatus("INITIATED").build(),
                LoanOfferStore.builder().userUUID("7d4baa6d-bd0a-4ea7-9e41-e10e56d193ec").offerStatus("REVIEW_PENDING").build()
        ));

        // when
        var findAbandonedOffers = loanOfferStoreService.findAnyOfferForEachAbandonedUser(7);

        var expectedAbandonedUsers = Set.of("ff0f26d1-332b-419a-b0ee-b7eec3a72dca",
                "7d4baa6d-bd0a-4ea7-9e41-e10e56d193ec",
                "3704060c-2a3d-40ae-8842-d51660c41937");
        // then
        StepVerifier.create(findAbandonedOffers)
                .consumeNextWith(abandoned -> assertAll(
                        () -> assertEquals(3, abandoned.size()),
                        () -> assertTrue(expectedAbandonedUsers.containsAll(abandoned.stream().map(LoanOfferStore::getUserUUID).collect(toSet())))
                ))
                .verifyComplete();
    }

    @Test
    void getLatestUpdatedOfferGroupedByApplication() {
        // given
        final Set<String> applicationIds = Set.of("applicationId1", "applicationId2", "applicationId3", "applicationId4", "applicationId5", "applicationId6");
        var expectedOfferIds = Set.of("expected1", "expected2", "expected3", "expected4", "expected5", "expected6");
        mongoTemplate.insertAll(List.of(
                LoanOfferStore.builder().loanOfferId("expected1").applicationId("applicationId1")
                        .statusLastUpdateDate(OffsetDateTime.now())
                        .kycStatusLastUpdateDate(OffsetDateTime.now().minusDays(1))
                        .acceptedDate(OffsetDateTime.now().minusDays(1))
                        .insertTS(LocalDateTime.now().minusDays(3)).build(),
                LoanOfferStore.builder().loanOfferId("expected2").applicationId("applicationId2")
                        .kycStatusLastUpdateDate(OffsetDateTime.now().minusDays(5))
                        .acceptedDate(OffsetDateTime.now().minusDays(1))
                        .insertTS(LocalDateTime.now()).build(),
                LoanOfferStore.builder().loanOfferId("expected3").applicationId("applicationId3")
                        .statusLastUpdateDate(OffsetDateTime.now())
                        .acceptedDate(OffsetDateTime.now().minusDays(1))
                        .insertTS(LocalDateTime.now()).build(),
                LoanOfferStore.builder().loanOfferId("expected4").applicationId("applicationId4")
                        .statusLastUpdateDate(OffsetDateTime.now())
                        .kycStatusLastUpdateDate(OffsetDateTime.now().minusDays(1))
                        .acceptedDate(OffsetDateTime.now().minusDays(1))
                        .insertTS(LocalDateTime.now()).build(),
                LoanOfferStore.builder().loanOfferId("expected5").applicationId("applicationId5")
                        .kycStatusLastUpdateDate(OffsetDateTime.now().minusDays(1))
                        .insertTS(LocalDateTime.now()).build(),
                LoanOfferStore.builder().loanOfferId("expected6").applicationId("applicationId6")
                        .kycStatusLastUpdateDate(OffsetDateTime.now().minusDays(1))
                        .insertTS(LocalDateTime.now()).build(),

                LoanOfferStore.builder().loanOfferId("old1").applicationId("applicationId1")
                        .statusLastUpdateDate(OffsetDateTime.now().minusMinutes(5))
                        .kycStatusLastUpdateDate(OffsetDateTime.now())
                        .acceptedDate(OffsetDateTime.now().minusDays(1))
                        .insertTS(LocalDateTime.now().minusDays(5)).build(),
                LoanOfferStore.builder().loanOfferId("old2").applicationId("applicationId2")
                        .kycStatusLastUpdateDate(OffsetDateTime.now().minusDays(10))
                        .acceptedDate(OffsetDateTime.now())
                        .insertTS(LocalDateTime.now().minusDays(5)).build(),
                LoanOfferStore.builder().loanOfferId("old3").applicationId("applicationId3")
                        .acceptedDate(OffsetDateTime.now().minusDays(2))
                        .insertTS(LocalDateTime.now().minusDays(5)).build(),
                LoanOfferStore.builder().loanOfferId("old4").applicationId("applicationId4")
                        .statusLastUpdateDate(OffsetDateTime.now().minusHours(5))
                        .insertTS(LocalDateTime.now().minusDays(5)).build(),
                LoanOfferStore.builder().loanOfferId("old5").applicationId("applicationId5")
                        .kycStatusLastUpdateDate(OffsetDateTime.now().minusDays(2))
                        .acceptedDate(OffsetDateTime.now())
                        .insertTS(LocalDateTime.now().minusDays(5)).build(),
                LoanOfferStore.builder().loanOfferId("old6").applicationId("applicationId6").insertTS(LocalDateTime.now().minusDays(5)).build()
        ));

        // when
        var latestOffersGroupedByApplication = loanOfferStoreService.getLatestUpdatedOffersGroupedByApplication(applicationIds);

        // then
        StepVerifier.create(latestOffersGroupedByApplication)
                .consumeNextWith(latestOffers -> assertAll(
                        () -> assertEquals(6, latestOffers.size()),
                        () -> assertTrue(expectedOfferIds.containsAll(latestOffers.values().stream().map(LoanOfferStore::getLoanOfferId).collect(toSet())))
                )).verifyComplete();
    }
}
