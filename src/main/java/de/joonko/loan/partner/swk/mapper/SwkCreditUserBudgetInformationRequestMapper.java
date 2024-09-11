package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.Expenses;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface SwkCreditUserBudgetInformationRequestMapper {

    @Mapping(target = "netIncome", source = "finance.income.netIncome")
    @Mapping(target = "rentIncome", source = "finance.income.rentalIncome", qualifiedByName = "getRentIncome")
    @Mapping(target = "otherIncome", constant = "0")
    @Mapping(target = "rentExpenses", source = "finance.expenses.rent")
    @Mapping(target = "propertyExpenses", source = "finance.expenses.mortgages")
    @Mapping(target = "insuranceAndSavingsExpenses", source = "finance.expenses", qualifiedByName = "getInsuranceAndSavingsExpenses")
    @Mapping(target = "otherInstallmentExpenses", source = "finance.expenses.loanInstallmentsSwk")
    @Mapping(target = "alimonyExpenses", source = "finance.expenses.alimony")
    @Mapping(target = "furtherExpenses", ignore = true)
    @Mapping(target = "childAllowance", ignore = true)
    @Mapping(target = "grossIncome", ignore = true)
    @Mapping(target = "leasingRates", ignore = true)
    @Mapping(target = "numberOfPersons", ignore = true)
    @Mapping(target = "propertyValue", source = "finance.expenses.mortgages", qualifiedByName = "getPropertyValue")
    @Mapping(target = "reasonForNoRentExpenses", ignore = true)
    @Mapping(target = "typeOfOtherIncome", source = "finance.income.otherRevenue", qualifiedByName = "getTypeOfOtherIncome")
    CreditApplicationServiceStub.BudgetInformation toBudgetInformation(PersonalDetails personalDetails);

    @Named("getPropertyValue")
    default int getPropertyValue(BigDecimal mortgages) {
        return mortgages != null && mortgages.intValue() > 0 ? SwkDefaults.PROPERTY_VALUE : 0;
    }

    @Named("getTypeOfOtherIncome")
    default String getTypeOfOtherIncome(BigDecimal otherIncome) {
        return otherIncome != null && otherIncome.intValue() > 0 ? SwkDefaults.OTHER_INCOME : null;
    }

    @Named("getRentIncome")
    default int getRentIncome(BigDecimal rentalIncome) {
        return rentalIncome != null ? rentalIncome.intValue() : 0;
    }

    @Named("getInsuranceAndSavingsExpenses")
    default int getInsuranceAndSavingsExpenses(Expenses expenses) {
        return expenses.getInsuranceAndSavings() != null ? expenses.getPrivateHealthInsurance() != null ? expenses.getInsuranceAndSavings().add(expenses.getPrivateHealthInsurance()).intValue() :
                expenses.getInsuranceAndSavings().intValue() : 0;
    }

}
