package de.joonko.loan.acceptoffer.domain;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.partner.santander.SantanderApplicationStatusGateway;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static de.joonko.loan.acceptoffer.domain.LoanApplicationStatus.UNDEFINED;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
class LoanApplicationStatusServiceTest {

    private LoanApplicationStatusService loanApplicationStatusService;

    private SantanderApplicationStatusGateway santanderApplicationStatusGateway;

    @BeforeEach
    void setUp() {
        santanderApplicationStatusGateway = mock(SantanderApplicationStatusGateway.class);
        loanApplicationStatusService = new LoanApplicationStatusService(List.of(santanderApplicationStatusGateway));
    }

    @Test
    void getStatus_happyPath(@Random OfferRequest offerRequest) {
        // given
        Bank bank = Bank.SANTANDER;
        offerRequest.setLoanProvider(bank.label);
        final var status = LoanApplicationStatus.PAID_OUT;

        // when
        when(santanderApplicationStatusGateway.getBank()).thenReturn(bank);
        when(santanderApplicationStatusGateway.getStatus(offerRequest)).thenReturn(Mono.just(status));

        final var actualStatus = loanApplicationStatusService.getStatus(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(actualStatus).expectNextMatches(status::equals).verifyComplete(),
                () -> verify(santanderApplicationStatusGateway).getStatus(offerRequest)
        );
    }

    @Test
    void getStatus_errorCase(@Random OfferRequest offerRequest) {
        // given
        Bank bank = Bank.SANTANDER;
        offerRequest.setLoanProvider(bank.label);
        final var status = LoanApplicationStatus.PAID_OUT;

        // when
        when(santanderApplicationStatusGateway.getBank()).thenReturn(bank);
        when(santanderApplicationStatusGateway.getStatus(offerRequest)).thenReturn(Mono.error(new RuntimeException("Exception")));

        final var actualStatus = loanApplicationStatusService.getStatus(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(actualStatus).expectNextMatches(UNDEFINED::equals).verifyComplete(),
                () -> verify(santanderApplicationStatusGateway).getStatus(offerRequest)
        );
    }

}
