package de.joonko.loan.partner.santander.testData;

import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import de.joonko.loan.util.DateUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

public class SantanderLoanDemandGatewayTestData {

    public Optional<LoanDemandStore> getLoanDemandStore() {
        return Optional.of(LoanDemandStore.builder().ftsTransactionId("ftsTransactionId").build());
    }

    public ScbCapsBcoWSStub.GetKreditvertragsangebotResponse getKreditvertragsangebotResponse(ScbCapsBcoWSStub.AntragstatusType antragstatusType) {
        ScbCapsBcoWSStub.GetKreditvertragsangebotResponse kreditvertragsangebotResponse = new ScbCapsBcoWSStub.GetKreditvertragsangebotResponse();
        ScbCapsBcoWSStub.GetKreditvertragsangebotResult kreditvertragsangebotResult = new ScbCapsBcoWSStub.GetKreditvertragsangebotResult();
        ScbCapsBcoWSStub.KreditantragsstatusXO kreditantragsstatusXO = new ScbCapsBcoWSStub.KreditantragsstatusXO();
        kreditantragsstatusXO.setScbAntragId("antragId");
        kreditantragsstatusXO.setStatus(antragstatusType);
        ScbCapsBcoWSStub.FinanzierungXO finanzierungXO = new ScbCapsBcoWSStub.FinanzierungXO();
        finanzierungXO.setLaufzeitInMonaten(BigInteger.valueOf(24));
        kreditantragsstatusXO.setFinanzierung(finanzierungXO);
        kreditvertragsangebotResult.setAntragsstatus(kreditantragsstatusXO);
        kreditvertragsangebotResponse.setGetKreditvertragsangebotResponse(kreditvertragsangebotResult);

        return kreditvertragsangebotResponse;
    }

    public ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot(int loanAmount, LocalDate localDate) {
        ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot = new ScbCapsBcoWSStub.GetKreditvertragsangebot();
        ScbCapsBcoWSStub.GetKreditvertragsangebotParams params = new ScbCapsBcoWSStub.GetKreditvertragsangebotParams();
        ScbCapsBcoWSStub.KreditantragXO kreditantragXO = new ScbCapsBcoWSStub.KreditantragXO();
        ScbCapsBcoWSStub.FinanzierungXO finanzierungXO = new ScbCapsBcoWSStub.FinanzierungXO();
        finanzierungXO.setLaufzeitInMonaten(BigInteger.valueOf(24));
        finanzierungXO.setKreditbetragNetto(BigDecimal.valueOf(loanAmount));
        kreditantragXO.setFinanzierung(finanzierungXO);
        ScbCapsBcoWSStub.DarlehnsnehmerXO darlehnsnehmerXO = new ScbCapsBcoWSStub.DarlehnsnehmerXO();
        ScbCapsBcoWSStub.BeschaeftigungsverhaeltnisXO beschaeftigungsverhaeltnisXO = new ScbCapsBcoWSStub.BeschaeftigungsverhaeltnisXO();
        beschaeftigungsverhaeltnisXO.setBeschaeftigtBis(DateUtil.toDate(localDate));
        darlehnsnehmerXO.setAktuellesBV(beschaeftigungsverhaeltnisXO);
        kreditantragXO.setDarlehnsnehmer(darlehnsnehmerXO);
        params.setKreditantrag(kreditantragXO);
        getKreditvertragsangebot.setGetKreditvertragsangebot(params);
        return getKreditvertragsangebot;
    }
}
