package de.joonko.loan.identification.service.webid;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.mapper.webid.SantanderCreateUserActionRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.service.IdentificationAuditService;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.partner.santander.SantanderContractGateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SantanderIdentServiceTest {

    @InjectMocks
    private SantanderIdentService santanderIdentService;

    @Mock
    private WebClient webClient;
    @Mock
    private SantanderCreateUserActionRequestMapper santanderCreateUserActionRequestMapper;
    @Mock
    private IdentificationAuditService identificationAuditService;
    @Mock
    private IdentificationLinkService identificationLinkService;
    @Mock
    private LoanOfferStoreService loanOfferStoreService;
    @Mock
    private SantanderContractGateway santanderContractGateway;

    @Test
    void should_create_santander_webid_ident() {
//        Mono<CreateIdentResponse> ident = santanderIdentService.createIdent(buildCreateIdentRequest());
    }

    @Test
    void fetch_valid_contract() {
        // given
        LoanOfferStore loanOfferStore = LoanOfferStore.builder().loanProviderReferenceNumber("13237702").build();
        when(loanOfferStoreService.findByLoanOfferId(anyString())).thenReturn(loanOfferStore);
        when(santanderContractGateway.fetchContract(anyString(), anyString(), any(), anyBoolean())).thenReturn(Mono.just(new byte[]{}));
        CreateIdentRequest createIdentRequest = buildCreateIdentRequest();

        // when
        var contract = santanderIdentService.fetchContract(createIdentRequest, null);

        // then
        StepVerifier.create(contract).expectNextCount(1).verifyComplete();
    }

    @Test
    void do_not_fetch_contract_when_loan_offer_not_found() {
        // given
        when(loanOfferStoreService.findByLoanOfferId(anyString())).thenThrow(RuntimeException.class);
        CreateIdentRequest createIdentRequest = buildCreateIdentRequest();

        // when
        var contract = santanderIdentService.fetchContract(createIdentRequest, null);

        // then
        assertAll(
                () -> StepVerifier.create(contract).verifyError(),
                () -> verifyNoInteractions(santanderContractGateway)
        );
    }

    public CreateIdentRequest buildCreateIdentRequest() {
        return CreateIdentRequest.builder()
                .applicationId("5f4657f9180ec17c5f9463d9")
                .loanOfferId("53461822180ec17c5f946bdc")
                .birthday("1989-02-21")
                .country("DE")
                .firstName("Rashmin")
                .gender("MALE")
                .city("Berlin")
                .lastName("Javiya")
                .zipCode("10779")
                .birthplace("Berlin")
                .nationality("DE")
                .street("Handjery")
                .houseNumber("2")
                .mobilePhone("+491234567890")
                .email("rashmin.javiya@joonko.io")
                .loanProvider("SANTANDER")
                .language("DE")
                .duration(12)
                .build();
    }
}