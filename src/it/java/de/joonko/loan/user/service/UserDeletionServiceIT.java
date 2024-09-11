package de.joonko.loan.user.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.config.s3.S3ConfigProperties;
import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.db.vo.LoanApplicationAuditTrail;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import de.joonko.loan.integrations.s3.S3Client;
import de.joonko.loan.integrations.s3.contract.S3ContractStorageService;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.partner.consors.PersonalizedCalculationsStore;
import de.joonko.loan.partner.santander.model.SantanderOffer;
import de.joonko.loan.partner.swk.model.SwkCreditApplicationOffer;
import de.joonko.loan.partner.swk.model.SwkOffer;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.webhooks.idnow.model.Identification;
import de.joonko.loan.webhooks.idnow.model.IdentificationProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class})
@SpringBootTest
class UserDeletionServiceIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserDeletionService userDeletionService;

    private S3Client s3Client;
    private AmazonS3Client amazonS3Client;
    private S3ConfigProperties s3Config;
    private ContractStorageService contractStorageService;

    @BeforeEach
    void setUp() {
        // given
        amazonS3Client = mock(AmazonS3Client.class);
        s3Client = new S3Client(amazonS3Client);
        S3ConfigProperties s3Config = mock(S3ConfigProperties.class);

        contractStorageService = new S3ContractStorageService(s3Client, s3Config);
        ReflectionTestUtils.setField(userDeletionService, "contractStorageService", contractStorageService);
    }

    @Test
    void deleteUserData() {

        final var userId = "c4bb3b2d-24ad-47c1-b87f-6fe2f5d61eeb";
        mongoTemplate.insertAll(getLoanDemandStoreList(userId));
        mongoTemplate.insertAll(getPersonalizedCalculations());
        mongoTemplate.insertAll(getSantanderOfferList());
        mongoTemplate.insertAll(getSwkOfferList());
        mongoTemplate.insertAll(getSwkCreditApplicationOfferList());
        mongoTemplate.insertAll(getLoanApplicationAuditTrailList());
        mongoTemplate.insertAll(getLoanApplicationAuditTrailList());
        mongoTemplate.insertAll(getIdentificationLinkList());
        mongoTemplate.insertAll(getIdentificationList());
        mongoTemplate.insertAll(getUserStatesStoreList(userId));
        mongoTemplate.insertAll(getUserAdditionalInfoStoreList(userId));
        mongoTemplate.insertAll(getUserPersonalInfoStoreList(userId));
        mongoTemplate.insertAll(getUserTransactionalDataStoreList(userId));
        mongoTemplate.insertAll(getUserTransactionalDraftDataStoreList(userId));
        mongoTemplate.insertAll(getLoanOfferStoreList(userId));
        mongoTemplate.insertAll(getLoanDemandRequestList(userId));

        when(amazonS3Client.deleteObjects(any(DeleteObjectsRequest.class))).thenReturn(new DeleteObjectsResult(List.of(new DeleteObjectsResult.DeletedObject())));

        // when
        var deletedUserMono = userDeletionService.deleteUser(userId);

        // then
        assertAll(
                () -> StepVerifier.create(deletedUserMono).expectNextCount(0).verifyComplete(),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("userUUID").is(userId)), UserStatesStore.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("applicationId").in("delete123", "delete321")), LoanDemandStore.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("applicationId").in("delete123", "delete321")), PersonalizedCalculationsStore.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("applicationId").in("delete123", "delete321")), SantanderOffer.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("applicationId").in("delete123", "delete321")), SwkOffer.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("applicationId").in("delete123", "delete321")), LoanDemandStore.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("applicationId").in("delete123", "delete321")), LoanApplicationAuditTrail.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("applicationId").in("delete123", "delete321")), IdentificationLink.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("identificationProcess.transactionNumber").in("externalIdent123", "externalIdent321")), Identification.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("userUUID").is(userId)), UserAdditionalInformationStore.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("userUUID").is(userId)), UserPersonalInformationStore.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("userUUID").is(userId)), UserTransactionalDataStore.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("userUUID").is(userId)), UserTransactionalDraftDataStore.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("userUUID").is(userId)), LoanOfferStore.class).isEmpty()),
                () -> assertTrue(mongoTemplate.find(new Query(Criteria.where("userUUID").is(userId)), LoanDemandRequest.class).isEmpty())
        );
    }

    private List<LoanDemandStore> getLoanDemandStoreList(String userId) {
        return List.of(
                LoanDemandStore.builder().userUUID(userId).applicationId("delete123").build(),
                LoanDemandStore.builder().userUUID(userId).applicationId("delete321").build(),
                LoanDemandStore.builder().userUUID("ignore").applicationId("ignore123").build()
        );
    }

    private List<PersonalizedCalculationsStore> getPersonalizedCalculations() {
        return List.of(
                PersonalizedCalculationsStore.builder().applicationId("ignore123").build(),
                PersonalizedCalculationsStore.builder().applicationId("delete123").build(),
                PersonalizedCalculationsStore.builder().applicationId("delete123").build()
        );
    }

    private List<SantanderOffer> getSantanderOfferList() {
        return List.of(
                SantanderOffer.builder().applicationId("delete123").build(),
                SantanderOffer.builder().applicationId("delete321").build(),
                SantanderOffer.builder().applicationId("ignore123").build()
        );
    }

    private List<SwkOffer> getSwkOfferList() {
        return List.of(
                SwkOffer.builder().applicationId("delete123").build(),
                SwkOffer.builder().applicationId("delete321").build(),
                SwkOffer.builder().applicationId("ignore123").build()
        );
    }

    private List<SwkCreditApplicationOffer> getSwkCreditApplicationOfferList() {
        return List.of(
                SwkCreditApplicationOffer.builder().applicationId("delete123").build(),
                SwkCreditApplicationOffer.builder().applicationId("delete321").build(),
                SwkCreditApplicationOffer.builder().applicationId("ignore123").build()
        );
    }

    private List<LoanApplicationAuditTrail> getLoanApplicationAuditTrailList() {
        return List.of(
                LoanApplicationAuditTrail.builder().applicationId("delete123").build(),
                LoanApplicationAuditTrail.builder().applicationId("delete321").build(),
                LoanApplicationAuditTrail.builder().applicationId("ignore123").build()
        );
    }

    private List<IdentificationLink> getIdentificationLinkList() {
        return List.of(
                IdentificationLink.builder().applicationId("delete123").externalIdentId("externalIdent123").build(),
                IdentificationLink.builder().applicationId("delete321").externalIdentId("externalIdent321").build(),
                IdentificationLink.builder().applicationId("ignore123").build()
        );
    }

    private List<Identification> getIdentificationList() {
        return List.of(
            Identification.builder().identificationProcess(IdentificationProcess.builder().transactionNumber("externalIdent123").build()).build(),
            Identification.builder().identificationProcess(IdentificationProcess.builder().transactionNumber("externalIdent321").build()).build(),
            Identification.builder().identificationProcess(IdentificationProcess.builder().transactionNumber("ignore123").build()).build()
        );
    }

    private List<UserStatesStore> getUserStatesStoreList(String userId) {
        var userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(userId);
        var userStatesStore2 = new UserStatesStore();
        userStatesStore2.setUserUUID("ignore");

        return List.of(userStatesStore, userStatesStore2);
    }

    private List<UserAdditionalInformationStore> getUserAdditionalInfoStoreList(String userId) {
        var userAdditionalInfo = new UserAdditionalInformationStore();
        userAdditionalInfo.setUserUUID(userId);
        var userAdditionalInfo2 = new UserAdditionalInformationStore();
        userAdditionalInfo.setUserUUID("ignore");

        return List.of(userAdditionalInfo, userAdditionalInfo2);
    }

    private List<UserPersonalInformationStore> getUserPersonalInfoStoreList(String userId) {
        var userPersonalInfo = new UserPersonalInformationStore();
        userPersonalInfo.setUserUUID(userId);
        var userPersonalInfo2 = new UserPersonalInformationStore();
        userPersonalInfo.setUserUUID("ignore");

        return List.of(userPersonalInfo, userPersonalInfo2);
    }

    private List<UserTransactionalDataStore> getUserTransactionalDataStoreList(String userId) {
        var userTransactionalData = new UserTransactionalDataStore();
        userTransactionalData.setUserUUID(userId);
        var userTransactionalData2 = new UserTransactionalDataStore();
        userTransactionalData.setUserUUID("ignore");

        return List.of(userTransactionalData, userTransactionalData2);
    }

    private List<UserTransactionalDraftDataStore> getUserTransactionalDraftDataStoreList(String userId) {
        var userTransactionalDraftDataStore = new UserTransactionalDraftDataStore();
        userTransactionalDraftDataStore.setUserUUID(userId);
        var userTransactionalDraftDataStore1 = new UserTransactionalDraftDataStore();
        userTransactionalDraftDataStore.setUserUUID("ignore");

        return List.of(userTransactionalDraftDataStore, userTransactionalDraftDataStore1);
    }

    private List<LoanOfferStore> getLoanOfferStoreList(String userId) {
        return List.of(
                LoanOfferStore.builder().userUUID(userId).build(),
                LoanOfferStore.builder().userUUID(userId).build(),
                LoanOfferStore.builder().userUUID(userId).contracts(List.of(
                        DocumentDetails.builder().name("agreement").key("key1").build(),
                        DocumentDetails.builder().name("schedule").key("key2").build()
                )).build(),
                LoanOfferStore.builder().userUUID(userId).contracts(List.of(
                        DocumentDetails.builder().name("agreement").key("key3").build(),
                        DocumentDetails.builder().name("schedule").key("key4").build()
                )).build(),
                LoanOfferStore.builder().userUUID("ignore").build()
        );
    }

    private List<LoanDemandRequest> getLoanDemandRequestList(String userId) {
        return List.of(
                LoanDemandRequest.builder().userUUID(userId).build(),
                LoanDemandRequest.builder().userUUID(userId).build(),
                LoanDemandRequest.builder().userUUID("ignore").build()
        );
    }
}
