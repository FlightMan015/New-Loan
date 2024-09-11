package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwkCreditUsersRequestMapperTest extends BaseMapperTest {

    @Autowired
    SwkCreditUsersRequestMapper mapper;

    @Random
    LoanDemand loanDemand;

    @Test
    void agreedToSchufaRequest() {
        CreditApplicationServiceStub.CreditUser creditUser = mapper.toCreditUsers(loanDemand);
        assertTrue(creditUser.getAgreedToSchufaRequest());
    }

    @Test
    void bankCardsInformation() {
        CreditApplicationServiceStub.CreditUser creditUser = mapper.toCreditUsers(loanDemand);
        assertNotNull(creditUser.getBankCardsInformation());
    }

    @Test
    void budgetInformation() {
        CreditApplicationServiceStub.CreditUser creditUser = mapper.toCreditUsers(loanDemand);
        assertNotNull(creditUser.getBudgetInformation());
    }

    @Test
    void carInformation() {
        CreditApplicationServiceStub.CreditUser creditUser = mapper.toCreditUsers(loanDemand);
        assertNotNull(creditUser.getCarInformation());
    }

    @Test
    void employmentInformation() {
        CreditApplicationServiceStub.CreditUser creditUser = mapper.toCreditUsers(loanDemand);
        assertNotNull(creditUser.getEmploymentInformation());
    }


    @Test
    void familyInformation() {
        CreditApplicationServiceStub.CreditUser creditUser = mapper.toCreditUsers(loanDemand);
        assertNotNull(creditUser.getFamilyInformation());
    }

    @Test
    void person() {
        CreditApplicationServiceStub.CreditUser creditUser = mapper.toCreditUsers(loanDemand);
        assertNotNull(creditUser.getPerson());
    }
}
