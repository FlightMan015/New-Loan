package de.joonko.loan.db.service;

import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.util.EncrDecrService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("integration")
@SpringBootTest
@ExtendWith(RandomBeansExtension.class)
class LoanDemandStoreServiceTest {

    @Autowired
    private LoanDemandStoreService loanDemandStoreService;

    @Autowired
    private EncrDecrService encrDecrService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void save_loan_demand(@Random LoanDemandRequest loanDemandRequest) {
        // given
        String userUuid = UUID.randomUUID().toString();

        // when
        LoanDemandStore loanDemandStore = loanDemandStoreService.saveLoanDemand(loanDemandRequest, true, userUuid);

        // then
        LoanDemandStore loanDemandStoreFromDB = mongoTemplate.findById(loanDemandStore.getApplicationId(), LoanDemandStore.class);

        assertAll(
                () -> assertNotNull(loanDemandStore),
                () -> assertNotNull(loanDemandStoreFromDB),
                () -> assertEquals(loanDemandRequest.getDacId(), loanDemandStoreFromDB.getDacId()),
                () -> assertEquals(userUuid, loanDemandStoreFromDB.getUserUUID()),
                () -> assertNotNull(loanDemandRequest.getFtsTransactionId(), loanDemandStoreFromDB.getFtsTransactionId()),
                () -> assertNotNull(loanDemandStoreFromDB.getUpdateTs()),
                () -> assertTrue(loanDemandStoreFromDB.getInternalUse()),
                () -> assertEquals(loanDemandRequest.getPersonalDetails().getFirstName(), encrDecrService.deAnonymize(loanDemandStoreFromDB.getFirstName())),
                () -> assertEquals(loanDemandRequest.getPersonalDetails().getLastName(), encrDecrService.deAnonymize(loanDemandStoreFromDB.getLastName())),
                () -> assertEquals(loanDemandRequest.getContactData().getEmail(), encrDecrService.deAnonymize(loanDemandStoreFromDB.getEmailId())),
                () -> assertEquals(loanDemandStore.getApplicationId(), loanDemandStoreFromDB.getExternalIdentifiers().getAuxmoneyExternalIdentifier()),
                () -> assertEquals(loanDemandStore.getApplicationId().substring(0, 23), loanDemandStoreFromDB.getExternalIdentifiers().getSwkExternalIdentifier()),
                () -> assertEquals(loanDemandStore.getApplicationId().substring(0, 20), loanDemandStoreFromDB.getExternalIdentifiers().getConsorsExternalIdentifier())
        );
    }

    @Test
    void find_loan_demand_by_id() {
        // given
        LoanDemandStore loanDemandStore = LoanDemandStore.builder().build();
        loanDemandStore = mongoTemplate.save(loanDemandStore);

        // when
        Optional<LoanDemandStore> optionalLoanDemand = loanDemandStoreService.findById(loanDemandStore.getApplicationId());

        // then
        assertTrue(optionalLoanDemand.isPresent());
    }

    @Test
    void do_not_find_loan_demand_by_id() {
        // given
        String applicationId = "123";

        // when
        Optional<LoanDemandStore> optionalLoanDemand = loanDemandStoreService.findById(applicationId);

        // then
        assertFalse(optionalLoanDemand.isPresent());
    }

    @Test
    void get_dac_id_by_application_id() {
        // given
        String expectedDacId = "321";
        LoanDemandStore loanDemandStore = LoanDemandStore.builder().dacId(expectedDacId).build();
        loanDemandStore = mongoTemplate.save(loanDemandStore);

        // when
        String dacId = loanDemandStoreService.getDacId(loanDemandStore.getApplicationId());

        // then
        assertEquals(expectedDacId, dacId);
    }

    @Test
    void get_empty_dac_id_when_missing() {
        // given
        String applicationId = "123";

        // when
        String dacId = loanDemandStoreService.getDacId(applicationId);

        // then
        assertEquals("", dacId);
    }

    @Test
    void delete_by_application_id() {
        // given
        final var applicationId = "delete123";
        mongoTemplate.insertAll(getTestData());

        // when
        var deletedDocumentMono = loanDemandStoreService.deleteById(applicationId);

        // then
        assertAll(
                () -> StepVerifier.create(deletedDocumentMono).expectNextCount(1).verifyComplete(),
                () -> assertTrue(findByApplicationId(applicationId).isEmpty()),
                () -> assertEquals(1, findByApplicationId("ignore123").size())
        );
    }

    @Test
    void empty_when_delete_by_application_id() {
        // given
        final var applicationId = "notFound123";

        // when
        var deletedDocumentMono = loanDemandStoreService.deleteById(applicationId);

        // then
        StepVerifier.create(deletedDocumentMono).expectNextCount(0).verifyComplete();
    }

    private List<LoanDemandStore> findByApplicationId(String applicationId) {
        var findQuery = new Query(Criteria.where("applicationId").is(applicationId));
        return mongoTemplate.find(findQuery, LoanDemandStore.class);
    }

    private List<LoanDemandStore> getTestData() {
        return List.of(
                LoanDemandStore.builder().applicationId("delete123").build(),
                LoanDemandStore.builder().applicationId("ignore123").build()
        );
    }
}
