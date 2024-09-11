package de.joonko.loan.offer.api;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.partner.fake.FakeLoanDemandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoanProvidersServiceTest {

    private LoanProvidersService service;

    private static final String APPLICATION_ID = "5f561054e3fa3d3d1a7ef3f4";

    private List<LoanDemandGateway> gateways;
    private LoanOfferStoreService loanOfferStoreService;
    private GetOffersConfigurations getOffersConfigurations;

    @BeforeEach
    void setUp() {
        gateways = List.of(new FakeLoanDemandGateway(null));
        loanOfferStoreService = mock(LoanOfferStoreService.class);
        getOffersConfigurations = mock(GetOffersConfigurations.class);
        service = new LoanProvidersService(gateways, loanOfferStoreService, getOffersConfigurations);
    }

    @Test
    void getLoanOffersProviders() {
        // given
        List<LoanOfferStore> offerStoreList = List.of(LoanOfferStore.builder()
                .offer(LoanOffer.builder()
                        .loanProvider(new LoanProvider("Consors Finanz"))
                        .build())
                .build());
        when(loanOfferStoreService.findAllByLoanApplicationId(APPLICATION_ID)).thenReturn(offerStoreList);

        // when
        List<String> actualLoanOffersProviders = service.getLoanOffersProviders(APPLICATION_ID);

        // then
        assertEquals("Consors Finanz", actualLoanOffersProviders.get(0));
    }

    @Test
    void getActiveLoanProviders() {
        // given

        // when
        var activeLoanProviders = service.getActiveLoanProviders();

        // then
        StepVerifier.create(activeLoanProviders)
                .expectNext("FakeLoanProvider")
                .verifyComplete();
    }
}
