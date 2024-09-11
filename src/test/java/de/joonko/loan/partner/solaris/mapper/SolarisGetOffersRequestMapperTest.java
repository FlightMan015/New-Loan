//package de.joonko.loan.partner.solaris.mapper;
//
//import de.joonko.loan.offer.domain.*;
//import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
//import de.joonko.loan.partner.solaris.model.EmploymentStatus;
//import de.joonko.loan.partner.solaris.model.LivingSituation;
//import de.joonko.loan.partner.solaris.model.SolarisBankDefaults;
//import de.joonko.loan.partner.solaris.model.SolarisGetOffersRequest;
//import io.github.glytching.junit.extension.random.Random;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class SolarisGetOffersRequestMapperTest extends BaseMapperTest {
//
//    @Autowired
//    private SolarisGetOffersRequestMapper solarisGetOffersRequestMapper;
//
//
//    @Test
//    @DisplayName("Should map id to partnerReferenceNumber")
//    void idToPartnerRefNumber(@Random LoanDemand loanDemand) {
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertEquals(loanDemand.getId().toString(), solarisGetOffersRequest.getPartnerReferenceNumber());
//    }
//
//    @Test
//    @DisplayName("Should map Sum of acknowledgedRent and acknowledgedMortgage to livingSituationAmount")
//    void mapToLivingSituationAmount(@Random LoanDemand loanDemand) {
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        Expenses expenses = loanDemand.getPersonalDetails().getFinance().getExpenses();
//        assertEquals(expenses.getAcknowledgedRentInEuroCent() + expenses.getAcknowledgedMortgagesInEuroCent(), solarisGetOffersRequest.getLivingSituationAmount().getValue());
//    }
//
//    @Test
//    @DisplayName("Should map loan Installments to creditPaymentsExcludingMortgage")
//    void mapCreditPaymentsExcludingMortgage(@Random LoanDemand loanDemand) {
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        Expenses expenses = loanDemand.getPersonalDetails().getFinance().getExpenses();
//        assertEquals(expenses.getLoanInstalmentsInEuroCent(), solarisGetOffersRequest.getExistingCreditRepaymentExcludingMortgage().getValue());
//    }
//
//    @Test
//    @DisplayName("Should map netIncomeAmount to user acknowledged netIncome")
//    void mapNetIncomeAmount(@Random LoanDemand loanDemand) {
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        Income income = loanDemand.getPersonalDetails().getFinance().getIncome();
//        assertEquals(income.getAcknowledgedNetIncomeEuroCent(), solarisGetOffersRequest.getNetIncomeAmount().getValue());
//    }
//
//    @Test
//    @DisplayName("Should map privateHealthInsurance to PrivateInsuranceAmount")
//    void mapPrivateInsuranceAmount(@Random LoanDemand loanDemand) {
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        Expenses expenses = loanDemand.getPersonalDetails().getFinance().getExpenses();
//        assertEquals(expenses.getPrivateHealthInsuranceInEuroCent(), solarisGetOffersRequest.getPrivateInsuranceAmount().getValue());
//    }
//
//    @Test
//    @DisplayName("Should map loanAsked to requestedLoanAmount")
//    void mapLoanAskedToRequestedLoanAmount(@Random LoanDemand loanDemand) {
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertEquals(loanDemand.getLoanAsked() * 100, solarisGetOffersRequest.getRequestedLoanAmount().getValue());
//    }
//
//    @Test
//    @DisplayName("Should set hasPrivateInsurance to true when privateInsurance is not Null")
//    void mapHasPrivateInsuranceTrue(@Random LoanDemand loanDemand) {
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertTrue(solarisGetOffersRequest.getHasPrivateInsurance().equals(Boolean.TRUE));
//    }
//
//    @Test
//    @DisplayName("Should set hasPrivateInsurance to false when privateInsurance is Null")
//    void mapHasPrivateInsuranceFalse(@Random LoanDemand loanDemand) {
//        loanDemand.getPersonalDetails().getFinance().getExpenses().setPrivateHealthInsurance(BigDecimal.ZERO);
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertTrue(solarisGetOffersRequest.getHasPrivateInsurance().equals(Boolean.FALSE));
//    }
//
//    @Test
//    @DisplayName("Should map category to loanPurpose")
//    void mapConstantOtherToLoanPurpose(@Random LoanDemand loanDemand) {
//
//        LoanDemand loanDemand1 = new LoanDemand(5000, LoanDuration.TWENTY_FOUR, LoanCategory.FURNITURE_RENOVATION_MOVE, loanDemand.getPersonalDetails(), loanDemand.getEmploymentDetails(), loanDemand.getContactData(), loanDemand.getDigitalAccountStatements(), null, null);
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand1);
//        assertEquals(SolarisBankDefaults.LOAN_PURPOSE, solarisGetOffersRequest.getLoanPurpose());
//    }
//
//    @Test
//    @DisplayName("Should map numberOfChilren to numberOfKids")
//    void mapNoOfChildrenToNoOfKids(@Random LoanDemand loanDemand) {
//        loanDemand.getPersonalDetails().setNumberOfChildren(2);
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertEquals(2, solarisGetOffersRequest.getNumberOfkids());
//    }
//
////    @Test
////    @DisplayName("Should map IBAN to Recipient iban")
////    void mapIbanToRecipientIban(@Random LoanDemand loanDemand) {
////        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
////        assertEquals(loanDemand.getDigitalAccountStatements().getIban(), solarisGetOffersRequest.getRecipientIban());
////    }
//
//    @Test
//    @DisplayName("Should map housingType to livingSituation")
//    void mapHousingTypeToLivingSituation(@Random LoanDemand loanDemand) {
//        loanDemand.getPersonalDetails().setHousingType(HousingType.OWNER);
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertEquals(LivingSituation.LIVING_IN_OWN_HOUSE, solarisGetOffersRequest.getLivingSituation());
//    }
//
//    @Test
//    @DisplayName("Should map hasMovedInLastTwoYears to TRUE if previousAddress is not NULL")
//    void mapHasMovedInLastTwoYearsTrue(@Random LoanDemand loanDemand) {
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertTrue(solarisGetOffersRequest.getHasMovedInLastTwoYears().equals(Boolean.TRUE));
//    }
//
//    @Test
//    @DisplayName("Should map hasMovedInLastTwoYears to FALSE if previousAddress is NULL")
//    void mapHasMovedInLastTwoYearsFalse(@Random LoanDemand loanDemand) {
//        loanDemand.getContactData().setPreviousAddress(null);
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertTrue(solarisGetOffersRequest.getHasMovedInLastTwoYears().equals(Boolean.FALSE));
//    }
//
//    @Test
//    @DisplayName("Should map employmentSince")
//    void mapEmploymentSince(@Random LoanDemand loanDemand) {
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertEquals(loanDemand.getEmploymentDetails().getEmploymentSince(), solarisGetOffersRequest.getEmploymentSince());
//    }
//
//    @Test
//    @DisplayName("Should map employmentStatus")
//    void mapEmploymentStatus(@Random LoanDemand loanDemand) {
//        loanDemand.getEmploymentDetails().setEmploymentType(EmploymentType.REGULAR_EMPLOYED);
//        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
//        assertEquals(EmploymentStatus.EMPLOYED, solarisGetOffersRequest.getEmploymentStatus());
//    }
//
//}
