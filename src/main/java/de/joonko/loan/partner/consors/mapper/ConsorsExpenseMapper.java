package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.consors.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ConsorsExpenseMapper {


    @Mapping(target = "warmRent", source = ".", qualifiedByName = "warmRentMapper")
    @Mapping(target = "spendingOnOtherChildren", ignore = true)
    @Mapping(target = "creditsAnother", source = "finance.expenses.loanInstalments", qualifiedByName = "creditsAnotherMapper")
    @Mapping(target = "otherHouseholdObligations", source = "finance.expenses.alimony")
    @Mapping(target = "privateHealthInsurance", source = "finance.expenses.privateHealthInsurance", qualifiedByName = "toPrivateInsurance")
    @Mapping(target = "hasResidentialProperty", source = "housingType", qualifiedByName = "hasResidentialPropertyMapper")
    @Mapping(target = "realEstate", source = ".", qualifiedByName = "toRealEstateMapper")
    Expense fromJoonko(PersonalDetails personalDetails);

    @Named("toPrivateInsurance")
    default boolean toPrivateInsurance(Double insurance) {
        return insurance.intValue() >= 1;
    }

    @Named("toRealEstateMapper")
    default String toRealEstateMapper(PersonalDetails personalDetails) {
        BigDecimal value = personalDetails.getHousingType().equals(HousingType.OWNER)
                ? personalDetails.getFinance().getExpenses().getMortgages() != null ?
                personalDetails.getFinance().getExpenses().getMortgages() : BigDecimal.ZERO :
                BigDecimal.ZERO;
        return String.valueOf(value.intValue());
    }

    @Named("creditsAnotherMapper")
    default Integer creditsAnotherMapper(BigDecimal loanInstalments) {
        return loanInstalments.intValue() > 0 ? loanInstalments.intValue() : null;
    }


    @Named("hasResidentialPropertyMapper")
    default boolean hasResidentialPropertyMapper(HousingType housingType) {
        return housingType.equals(HousingType.OWNER);
    }

    @Named("warmRentMapper")
    default int warmRentMapper(PersonalDetails personalDetails) {
        final var acknowledgeRent = personalDetails.getFinance().getExpenses().getAcknowledgedRent();

        if (personalDetails.getHousingType().equals(HousingType.RENT) &&
                acknowledgeRent != null && acknowledgeRent.compareTo(BigDecimal.ZERO) > 0) {
            return acknowledgeRent.intValue();
        } else {
            return 0;
        }
    }

}
