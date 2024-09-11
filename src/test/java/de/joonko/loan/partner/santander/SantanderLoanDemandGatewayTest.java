package de.joonko.loan.partner.santander;

import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import de.joonko.loan.partner.santander.testData.SantanderLoanDemandGatewayTestData;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SantanderLoanDemandGatewayTest {

    private SantanderBaseLoanDemandGateway santanderLoanDemandGateway;

    private SantanderLoanProviderApiMapper mapper;
    private SantanderStoreService santanderStoreService;
    private LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private DataSupportService dataSupportService;
    private LoanDemandStoreService loanDemandStoreService;
    private FTSAccountSnapshotGateway ftsAccountSnapshotGateway;
    private SantanderPrecheckFilter precheckFilter;
    private SantanderClientApi santanderClientApi;

    private SantanderLoanDemandGatewayTestData testData;

    private static final String APPLICATION_ID = "applicationId";

    @BeforeEach
    void setUp() {
        mapper = mock(SantanderLoanProviderApiMapper.class);
        santanderStoreService = mock(SantanderStoreService.class);
        loanApplicationAuditTrailService = mock(LoanApplicationAuditTrailService.class);
        dataSupportService = mock(DataSupportService.class);
        loanDemandStoreService = mock(LoanDemandStoreService.class);
        ftsAccountSnapshotGateway = mock(FTSAccountSnapshotGateway.class);
        precheckFilter = mock(SantanderPrecheckFilter.class);
        santanderClientApi = mock(SantanderClientApi.class);

        testData = new SantanderLoanDemandGatewayTestData();

        santanderLoanDemandGateway = new SantanderLoanDemandGateway(mapper, santanderStoreService, loanApplicationAuditTrailService,
                dataSupportService, loanDemandStoreService, ftsAccountSnapshotGateway, precheckFilter, santanderClientApi);
    }

    @Test
    void filterGateway() {
        // given
        LoanDemand loanDemand = mock(LoanDemand.class);
        when(precheckFilter.test(loanDemand)).thenReturn(true);

        // when
        santanderLoanDemandGateway.filterGateway(loanDemand);

        // then
        verify(precheckFilter).test(any(LoanDemand.class));
    }

    @SneakyThrows
    @Test
    void getOffersFromSantander() {
        // given
        int loanAmount = 6000;
        LocalDate professionEndDate = LocalDate.now().plusYears(3);
        ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot = testData.getKreditvertragsangebot(loanAmount, professionEndDate);
        ScbCapsBcoWSStub.GetKreditvertragsangebotResponse kreditvertragsangebotResponse = testData.getKreditvertragsangebotResponse(ScbCapsBcoWSStub.AntragstatusType.GENEHMIGT);
        when(loanDemandStoreService.findById(APPLICATION_ID)).thenReturn(testData.getLoanDemandStore());
        when(santanderClientApi.getOffer(getKreditvertragsangebot)).thenReturn(kreditvertragsangebotResponse);

        // when
        var actualOffers = santanderLoanDemandGateway.callApi(getKreditvertragsangebot, APPLICATION_ID);

        // then
        StepVerifier.create(actualOffers)
                .consumeNextWith(response -> assertEquals(1, response.size()))
                .verifyComplete();
    }


}
