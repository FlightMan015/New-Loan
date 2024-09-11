package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import de.joonko.loan.offer.domain.Expenses;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "mortgages", source = "mortgages")
    @Mapping(target = "insuranceAndSavings", source = "insuranceAndSavings")
    @Mapping(target = "loanInstalments", source = "loanInstalments")
    @Mapping(target = "rent", source = "rent")
    @Mapping(target = "alimony", source = "alimony")
    @Mapping(target = "vehicleInsurance", source = "vehicleInsurance")
    @Mapping(target = "privateHealthInsurance", source = "privateHealthInsurance")
    @Mapping(target = "acknowledgedMortgages", source = "acknowledgedMortgages")
    @Mapping(target = "acknowledgedRent", source = "acknowledgedRent")
    @Mapping(target = "loanInstallmentsSwk", source = "loanInstallmentsSwk")
    Expenses map(final de.joonko.loan.offer.api.Expenses expenses);


    @Mapping(target = "mortgages", source = "mortgages")
    @Mapping(target = "insuranceAndSavings", source = "insuranceAndSavings")
    @Mapping(target = "loanInstalments", source = "loanInstalments")
    @Mapping(target = "rent", source = "rent")
    @Mapping(target = "alimony", source = "alimony")
    @Mapping(target = "vehicleInsurance", source = "vehicleInsurance")
    @Mapping(target = "privateHealthInsurance", source = "privateHealthInsurance")
    @Mapping(target = "acknowledgedMortgages", source = "acknowledgedMortgages")
    @Mapping(target = "acknowledgedRent", source = "acknowledgedRent")
    @Mapping(target = "loanInstallmentsSwk", source = "loanInstallmentsSwk")
    de.joonko.loan.offer.api.Expenses map(final Expenses expenses);
}
