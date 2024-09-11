package de.joonko.loan.partner.swk;

import de.joonko.loan.offer.domain.LoanDemand;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.rmi.RemoteException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
@ActiveProfiles("integration")
@SpringBootTest
class SwkLoanDemandGatewayTest {

    @Autowired
    SwkLoanDemandGateway swkLoanDemandGateway;

    @MockBean
    private SwkPrecheckFilter precheckFilter;

    @Test
    void filterGateway(@Random LoanDemand loanDemand) throws RemoteException {
        // given
        when(precheckFilter.test(loanDemand)).thenReturn(false);

        // when
        swkLoanDemandGateway.filterGateway(loanDemand);

        // then
        verify(precheckFilter).test(any(LoanDemand.class));
    }
}
