package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class SantanderGetOfferLoanDetailRequestMapperTest extends BaseMapperTest {

    @Autowired
    SantanderGetOfferLoanDetailRequestMapper santanderGetOfferLoanDetailRequestMapper;

    @Test
    void loanAsked(@Random LoanDemand loanDemand) {
        ScbCapsBcoWSStub.FinanzierungXO finanzierungXO = santanderGetOfferLoanDetailRequestMapper.toFinanzierung(loanDemand);
        Assert.assertEquals(finanzierungXO.getKreditbetragNetto().intValue(), loanDemand.getLoanAsked().intValue());
    }

    @Test
    void loanDemand_maps_correctly_to_santander_request(@Random LoanDemand loanDemand) {
        ScbCapsBcoWSStub.FinanzierungXO finanzierungXO = santanderGetOfferLoanDetailRequestMapper.toFinanzierung(loanDemand);
        Assert.assertEquals(finanzierungXO.getKreditbetragNetto().intValue(), loanDemand.getLoanAsked().intValue());
    }


    @Test
    void purpose(@Random LoanDemand loanDemand) {
        ScbCapsBcoWSStub.FinanzierungXO finanzierungXO = santanderGetOfferLoanDetailRequestMapper.toFinanzierung(loanDemand);
        Assert.assertEquals(finanzierungXO.getVerwendungszweck(), ScbCapsBcoWSStub.VwzType.STANDARD);
    }

    @Test
    void collectionData(@Random LoanDemand loanDemand) {
        ScbCapsBcoWSStub.FinanzierungXO finanzierungXO = santanderGetOfferLoanDetailRequestMapper.toFinanzierung(loanDemand);
        Assert.assertEquals(finanzierungXO.getRateneinzugZum(), ScbCapsBcoWSStub.RateneinzugType.ERSTER_EINES_MONATS);
    }

    @Test
    void rsv(@Random LoanDemand loanDemand) {
        ScbCapsBcoWSStub.FinanzierungXO finanzierungXO = santanderGetOfferLoanDetailRequestMapper.toFinanzierung(loanDemand);
        Assert.assertEquals(finanzierungXO.getRsv(), ScbCapsBcoWSStub.RsvType.OHNE_RSV);
    }
}