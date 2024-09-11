package de.joonko.loan.partner.santander;

import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SantanderAcceptOfferRequest {
    private LoanDuration duration;
    private ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot;
}