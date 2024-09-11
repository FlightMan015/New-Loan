package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import de.joonko.loan.offer.domain.Income;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IncomeMapper {

    @Mapping(target = "netIncome", source = "netIncome")
    @Mapping(target = "childBenefits", source = "childBenefits")
    @Mapping(target = "pensionBenefits", source = "pensionBenefits")
    @Mapping(target = "otherRevenue", source = "otherRevenue")
    @Mapping(target = "rentalIncome", source = "rentalIncome")
    @Mapping(target = "alimonyPayments", source = "alimonyPayments")
    @Mapping(target = "acknowledgedNetIncome", source = "acknowledgedNetIncome")
    Income map(final de.joonko.loan.offer.api.Income income);


    @Mapping(target = "netIncome", source = "netIncome")
    @Mapping(target = "childBenefits", source = "childBenefits")
    @Mapping(target = "pensionBenefits", source = "pensionBenefits")
    @Mapping(target = "otherRevenue", source = "otherRevenue")
    @Mapping(target = "rentalIncome", source = "rentalIncome")
    @Mapping(target = "alimonyPayments", source = "alimonyPayments")
    @Mapping(target = "acknowledgedNetIncome", source = "acknowledgedNetIncome")
    de.joonko.loan.offer.api.Income map(final Income income);
}
