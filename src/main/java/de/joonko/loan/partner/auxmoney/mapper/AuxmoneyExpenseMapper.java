package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.partner.auxmoney.AuxmoneyDefaults;
import de.joonko.loan.partner.auxmoney.model.Expenses;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
interface AuxmoneyExpenseMapper {


    @Mapping(target = "totalExpenses", source = ".", qualifiedByName = "getTotalExpensesInEuroCent")
    @Mapping(target = "rentAndMortgage", source = ".", qualifiedByName = "getRentAndMortgageInEuroCent")
    @Mapping(target = "supportExpenses", ignore = true)
    @Mapping(source = ".", target = "insuranceAndSavings", qualifiedByName = "getInsuranceAndSavings")
    @Mapping(target = "memberships", constant = AuxmoneyDefaults.MEMBERSHIPS)
    @Mapping(source = "loanInstalmentsInEuroCent", target = "debtExpenses")
    @Mapping(target = "livingExpenses", ignore = true)
    @Mapping(target = "other", source = ".", qualifiedByName = "getOtherInEuroCent")
    Expenses toAuxmoneyExpenses(de.joonko.loan.offer.domain.Expenses expenses);

    @Named("getRentAndMortgageInEuroCent")
    static int getRentAndMortgageInEuroCent(de.joonko.loan.offer.domain.Expenses expenses) {
        return expenses.getRentInEuroCent() + expenses.getMortgagesInEuroCent();
    }

    @Named("getOtherInEuroCent")
    static int getOtherInEuroCent(de.joonko.loan.offer.domain.Expenses expenses) {
        return expenses.getAlimonyInEuroCent() + expenses.getPrivateHealthInsuranceInEuroCent();
    }

    @Named("getTotalExpensesInEuroCent")
    static int getTotalExpensesInEuroCent(de.joonko.loan.offer.domain.Expenses expenses) {
        return getRentAndMortgageInEuroCent(expenses) + getOtherInEuroCent(expenses) + expenses.getInsuranceAndSavingsInEuroCent() + expenses.getLoanInstalmentsInEuroCent();
    }

    @Named("getInsuranceAndSavings")
    static int getInsuranceAndSavings(de.joonko.loan.offer.domain.Expenses expenses) {
        return expenses.getInsuranceAndSavingsInEuroCent() + expenses.getVehicleInsuranceInEuroCent();
    }

}
