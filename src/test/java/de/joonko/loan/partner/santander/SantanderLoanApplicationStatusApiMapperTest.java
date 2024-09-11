package de.joonko.loan.partner.santander;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@ExtendWith(RandomBeansExtension.class)
class SantanderLoanApplicationStatusApiMapperTest {

    private SantanderLoanApplicationStatusApiMapper santanderLoanApplicationStatusApiMapper;

    @BeforeEach
    void setUp() {
        santanderLoanApplicationStatusApiMapper = new SantanderLoanApplicationStatusApiMapper();
    }

    public static class CustomArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {

            return Stream.of(
                    arguments(ScbCapsBcoWSStub.AntragstatusType.ABGESCHLOSSEN, LoanApplicationStatus.PAID_OUT),
                    arguments(ScbCapsBcoWSStub.AntragstatusType.GENEHMIGT, LoanApplicationStatus.OFFER_ACCEPTED),
                    arguments(ScbCapsBcoWSStub.AntragstatusType.STORNIERT, LoanApplicationStatus.CANCELED),
                    arguments(ScbCapsBcoWSStub.AntragstatusType.ABGELEHNT, LoanApplicationStatus.REJECTED),
                    arguments(ScbCapsBcoWSStub.AntragstatusType.ZURUECKGESTELLT_SONSTIGES, LoanApplicationStatus.PENDING),
                    arguments(ScbCapsBcoWSStub.AntragstatusType.IN_BEARBEITUNG, LoanApplicationStatus.PENDING),
                    arguments(ScbCapsBcoWSStub.AntragstatusType.ZURUECKGESTELLT_DOKUMENTE_ERHALTEN, LoanApplicationStatus.UNDEFINED),
                    arguments(ScbCapsBcoWSStub.AntragstatusType.ZURUECKGESTELLT_DOKUMENTE_FEHLEND, LoanApplicationStatus.UNDEFINED)
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(CustomArgumentProvider.class)
    void fromLoanApplicationStatusResponse(ScbCapsBcoWSStub.AntragstatusType santanderStatus, LoanApplicationStatus expectedMappedStatus) {
        // given
        final var response = new ScbCapsBcoWSStub.GetKreditantragsstatusResult();
        response.setAntragsstatus(new ScbCapsBcoWSStub.KreditantragsstatusXO());
        response.getAntragsstatus().setStatus(santanderStatus);

        // when
        final var actualStatus = santanderLoanApplicationStatusApiMapper.fromLoanApplicationStatusResponse(response);

        // then
        assertEquals(expectedMappedStatus, actualStatus);
    }

}