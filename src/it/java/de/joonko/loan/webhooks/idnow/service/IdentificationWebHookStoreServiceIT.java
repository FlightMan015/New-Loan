package de.joonko.loan.webhooks.idnow.service;

import de.joonko.loan.webhooks.idnow.model.Identification;
import de.joonko.loan.webhooks.idnow.model.IdentificationProcess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class})
@SpringBootTest
class IdentificationWebHookStoreServiceIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdentificationWebHookStoreService identificationWebHookStoreService;

    @Test
    void deleteAllByTransactionNumber() {
        // given
        final var transactionNumber = "delete123";
        mongoTemplate.insertAll(getTestData());

        // when
        var deletedDocumentsMono = identificationWebHookStoreService.deleteByTransactionNumber(transactionNumber);

        // then
        assertAll(
                () -> StepVerifier.create(deletedDocumentsMono).consumeNextWith(deletedDocuments ->
                        assertEquals(2, deletedDocuments)).verifyComplete(),
                () -> assertTrue(findByTransactionNumber(transactionNumber).isEmpty()),
                () -> assertEquals(1, findByTransactionNumber("any123").size())
        );
    }

    private List<Identification> findByTransactionNumber(String transactionNumber) {
        var deleteQuery = new Query(Criteria.where("identificationProcess.transactionNumber").is(transactionNumber));
        return mongoTemplate.find(deleteQuery, Identification.class);
    }

    private List<Identification> getTestData() {
        return List.of(
                Identification.builder()
                        .identificationProcess(IdentificationProcess.builder()
                                .transactionNumber("any123").build()).build(),
                Identification.builder()
                        .identificationProcess(IdentificationProcess.builder()
                                .transactionNumber("delete123").build()).build(),
                Identification.builder()
                        .identificationProcess(IdentificationProcess.builder()
                                .transactionNumber("delete123").build()).build()
        );
    }
}
