package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SantanderGetOfferResponseMapper {

    default List<LoanOffer> toLoanProviderResponse(List<ScbCapsBcoWSStub.GetKreditvertragsangebotResponse> response) {

        return response.stream()
                .filter(res ->
                        (null != res &&
                                null != res.getGetKreditvertragsangebotResponse() &&
                                null != res.getGetKreditvertragsangebotResponse().getAntragsstatus() &&
                                null != res.getGetKreditvertragsangebotResponse().getAntragsstatus().getFinanzierung())
                ).map(this::toSingleOffer)
                .collect(Collectors.toList());

    }

    private LoanOffer toSingleOffer(ScbCapsBcoWSStub.GetKreditvertragsangebotResponse santanderResponse) {
        ScbCapsBcoWSStub.FinanzierungXO santanderOffer = santanderResponse.getGetKreditvertragsangebotResponse().getAntragsstatus().getFinanzierung();

        return LoanOffer.builder()
                .amount(santanderOffer.getKreditbetragNetto().intValue())
                .durationInMonth(santanderOffer.getLaufzeitInMonaten().intValue())
                .effectiveInterestRate(santanderOffer.getEffektivzinsPaProz())
                .nominalInterestRate(santanderOffer.getNominalzinsPaProz())
                .monthlyRate(santanderOffer.getRatenbetragMonatl())
                .totalPayment(santanderOffer.getKreditbetragGesamt())
                .loanProvider(new LoanProvider(Bank.SANTANDER.label))
                .loanProviderOfferId(santanderResponse.getGetKreditvertragsangebotResponse().getAntragsstatus().getScbAntragId())
                .build();
    }
}

