package de.joonko.loan.partner.swk;

import com.github.tomakehurst.wiremock.WireMockServer;
import de.joonko.loan.common.WireMockInitializer;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.partner.solaris.SolarisAcceptOfferGateway;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
@SpringBootTest
@ActiveProfiles("integration")
@ContextConfiguration(initializers = WireMockInitializer.class)
class SwkAcceptOfferGatewayTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WireMockServer mockServer;

    @Autowired
    private SwkAcceptOfferGateway swkAcceptOfferGateway;

    @MockBean
    private SwkStoreService swkStoreService;

    @MockBean
    private CreditApplicationServiceStub creditApplicationServiceStub;

    @Test
    void offerNotProceed() throws RemoteException, RemoteException {
        // given
        var applicationId = "8327565";
        var offerId = "98565";
        var applyForCredit = new CreditApplicationServiceStub.ApplyForCredit();
        var applicationRequest = new CreditApplicationServiceStub.ApplicationRequest();

        CreditApplicationServiceStub.Property property = new CreditApplicationServiceStub.Property();
        CreditApplicationServiceStub.Property[] properties = new CreditApplicationServiceStub.Property[1];
        properties[0] = property;

        applicationRequest.setExtraInfo(properties);
        applyForCredit.setRequest(applicationRequest);

        var swkAcceptOfferRequest = SwkAcceptOfferRequest.builder()
                .offerId(offerId)
                .applyForCredit(applyForCredit)
                .build();
        when(swkStoreService.getCustomerNumber(anyString(), anyInt())).thenReturn("");

        LoanOfferStore offerStore = LoanOfferStore.builder()
                .loanOfferId(offerId)
                .offer(LoanOffer.builder().durationInMonth(1).build())
                .build();
        mongoTemplate.insert(offerStore);

        // when
        Executable response = () -> swkAcceptOfferGateway.callApi(swkAcceptOfferRequest, applicationId, offerId);

        // then
        assertThrows(RuntimeException.class, response);
    }
}