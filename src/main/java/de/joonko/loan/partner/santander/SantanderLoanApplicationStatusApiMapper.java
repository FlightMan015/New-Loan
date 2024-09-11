package de.joonko.loan.partner.santander;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatusApiMapper;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import org.springframework.stereotype.Component;

import java.util.Set;

import static de.joonko.loan.acceptoffer.domain.LoanApplicationStatus.*;
import static de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.AntragstatusType.*;

@Component
public class SantanderLoanApplicationStatusApiMapper implements LoanApplicationStatusApiMapper<ScbCapsBcoWSStub.GetKreditantragsstatusParams, ScbCapsBcoWSStub.GetKreditantragsstatusResult> {

    @Override
    public ScbCapsBcoWSStub.GetKreditantragsstatusParams toLoanApplicationStatusRequest(OfferRequest offerRequest) {
        return new ScbCapsBcoWSStub.GetKreditantragsstatusParams();
    }

    @Override
    public LoanApplicationStatus fromLoanApplicationStatusResponse(ScbCapsBcoWSStub.GetKreditantragsstatusResult response) {
        if (ABGESCHLOSSEN.equals(response.getAntragsstatus().getStatus())) {
            return PAID_OUT;
        } else if (ABGELEHNT.equals(response.getAntragsstatus().getStatus())) {
            return REJECTED;
        } else if (STORNIERT.equals(response.getAntragsstatus().getStatus())) {
            return CANCELED;
        } else if (GENEHMIGT.equals(response.getAntragsstatus().getStatus())) {
            return OFFER_ACCEPTED;
        } else if (Set.of(IN_BEARBEITUNG, ZURUECKGESTELLT_SONSTIGES).contains(response.getAntragsstatus().getStatus())) {
            return PENDING;
        } else {
            return UNDEFINED;
        }
    }
}
