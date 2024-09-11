package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwkAccountRequestMapperTest extends BaseMapperTest {

    @Autowired
    private SwkAccountRequestMapper swkAccountRequestMapper;

    @Test
    void toBankingAccount(@Random LoanDemand loanDemand) {
        CreditApplicationServiceStub.BankingInformation bankingInformation = swkAccountRequestMapper.toBankingAccount(loanDemand);

        assertEquals(bankingInformation.getAccountHolder(), loanDemand.getPersonalDetails()
                .getFirstName()
                .concat(" ")
                .concat(loanDemand.getPersonalDetails()
                        .getLastName()));
        assertEquals(bankingInformation.getAccountNumber(), loanDemand.getDigitalAccountStatements()
                .getIban());

    }
}
