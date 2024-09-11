package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.Income;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import io.github.glytching.junit.extension.random.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsorsIncomeMapperTest extends BaseMapperTest {

    @Autowired
    private ConsorsIncomeMapper consorsIncomeMapper;

    @Random
    private Income joonkoIncome;

    @Test
    @DisplayName("Should convert joonko netIncome to ConsorsNetIncome")
    void fromJoonko() {
        de.joonko.loan.partner.consors.model.Income income = consorsIncomeMapper.fromJoonko(joonkoIncome);
        assertEquals(joonkoIncome.getNetIncome()
                .doubleValue() + joonkoIncome.getChildBenefits().doubleValue(), income.getNetIncome());

    }

    @Test
    @DisplayName("Should convert joonko rentalIncome to Consors rentIncome")
    void fromJoonkoRentIncome() {
        de.joonko.loan.partner.consors.model.Income income = consorsIncomeMapper.fromJoonko(joonkoIncome);
        assertEquals(joonkoIncome.getRentalIncome()
                .doubleValue(), income.getRentIncome());

    }

    @Test
    @DisplayName("Should convert joonko otherRevenue to Consors otherIncome as integer")
    void fromJoonkoOtherIncome() {
        joonkoIncome.setOtherRevenue(BigDecimal.valueOf(1234.56));
        de.joonko.loan.partner.consors.model.Income income = consorsIncomeMapper.fromJoonko(joonkoIncome);
        assertEquals(1234, income.getOtherIncome());
    }

    @Test
    @DisplayName("Should default to 0 if joonko otherRevenue is not present")
    void fromNullToOtherIncome() {
        joonkoIncome.setOtherRevenue(null);
        de.joonko.loan.partner.consors.model.Income income = consorsIncomeMapper.fromJoonko(joonkoIncome);
        assertEquals(0, income.getOtherIncome());
    }

    @Test
    @DisplayName("Should ignore Consors isChildBenefitInSalery")
    void isChildBenefitInSalery() {
        de.joonko.loan.partner.consors.model.Income income = consorsIncomeMapper.fromJoonko(joonkoIncome);
        assertTrue(income.getIsChildBenefitInSalery());

    }

    @Test
    @DisplayName("Should ignore Consors ChildBenefitInSalery")
    void childBenefitInSalery() {
        de.joonko.loan.partner.consors.model.Income income = consorsIncomeMapper.fromJoonko(joonkoIncome);
        assertTrue(income.getChildBenefitInSalery());

    }
}
