package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import de.joonko.loan.partner.auxmoney.model.LoanDuration;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuxmoneyGetOffersResponseMapperTest extends BaseMapperTest {

    @Autowired
    private AuxmoneyGetOffersResponseMapper getOffersResponseMapper;

    @Random
    private AuxmoneySingleCallResponse auxmoneySingleCallResponse;


    @Test
    @DisplayName("Should map Auxmoney amount to Loan asked")
    void amount() {
        auxmoneySingleCallResponse.setLoanAsked(4200);
        List<LoanOffer> offers = getOffersResponseMapper.fromAuxmoneyResponse(List.of(auxmoneySingleCallResponse));
        assertEquals(1, offers.size());
        assertEquals(4200, offers.get(0)
                .getAmount());
    }

    @Test
    @DisplayName("Should map Auxmoney duration to Offer.duration")
    void duration() {
        auxmoneySingleCallResponse.setDuration(LoanDuration.TWELVE.getValue());
        List<LoanOffer> offers = getOffersResponseMapper.fromAuxmoneyResponse(List.of(auxmoneySingleCallResponse));
        assertEquals(1, offers.size());
        assertEquals(12, offers.get(0)
                .getDurationInMonth());

    }

    @Test
    @DisplayName("Should map Auxmoney EffRate to Offer.effectiveInterestRate")
    void effectiveInterestRate() {

        auxmoneySingleCallResponse.setEffRate(12.22);
        List<LoanOffer> offers = getOffersResponseMapper.fromAuxmoneyResponse(List.of(auxmoneySingleCallResponse));
        assertEquals(1, offers.size());
        assertEquals(12.22, offers.get(0)
                .getEffectiveInterestRate()
                .doubleValue(), 0.000001);

    }

    @Test
    @DisplayName("Should map Loan AuxMoney to AuxMoney")
    void loadProvider() {


        List<LoanOffer> offers = getOffersResponseMapper.fromAuxmoneyResponse(List.of(auxmoneySingleCallResponse));
        assertEquals(1, offers.size());
        assertEquals(Bank.AUXMONEY.label, offers.get(0)
                .getLoanProvider()
                .getName());
    }

    @Test
    @DisplayName("Should map PreAccepted to true")
    void preAccepted() {


        List<LoanOffer> offers = getOffersResponseMapper.fromAuxmoneyResponse(List.of(auxmoneySingleCallResponse));
        assertEquals(1, offers.size());

    }

    @Test
    @DisplayName("Should map Installment amount to Monthly rate")
    void installmentAmount() {
        auxmoneySingleCallResponse.setInstallmentAmount(10.12);
        List<LoanOffer> offers = getOffersResponseMapper.fromAuxmoneyResponse(List.of(auxmoneySingleCallResponse));
        assertEquals(1, offers.size());
        assertEquals(10.12, offers.get(0)
                .getMonthlyRate()
                .doubleValue(), 0.00001);

    }

    @Test
    @DisplayName("Should map  Total Payment to Total credit amount")
    @Disabled
    void creditAmount() {
        List<LoanOffer> offers = getOffersResponseMapper.fromAuxmoneyResponse(List.of(auxmoneySingleCallResponse));
        assertEquals(1, offers.size());
        assertEquals(1111.01, offers.get(0)
                .getTotalPayment()
                .doubleValue(), 0.00001);

    }


}
