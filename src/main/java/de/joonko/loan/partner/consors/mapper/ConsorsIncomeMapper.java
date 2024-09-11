package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.Income;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsorsIncomeMapper {

    default de.joonko.loan.partner.consors.model.Income fromJoonko(Income income) {
        if (income == null) {
            return null;
        }
        de.joonko.loan.partner.consors.model.Income.IncomeBuilder income1 = de.joonko.loan.partner.consors.model.Income.builder();

        if (income.getOtherRevenue() != null) {
            income1.otherIncome(income.getOtherRevenue().intValue());
        }
        if (income.getNetIncome() != null) {
            income1.netIncome(income.getNetIncome().intValue() + income.getChildBenefits().intValue());
        }
        if (income.getRentalIncome() != null) {
            income1.rentIncome(income.getRentalIncome().intValue());
        }
        income1.childBenefitInSalery(true);
        income1.isChildBenefitInSalery(true);

        return income1.build();
    }
}
