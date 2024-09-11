package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.data.support.mapper.MapstructBaseTest;
import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.consors.model.Expense;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = ConsorsExpenseMapperImpl.class)
class ConsorsExpenseMapperTest extends MapstructBaseTest {

    @Random
    private PersonalDetails personalDetails;

    @Autowired
    private ConsorsExpenseMapper consorsExpenseMapper;

    @Test
    @DisplayName("Should return 0 if housing type is owner")
    void warmRentForOwner() {
        personalDetails.setHousingType(HousingType.OWNER);
        personalDetails.getFinance().getExpenses().setAcknowledgedRent(BigDecimal.valueOf(234.56));
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertEquals(0, expense.getWarmRent());
    }

    @Test
    @DisplayName("Should convert joonko rent to an integer warmRent")
    void warmRentForRenter() {
        personalDetails.setHousingType(HousingType.RENT);
        personalDetails.getFinance().getExpenses().setAcknowledgedRent(BigDecimal.valueOf(234.56));
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertEquals(234, expense.getWarmRent());
    }

    @Test
    @DisplayName("Should default warm rent to 0 if not present")
    void warmRentNull() {
        personalDetails.setHousingType(HousingType.RENT);
        personalDetails.getFinance().getExpenses().setAcknowledgedRent(BigDecimal.ZERO);
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertEquals(0, expense.getWarmRent());
    }

    @Test
    @DisplayName("Should convert spendingOnOtherChildren to null")
    void spendingOnOtherChildren() {
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertNull(expense.getSpendingOnOtherChildren());
    }

    @Test
    @DisplayName("Should return creditsAnother null when there is no loan|Installments")
    void creditsAnotherNull() {
        personalDetails.getFinance().getExpenses().setLoanInstalments(BigDecimal.ZERO);
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertNull(expense.getCreditsAnother());
    }

    @Test
    @DisplayName("Should return creditsAnother value when there is loan|Installments")
    void creditsAnotherValue() {
        personalDetails.getFinance().getExpenses().setLoanInstalments(BigDecimal.TEN);
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertEquals(10, expense.getCreditsAnother());
    }

    @Test
    @DisplayName("Should convert alimony to an integer otherHouseholdObligations")
    void otherHouseholdObligations() {
        personalDetails.getFinance().getExpenses().setAlimony(BigDecimal.valueOf(12345.67));
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertEquals(12345, expense.getOtherHouseholdObligations());
    }

    @Test
    @DisplayName("Should convert privateHealthInsurance to true if joonko privateHealthInsurance is more than 1.0")
    void privateHealthInsurance() {
        personalDetails.getFinance().getExpenses().setPrivateHealthInsurance(BigDecimal.valueOf(12345.67));
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertTrue(expense.getPrivateHealthInsurance());
    }

    @Test
    @DisplayName("Should convert privateHealthInsurance to false if joonko privateHealthInsurance is less than 1.0")
    void privateHealthInsurance_is_mapped_to_false_when_value_is_less_than_one() {
        personalDetails.getFinance().getExpenses().setPrivateHealthInsurance(BigDecimal.ZERO);
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertFalse(expense.getPrivateHealthInsurance());
    }

    @Test
    @DisplayName("Shouldreturn false when housing type is RENT")
    void hasResidentialProperty_is_false() {
        personalDetails.setHousingType(HousingType.RENT);
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertFalse(expense.isHasResidentialProperty());
    }

    @Test
    @DisplayName("Should return true when housing type is OWNER")
    void hasResidentialProperty_is_true() {
        personalDetails.setHousingType(HousingType.OWNER);
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertTrue(expense.isHasResidentialProperty());
    }

    @Test
    @DisplayName("Should return null when housing type is RENT")
    void toRealEstateMapperNull() {
        personalDetails.setHousingType(HousingType.RENT);
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertEquals(expense.getRealEstate(), String.valueOf(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should return mortgages when housing type is OWNER")
    void toRealEstateMapperFromMortgageToExpense() {
        personalDetails.setHousingType(HousingType.OWNER);
        personalDetails.getFinance().getExpenses().setMortgages(BigDecimal.TEN);
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertEquals(expense.getRealEstate(), String.valueOf(BigDecimal.TEN));
    }

    @Test
    @DisplayName("Should return null when housing type is RENT")
    void toRealEstateMapperFromMortgageNullToExpense() {
        personalDetails.setHousingType(HousingType.OWNER);
        personalDetails.getFinance().getExpenses().setMortgages(null);
        Expense expense = consorsExpenseMapper.fromJoonko(personalDetails);
        assertEquals(expense.getRealEstate(), String.valueOf(BigDecimal.ZERO));
    }

}
