package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.Finance;
import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface SantanderGetOfferIncomeExpenseRequestMapper {

    @Named("toRent")
    static BigDecimal toRent(final PersonalDetails personalDetails) {
        return personalDetails.getHousingType().equals(HousingType.RENT) ? personalDetails.getFinance().getExpenses().getAcknowledgedRent() : null;
    }

    @Named("toMortgage")
    static BigDecimal toMortgage(final Finance finance) {
        return (finance.getExpenses().getMortgages() == null || finance.getExpenses().getMortgages().compareTo(BigDecimal.ZERO) == 0) ? null : finance.getExpenses().getMortgages();
    }

    @Named("toHousingType")
    static ScbCapsBcoWSStub.WohnartType toHousingType(final PersonalDetails personalDetails) {
        return personalDetails.getHousingType().equals(HousingType.RENT) ? ScbCapsBcoWSStub.WohnartType.MIETWOHNUNG : null;
    }

    @Mapping(target = "nettoEinkommen", source = "finance.income.netIncome")
    @Mapping(target = "kindergeld", source = "finance.income.childBenefits")
    @Mapping(target = "rentenbezuege", source = "finance.income.pensionBenefits")
    @Mapping(target = "sonstigeEinnahmen", source = "finance.income.otherRevenue")
    @Mapping(target = "mietEinnahmen", source = "finance.income.rentalIncome")
    @Mapping(target = "unterhaltEingang", source = "finance.income.alimonyPayments")

    @Mapping(target = "hypothek", source = "finance", qualifiedByName = "toMortgage")
    @Mapping(target = "warmmiete", source = "personalDetails", qualifiedByName = "toRent")
    @Mapping(target = "lebensversicherung", ignore = true)
    @Mapping(target = "unterhaltZahlung", source = "finance.expenses.alimony")
    @Mapping(target = "wohnart", source = ".", qualifiedByName = "toHousingType")
    ScbCapsBcoWSStub.EinnahmenAusgabenXO toIncomesAndExpenses(final PersonalDetails personalDetails);
}
