package de.joonko.loan.partner.santander;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SantanderLoanProviderApiMapperTest extends BaseMapperTest {

    @Autowired
    SantanderLoanProviderApiMapper loanProviderApiMapper;

    @ParameterizedTest
    @EnumSource(
            value = FamilyStatus.class,
            names = {"MARRIED", "LIVING_IN_LONGTERM_RELATIONSHIP"},
            mode = EnumSource.Mode.INCLUDE)
    void toLoanProviderRequest_whenMarried_shouldReturn2AdultsInHousehold(final FamilyStatus familyStatus, final @Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails().setFamilyStatus(familyStatus);
        final var loanProviderRequest = loanProviderApiMapper.toLoanProviderRequest(loanDemand, LoanDuration.TWELVE);
        assertEquals(2, loanProviderRequest.getGetKreditvertragsangebot().getKreditantrag().getDarlehnsnehmer().getErwachseneImHaushalt().intValue());
    }

    @ParameterizedTest
    @EnumSource(
            value = FamilyStatus.class,
            names = {"MARRIED", "LIVING_IN_LONGTERM_RELATIONSHIP"},
            mode = EnumSource.Mode.EXCLUDE)
    void toLoanProviderRequest_whenAlone_shouldReturn1AdultInHousehold(final FamilyStatus familyStatus, final @Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails().setFamilyStatus(familyStatus);
        final var loanProviderRequest = loanProviderApiMapper.toLoanProviderRequest(loanDemand, LoanDuration.TWELVE);
        assertEquals(1, loanProviderRequest.getGetKreditvertragsangebot().getKreditantrag().getDarlehnsnehmer().getErwachseneImHaushalt().intValue());
    }

    @Test
    void fromLoanProviderResponse() {
        // given

        final var givenList = List.of(getKreditvertragsangebotResponse());

        // when
        final var mappedList = loanProviderApiMapper.fromLoanProviderResponse(givenList);

        // then
        assertAll(
                () -> assertEquals("38fh9283", mappedList.get(0).getLoanProviderOfferId()),
                () -> assertEquals(Bank.SANTANDER.getLabel(), mappedList.get(0).getLoanProvider().getName())
        );
    }

    private ScbCapsBcoWSStub.GetKreditvertragsangebotResponse getKreditvertragsangebotResponse() {
        ScbCapsBcoWSStub.GetKreditvertragsangebotResponse response = new ScbCapsBcoWSStub.GetKreditvertragsangebotResponse();
        ScbCapsBcoWSStub.GetKreditvertragsangebotResult getKreditvertragsangebotResult = new ScbCapsBcoWSStub.GetKreditvertragsangebotResult();
        ScbCapsBcoWSStub.KreditantragsstatusXO kreditantragsstatusXO = new ScbCapsBcoWSStub.KreditantragsstatusXO();
        kreditantragsstatusXO.setScbAntragId("38fh9283");
        ScbCapsBcoWSStub.FinanzierungXO finanzierungXO = new ScbCapsBcoWSStub.FinanzierungXO();
        finanzierungXO.setKreditbetragNetto(BigDecimal.ONE);
        finanzierungXO.setLaufzeitInMonaten(BigInteger.ONE);
        finanzierungXO.setEffektivzinsPaProz(BigDecimal.ONE);
        finanzierungXO.setRateneinzugZum(ScbCapsBcoWSStub.RateneinzugType.ERSTER_EINES_MONATS);
        finanzierungXO.setKreditbetragGesamt(BigDecimal.ONE);
        kreditantragsstatusXO.setFinanzierung(finanzierungXO);
        getKreditvertragsangebotResult.setAntragsstatus(kreditantragsstatusXO);
        response.setGetKreditvertragsangebotResponse(getKreditvertragsangebotResult);

        return response;
    }
}
