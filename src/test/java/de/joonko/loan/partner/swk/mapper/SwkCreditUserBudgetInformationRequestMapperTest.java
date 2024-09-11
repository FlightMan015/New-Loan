package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SwkCreditUserBudgetInformationRequestMapperTest extends BaseMapperTest {

    @Autowired
    SwkCreditUserBudgetInformationRequestMapper mapper;

    @Random
    PersonalDetails personalDetails;

    @Test
    void netIncome() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getNetIncome(), personalDetails.getFinance().getIncome().getNetIncome().intValue());
    }

    @Test
    void rentIncome() {
        personalDetails.getFinance().getIncome().setRentalIncome(BigDecimal.valueOf(100));
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getRentIncome(), 100);
    }

    @Test
    void rentIncomeZero() {
        personalDetails.getFinance().getIncome().setRentalIncome(BigDecimal.ZERO);
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getRentIncome(), BigDecimal.ZERO.intValue());
    }

    @Test
    @DisplayName("otherIncome should always zero")
    void otherIncome() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getOtherIncome(), 0);
    }

    @Test
    void rentExpenses() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getRentExpenses(), personalDetails.getFinance().getExpenses().getRent().intValue());
    }

    @Test
    void propertyExpenses() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getPropertyExpenses(), personalDetails.getFinance().getExpenses().getMortgages().intValue());
    }

    @Test
    void insuranceAndSavingsExpenses() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getInsuranceAndSavingsExpenses(), personalDetails.getFinance().getExpenses().getInsuranceAndSavings().add(personalDetails.getFinance().getExpenses().getPrivateHealthInsurance()).intValue());
    }

    @Test
    void otherInstallmentExpensesSwk() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getOtherInstallmentExpenses(), personalDetails.getFinance().getExpenses().getLoanInstallmentsSwk().intValue());
    }

    @Test
    void alimonyExpenses() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getAlimonyExpenses(), personalDetails.getFinance().getExpenses().getAlimony().intValue());
    }

    @Test
    void furtherExpenses() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(0, budgetInformation.getFurtherExpenses());
    }

    @Test
    void childAllowance() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(0, budgetInformation.getChildAllowance());
    }

    @Test
    void grossIncome() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(0, budgetInformation.getGrossIncome());
    }

    @Test
    void leasingRates() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(0, budgetInformation.getLeasingRates());
    }

    @Test
    void numberOfPersons() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(0, budgetInformation.getNumberOfPersons());
    }

    @Test
    void propertyValue50000() {
        personalDetails.getFinance().getExpenses().setMortgages(new BigDecimal(10));
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(SwkDefaults.PROPERTY_VALUE, budgetInformation.getPropertyValue());
    }

    @Test
    void propertyValue0() {
        personalDetails.getFinance().getExpenses().setMortgages(null);
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(0, budgetInformation.getPropertyValue());
    }

    @Test
    void reasonForNoRentExpenses() {
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertNull(budgetInformation.getReasonForNoRentExpenses());
    }

    @Test
    void typeOfOtherIncomeNull() {
        personalDetails.getFinance().getIncome().setOtherRevenue(BigDecimal.ZERO);
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertNull(budgetInformation.getReasonForNoRentExpenses());
    }

    @Test
    void typeOfOtherIncomeDefault() {
        personalDetails.getFinance().getIncome().setOtherRevenue(BigDecimal.TEN);
        CreditApplicationServiceStub.BudgetInformation budgetInformation = mapper.toBudgetInformation(personalDetails);
        assertEquals(budgetInformation.getTypeOfOtherIncome(), SwkDefaults.OTHER_INCOME);
    }
}
