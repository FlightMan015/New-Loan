package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.*;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.CreditPlusMapperUtils;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CreditPlusDebtorFinancialDataMapper {
    @Mapping(target = "income", source = "digitalAccountStatements", qualifiedByName = "toIncome")
    @Mapping(target = "otherIncome", source = "digitalAccountStatements", qualifiedByName = "toOtherIncome")
    @Mapping(target = "otherIncomeType", constant = CreditPlusDefaults.OTHER_INCOME_TYPE)
    @Mapping(target = "childBenefit", source = "digitalAccountStatements", qualifiedByName = "toChildBenefit")
    @Mapping(target = "pension", source = "digitalAccountStatements", qualifiedByName = "toPension")
    @Mapping(target = "rentalIncome", source = "digitalAccountStatements", qualifiedByName = "toRentalIncome")
    @Mapping(target = "rent", source = "digitalAccountStatements", qualifiedByName = "toRent")
    @Mapping(target = "privateHealthInsurance", source = "digitalAccountStatements", qualifiedByName = "toPrivateHealthInsurance")
    @Mapping(target = "otherCreditRates", source = "digitalAccountStatements", qualifiedByName = "toOtherCreditRates")
    @Mapping(target = "aliment", source = "digitalAccountStatements", qualifiedByName = "toAliment")
    @Mapping(target = "houseCosts", source = "digitalAccountStatements", qualifiedByName = "toHouseCosts")
    @Mapping(target = "vehicle", source = "digitalAccountStatements", qualifiedByName = "toVehicle")
    @Mapping(target = "residenceType", source = "personalDetails.housingType", qualifiedByName = "toResidenceType")
    EfinComparerServiceStub.FinancialData toFinancialData(LoanDemand loanDemand);

    @Named("toResidenceType")
    default String toResidenceType(HousingType housingType) {
        return housingType.equals(HousingType.OWNER) ? CreditPlusDefaults.OWNER : CreditPlusDefaults.RENTER;
    }

    @Named("toRent")
    default BigDecimal mapRent(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_ID_RENT).abs();
    }

    @Named("toHouseCosts")
    default BigDecimal toHouseCosts(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_HOUSE_COSTS).abs();
    }

    @Named("toPrivateHealthInsurance")
    default BigDecimal toPrivateHealthInsurance(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_PRIVATE_HEALTH_INSURANCE).abs();
    }

    @Named("toAliment")
    default BigDecimal toAliment(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_ALIMENT);
    }

    @Named("toOtherCreditRates")
    default BigDecimal toOtherCreditRates(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_OTHER_CREDIT_RATES).abs();
    }

    @Named("toIncome")
    default BigDecimal toIncome(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_INCOME);
    }

    @Named("toOtherIncome")
    default BigDecimal toOtherIncome(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_OTHER_INCOME);
    }

    @Named("toRentalIncome")
    default BigDecimal toRentalIncome(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_RENTAl_INCOME);
    }

    @Named("toPension")
    default BigDecimal toPension(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_PENSION);
    }

    @Named("toChildBenefit")
    default BigDecimal toChildBenefit(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.calculateChildBenefit(digitalAccountStatements);
    }

    @Named("toVehicle")
    default Boolean toVehicle(DigitalAccountStatements digitalAccountStatements) {
        BigDecimal vehicleValue = CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_VEHICLE);
        if (vehicleValue.compareTo(BigDecimal.valueOf(50)) > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
