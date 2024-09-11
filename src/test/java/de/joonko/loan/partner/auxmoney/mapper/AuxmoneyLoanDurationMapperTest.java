package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuxmoneyLoanDurationMapperTest extends BaseMapperTest {


    @Autowired
    AuxmoneyLoanDurationMapper auxmoneyLoanDurationMapper;

    @Test
    void sixMonth() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.SIX);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.TWELVE, loanDuration);
    }

    @Test
    void twelve() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.TWELVE);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.TWELVE, loanDuration);
    }

    @Test
    void eighteen() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.EIGHTEEN);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.TWENTY_FOUR, loanDuration);
    }

    @Test
    void twenty_four() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.TWENTY_FOUR);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.TWENTY_FOUR, loanDuration);
    }

    @Test
    void forty_eight() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.FORTY_EIGHT);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.FORTY_EIGHT, loanDuration);
    }

    @Test
    void sixty() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.SIXTY);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.SIXTY, loanDuration);
    }

    @Test
    void seventyTwo() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.SEVENTY_TWO);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.SEVENTY_TWO, loanDuration);
    }

    @Test
    void eightyFour() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.EIGHTY_FOUR);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.EIGHTY_FOUR, loanDuration);
    }

    @Test
    void ninety_six() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.NINETY_SIX);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.EIGHTY_FOUR, loanDuration);
    }

    @Test
    void one_hundred_eight() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.ONE_HUNDRED_EIGHT);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.EIGHTY_FOUR, loanDuration);
    }

    @Test
    void one_hundred_twenty() {
        de.joonko.loan.partner.auxmoney.model.LoanDuration loanDuration = auxmoneyLoanDurationMapper.toAuxmoneyLoanDuration(LoanDuration.ONE_HUNDRED_TWENTY);
        assertEquals(de.joonko.loan.partner.auxmoney.model.LoanDuration.EIGHTY_FOUR, loanDuration);
    }


}
