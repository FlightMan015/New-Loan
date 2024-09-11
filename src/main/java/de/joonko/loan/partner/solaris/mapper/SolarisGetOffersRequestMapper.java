package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.Expenses;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.solaris.SolarisPropertiesConfig;
import de.joonko.loan.partner.solaris.model.AmountValue;
import de.joonko.loan.partner.solaris.model.SolarisBankDefaults;
import de.joonko.loan.partner.solaris.model.SolarisGetOffersRequest;
import de.joonko.loan.util.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static de.joonko.loan.util.SolarisBankConstant.CURRENCY;
import static de.joonko.loan.util.SolarisBankConstant.CURRENCY_UNIT;

@Mapper(componentModel = "spring", uses = {SolarisLivingSituationMapper.class, SolarisEmploymentStatusMapper.class})
public abstract class SolarisGetOffersRequestMapper {

    @Autowired
    public SolarisPropertiesConfig solarisPropertiesConfig;

    @Mapping(target = "partnerReferenceNumber", expression = "java( loanDemand.getLoanApplicationId())")
    @Mapping(target = "livingSituationAmount", source = ".", qualifiedByName = "getLivingSituationAmount")
    // Mortgages + rent
    @Mapping(target = "existingCreditRepaymentExcludingMortgage", source = ".", qualifiedByName = "getExistingCreditRepaymentExcludingMortgage")
    @Mapping(target = "maintenanceObligationsAmount", source = ".", qualifiedByName = "getMaintenanceObligationsAmount")
    @Mapping(target = "netIncomeAmount", source = ".", qualifiedByName = "getNetIncomeAmount")
    @Mapping(target = "privateInsuranceAmount", source = ".", qualifiedByName = "getPrivateInsuranceAmount")
    @Mapping(target = "requestedLoanAmount", source = ".", qualifiedByName = "getRequestedLoanAmount")
    @Mapping(target = "hasPrivateInsurance", source = ".", qualifiedByName = "getHasPrivateInsurance")
    @Mapping(target = "numberOfkids", source = "personalDetails.numberOfChildren")
    @Mapping(target = "recipientIban", source = ".", qualifiedByName = "getRecipientIban")
    @Mapping(target = "livingSituation", source = "personalDetails.housingType")
    @Mapping(target = "hasMovedInLastTwoYears", source = ".", qualifiedByName = "getLastMovedInTwoYears")
    @Mapping(target = "employmentSince", source = "employmentDetails.employmentSince")
    @Mapping(target = "employmentStatus" , source = "employmentDetails.employmentType")
    @Mapping(target = "loanPurpose", constant = SolarisBankDefaults.LOAN_PURPOSE)
    @Mapping(target = "numberOfDependents", constant = SolarisBankDefaults.NUMBER_OF_DEPENDENTS)
    @Mapping(target = "isJointApplication", constant = SolarisBankDefaults.IS_JOINT_APPLICATION)
    @Mapping(target = "shouldSolarisBankGenerateContract", constant = SolarisBankDefaults.SHOULD_SOLARIS_GENERATE_CONTRACT)
    @Mapping(target = "repaymentDayOfMonth", constant = SolarisBankDefaults.REPAYMENT_DAY_OF_MONTH)
    @Mapping(target = "ftsTransactionId", source = "ftsTransactionId")
    @Mapping(constant = "48", target = "duration") //Default
    @Mapping(target = "additionalCosts", source = ".", qualifiedByName = "getDefaultAmountValue") // Default to 0
    @Mapping(target = "rent", source = ".", qualifiedByName = "getDefaultAmountValue") // Default to 0
    @Mapping(target = "mortgage", source = ".", qualifiedByName = "getDefaultAmountValue") // Default to 0
    public abstract SolarisGetOffersRequest toSolarisRequest(LoanDemand loanDemand);

    Boolean isTest() {
        return solarisPropertiesConfig.getTweakSnapshot();
    }

    @Named("getRequestedLoanAmount")
    static AmountValue getRequestedLoanAmount(LoanDemand loanDemand) {
        return AmountValue.builder()
                .currency(CURRENCY)
                .unit(CURRENCY_UNIT)
                .value(Util.toEuroCent(BigDecimal.valueOf(loanDemand.getLoanAsked())))
                .build();
    }

    @Named("getLivingSituationAmount")
    AmountValue getLivingSituationAmount(LoanDemand loanDemand) {
        Expenses expenses = loanDemand.getPersonalDetails().getFinance().getExpenses();
        Integer value = isTest() ? 0 : (expenses.getAcknowledgedMortgagesInEuroCent() + expenses.getAcknowledgedRentInEuroCent());

        return AmountValue.builder()
                .currency(CURRENCY)
                .unit(CURRENCY_UNIT)
                .value(value)
                .build();
    }

    @Named("getDefaultAmountValue")
    AmountValue getDefaultAmountValue(LoanDemand loanDemand) {
        return AmountValue.builder()
                .currency(CURRENCY)
                .unit(CURRENCY_UNIT)
                .value(Integer.valueOf(0))
                .build();
    }

    @Named("getExistingCreditRepaymentExcludingMortgage")
    AmountValue getExistingCreditRepaymentExcludingMortgage(LoanDemand loanDemand) {
        Integer value = isTest() ? 0 : loanDemand.getPersonalDetails().getFinance().getExpenses().getLoanInstalmentsInEuroCent();
        return AmountValue.builder()
                .currency(CURRENCY)
                .unit(CURRENCY_UNIT)
                .value(value)
                .build();
    }

    @Named("getMaintenanceObligationsAmount")
    AmountValue getMaintenanceObligationsAmount(LoanDemand loanDemand) {
        Integer value = isTest() ? 0 : loanDemand.getPersonalDetails().getFinance().getExpenses().getAlimonyInEuroCent();
        return AmountValue.builder()
                .currency(CURRENCY)
                .unit(CURRENCY_UNIT)
                .value(value)
                .build();
    }

    @Named("getNetIncomeAmount")
    AmountValue getNetIncomeAmount(LoanDemand loanDemand) {
        return AmountValue.builder()
                .currency(CURRENCY)
                .unit(CURRENCY_UNIT)
                .value(loanDemand.getPersonalDetails().getFinance().getIncome().getAcknowledgedNetIncomeEuroCent())
                .build();
    }

    @Named("getPrivateInsuranceAmount")
    AmountValue getPrivateInsuranceAmount(LoanDemand loanDemand) {
        Integer value = isTest() ? 0 : loanDemand.getPersonalDetails().getFinance().getExpenses().getPrivateHealthInsuranceInEuroCent();
        return AmountValue.builder()
                .currency(CURRENCY)
                .unit(CURRENCY_UNIT)
                .value(value)
                .build();
    }

    @Named("getLastMovedInTwoYears")
    Boolean getLastMovedInTwoYears(LoanDemand loanDemand) {
        return null != loanDemand.getContactData().getPreviousAddress();
    }

    @Named("getHasPrivateInsurance")
    Boolean getHasPrivateInsurance(LoanDemand loanDemand) {
        return !(loanDemand.getPersonalDetails().getFinance().getExpenses().getPrivateHealthInsuranceInEuroCent() == 0);
    }

    @Named("getRecipientIban")
    String getRecipientIban(LoanDemand loanDemand) {
        return isTest() ? "DE92370601930002130041" : loanDemand.getDigitalAccountStatements().getIban();
    }
}
