package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.partner.auxmoney.model.Income;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
interface AuxmoneyIncomeMapper {


    @Mapping(target = "benefit", source = "childBenefitsInEuroCent")
    @Mapping(target = "net", source = "netIncomeInEuroCent")
    @Mapping(target = "other", source = ".", qualifiedByName = "getOtherInEuroCent")
    @Mapping(target = "total", source = ".", qualifiedByName = "getTotalInEuroCent")
    Income toAuxmoneyIncome(de.joonko.loan.offer.domain.Income income);

    @Named("getOtherInEuroCent")
    default int getOtherInEuroCent(de.joonko.loan.offer.domain.Income income) {
        return income.getPensionBenefitsInEuroCent() + income.getOtherRevenueInEuroCent() + income.getRentalIncomeInEuroCent() + income.getAlimonyPaymentsInEuroCent();
    }

    @Named("getTotalInEuroCent")
    default int getTotalInEuroCent(de.joonko.loan.offer.domain.Income income) {
        return income.getPensionBenefitsInEuroCent() + income.getOtherRevenueInEuroCent() + income.getRentalIncomeInEuroCent() + income.getAlimonyPaymentsInEuroCent()
                + income.getNetIncomeInEuroCent() + income.getChildBenefitsInEuroCent();
    }
}
