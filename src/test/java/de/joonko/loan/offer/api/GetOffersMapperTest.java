package de.joonko.loan.offer.api;

import de.joonko.loan.offer.domain.Currency;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import io.github.glytching.junit.extension.random.Random;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetOffersMapperTest extends BaseMapperTest {

    @Autowired
    private GetOffersMapper getOffersMapper;

    @Test
    @DisplayName("Should convert LoanOffer.loanAmount to LoanOfferResponse.amount")
    void toAmount(@Random de.joonko.loan.offer.domain.LoanOffer loanOffer) {
        LoanOffer offerResponse = getOffersMapper.toResponse(loanOffer);
        Assert.assertEquals(loanOffer.getAmount(), offerResponse.getAmount());

    }

    @Test
    @DisplayName("Should convert LoanOffer.durationInMonth to LoanOfferResponse.durationInMonth")
    void durationInMonth(@Random de.joonko.loan.offer.domain.LoanOffer loanOffer) {
        LoanOffer offerResponse = getOffersMapper.toResponse(loanOffer);
        Assert.assertEquals(loanOffer.getDurationInMonth(), offerResponse.getDurationInMonth());

    }

    @Test
    @DisplayName("Should convert LoanOffer.LoanProvider to LoanOfferResponse.LoanProvider")
    void loanProvider(@Random de.joonko.loan.offer.domain.LoanOffer loanOffer) {
        LoanOffer offerResponse = getOffersMapper.toResponse(loanOffer);
        Assert.assertEquals(loanOffer.getLoanProvider()
                .getName(), offerResponse.getLoanProvider()
                .getName());

    }

    @Test
    @DisplayName("Should convert LoanOffer.EffectiveInterestRate to LoanOfferResponse.EffectiveInterestRate")
    void effectiveInterestRate(@Random de.joonko.loan.offer.domain.LoanOffer loanOffer) {
        LoanOffer offerResponse = getOffersMapper.toResponse(loanOffer);
        Assert.assertEquals(loanOffer.getEffectiveInterestRate(), offerResponse.getEffectiveInterestRate());
    }

    @Test
    @DisplayName("Should convert LoanOffer.MonthlyRate to LoanOfferResponse.MonthlyRate")
    void monthlyRate(@Random de.joonko.loan.offer.domain.LoanOffer loanOffer) {
        LoanOffer offerResponse = getOffersMapper.toResponse(loanOffer);
        Assert.assertEquals(loanOffer.getMonthlyRate(), offerResponse.getMonthlyRate());
    }

    @Test
    @DisplayName("Should convert LoanOffer.NominalInterestRate to LoanOfferResponse.NominalInterestRate")
    void nominalInterestRate(@Random de.joonko.loan.offer.domain.LoanOffer loanOffer) {
        LoanOffer offerResponse = getOffersMapper.toResponse(loanOffer);
        Assert.assertEquals(loanOffer.getNominalInterestRate(), offerResponse.getNominalInterestRate());
    }

    @Test
    @DisplayName("Should convert LoanOffer.TotalPayment to LoanOfferResponse.TotalPayment")
    void totalPayment(@Random de.joonko.loan.offer.domain.LoanOffer loanOffer) {
        LoanOffer offerResponse = getOffersMapper.toResponse(loanOffer);
        Assert.assertEquals(loanOffer.getTotalPayment(), offerResponse.getTotalPayment());
    }

    @Test
    @DisplayName("Should map to LoanDemand")
    void mapToLoanDemand(@Random LoanDemandRequest request) {
        // given
        adjustLoanRequestModel(request);

        // when
        LoanDemand demand = getOffersMapper.fromRequest(request, RandomStringUtils.randomAlphabetic(20), "123");

        // then
        assertAll(
                () -> assertEquals(LocalDate.of(2018, 03, 01), demand.getEmploymentDetails().getProfessionEndDate(), "failed mapping employmentDetails.endDate"),
                () -> assertEquals(request.getEmploymentDetails().getHouseNumber(), demand.getEmploymentDetails().getHouseNumber(), "failed mapping employmentDetails.houseNumber"),

                () -> assertEquals(request.getCreditDetails().getBonimaScore(), demand.getCreditDetails().getBonimaScore(), "failed mapping creditDetails.bonimaScore"),
                () -> assertEquals(request.getCreditDetails().getEstimatedSchufaClass(), demand.getCreditDetails().getEstimatedSchufaClass(), "failed mapping creditDetails.estimatedSchufaClass"),
                () -> assertEquals(request.getCreditDetails().getProbabilityOfDefault(), demand.getCreditDetails().getProbabilityOfDefault().doubleValue(), "failed mapping creditDetails.probabilityOfDefault"),
                () -> assertEquals(request.getCreditDetails().getCreditCardLimitDeclared(), demand.getCreditDetails().getCreditCardLimitDeclared().doubleValue(), "failed mapping creditDetails.creditCardLimitDeclared"),

                () -> assertEquals(request.getPersonalDetails().getNumberOfDependants(), demand.getPersonalDetails().getNumberOfDependants(), "failed mapping personalDetails.numberOfDependants"),
                () -> assertEquals(request.getPersonalDetails().getCountryOfBirth(), demand.getPersonalDetails().getCountryOfBirth(), "failed mapping personalDetails.countryOfBirth"),

                () -> assertEquals(request.getExpenses().getMonthlyLifeCost(), demand.getPersonalDetails().getFinance().getExpenses().getMonthlyLifeCost().doubleValue(), "failed mapping expenses.monthlyLifeCost"),
                () -> assertEquals(request.getExpenses().getMonthlyLoanInstallmentsDeclared(), demand.getPersonalDetails().getFinance().getExpenses().getMonthlyLoanInstallmentsDeclared().doubleValue(), "failed mapping expenses.monthlyLoanInstallmentsDeclared"),

                () -> assertEquals(request.getIncome().getIncomeDeclared(), demand.getPersonalDetails().getFinance().getIncome().getIncomeDeclared().doubleValue(), "failed mapping income.declaredSalary"),

                () -> assertEquals(request.getCustomDACData().getHasSalaryEachMonthLast3M(), demand.getCustomDACData().getHasSalaryEachMonthLast3M(), "failed mapping customDacData.hasSalaryEachMonthLast3M"),
                () -> assertEquals(request.getCustomDACData().getWasDelayInInstallments40DaysDiff(), demand.getCustomDACData().getWasDelayInInstallments40DaysDiff(), "failed mapping customDacData.asDelayInInstallments40DaysDiff"),
                () -> assertEquals(request.getCustomDACData().getWasDelayInInstallments62DaysDiff(), demand.getCustomDACData().getWasDelayInInstallments62DaysDiff(), "failed mapping customDacData.asDelayInInstallments62DaysDiff"),
                () -> assertEquals(request.getCustomDACData().getIsCurrentDelayInInstallments(), demand.getCustomDACData().getIsCurrentDelayInInstallments(), "failed mapping customDacData.isCurrentDelayInInstallments"),
                () -> assertEquals(request.getCustomDACData().getSumIncomes1MAgo(), demand.getCustomDACData().getSumIncomes1MAgo(), "failed mapping customDacData.sumIncomes1MAgo"),
                () -> assertEquals(request.getCustomDACData().getSumIncomes2MAgo(), demand.getCustomDACData().getSumIncomes2MAgo(), "failed mapping customDacData.sumIncomes2MAgo"),
                () -> assertEquals(request.getCustomDACData().getSumIncomes3MAgo(), demand.getCustomDACData().getSumIncomes3MAgo(), "failed mapping customDacData.sumIncomes3MAgo"),

                () -> assertEquals(request.getContactData().getPreviousAddress().getCity(), demand.getContactData().getPreviousAddress().getCity(), "failed mapping contactData.previousAddress.city"),
                () -> assertEquals(request.getContactData().getPreviousAddress().getPostCode(), demand.getContactData().getPreviousAddress().getPostCode(), "failed mapping contactData.previousAddress.postCode"),
                () -> assertEquals(request.getContactData().getPreviousAddress().getStreetName(), demand.getContactData().getPreviousAddress().getStreet(), "failed mapping contactData.previousAddress.street"),
                () -> assertEquals(request.getContactData().getPreviousAddress().getCountry().toString(), demand.getContactData().getPreviousAddress().getCountry().toString(), "failed mapping contactData.previousAddress.country"),
                () -> assertEquals(request.getContactData().getPreviousAddress().getHouseNumber(), demand.getContactData().getPreviousAddress().getHouseNumber(), "failed mapping contactData.previousAddress.houseNumber"),
                () -> assertEquals(request.getContactData().getPreviousAddress().getLivingSince().getYear(), demand.getContactData().getPreviousAddress().getLivingSince().getYear(), "failed mapping contactData.previousAddress.livingSince.year"),
                () -> assertEquals(request.getContactData().getPreviousAddress().getLivingSince().getYear(), demand.getContactData().getPreviousAddress().getLivingSince().getYear(), "failed mapping contactData.previousAddress.livingSince.year"),
                () -> assertEquals(request.getContactData().getLivingSince().getYear(), demand.getContactData().getLivingSince().getYear(), "failed mapping contactData.livingSince.year"),
                () -> assertEquals(request.getContactData().getLivingSince().getMonth(), demand.getContactData().getLivingSince().getMonthValue(), "failed mapping contactData.livingSince.month"),
                () -> assertEquals(request.getAccountDetails().getBankName(), demand.getDigitalAccountStatements().getBankName(), "failed mapping accountDetails.bankName")
        );
    }

    @Test
    void mapToLoanOfferStore(@Random de.joonko.loan.offer.domain.LoanOffer loanOffer) {
        // given
        final var userUuid = "dd722792-14c4-4b0c-b542-d3b677ecb2d8";
        final var applicationId = "38fh923f823f";
        final var parentApplicationId = "3hf8329f";

        // when
        final var loanOfferStore = getOffersMapper.toStore(loanOffer, userUuid, applicationId, parentApplicationId);

        // then
        assertAll(
                () -> assertNotNull(loanOfferStore.getOffer()),
                () -> assertEquals(userUuid, loanOfferStore.getUserUUID()),
                () -> assertEquals(applicationId, loanOfferStore.getApplicationId()),
                () -> assertEquals(parentApplicationId, loanOfferStore.getParentApplicationId()),
                () -> assertEquals(loanOffer.getLoanProviderOfferId(), loanOfferStore.getLoanProviderReferenceNumber())
        );
    }

    @Test
    @DisplayName("Should convert LoanOfferResponse.consents to LoanDemand.consents correctly")
    void consentList(@Random LoanDemandRequest request) {
        // given
        adjustLoanRequestModel(request);

        // when
        final var demand = getOffersMapper.fromRequest(request, RandomStringUtils.randomAlphabetic(20), "123");

        // then
        assertEquals(request.getConsents(), demand.getConsents());
    }

    @Test
    @DisplayName("Should map taxId correctly")
    void taxId(@Random LoanDemandRequest request) {
        // given
        adjustLoanRequestModel(request);

        // when
        final var demand = getOffersMapper.fromRequest(request, RandomStringUtils.randomAlphabetic(20), "123");

        // then
        assertEquals(request.getPersonalDetails().getTaxId(), demand.getPersonalDetails().getTaxId());
    }

    @Test
    @DisplayName("Should convert LoanOfferResponse.customDACData to LoanDemand.customDACData correctly")
    void dacPreventionTagsMappedCorrectly(@Random LoanDemandRequest request) {
        // given
        adjustLoanRequestModel(request);

        // when
        final var demand = getOffersMapper.fromRequest(request, RandomStringUtils.randomAlphabetic(20), "123");

        // then
        assertAll(
                () -> assertEquals(request.getCustomDACData().getCountEncashmentTag(), demand.getCustomDACData().getCountEncashmentTag()),
                () -> assertEquals(request.getCustomDACData().getCountSeizureTag(), demand.getCustomDACData().getCountSeizureTag()),
                () -> assertEquals(request.getCustomDACData().getCountPAccountTag(), demand.getCustomDACData().getCountPAccountTag()),
                () -> assertEquals(request.getCustomDACData().getCountChargebackTag(), demand.getCustomDACData().getCountChargebackTag())
        );
    }

    @Test
    @DisplayName("Should convert LoanOfferResponse.accountDetails.transactions to LoanDemand.accountDetails.transactions correctly")
    void accountTransactionsMappedCorrectly(@Random LoanDemandRequest request) {
        // given
        adjustLoanRequestModel(request);
        request.getAccountDetails().setTransactions(transactionsWithNullFields());

        // when
        final var demand = getOffersMapper.fromRequest(request, RandomStringUtils.randomAlphabetic(20), "123");

        // then
        assertAll(
                () -> assertEquals(request.getAccountDetails().getTransactions().stream().count(), demand.getDigitalAccountStatements().getTransactions().stream().count())
        );
    }

    private List<DacTransaction> transactionsWithNullFields() {
        return List.of(
                buildNormalTransaction().toBuilder().build(),
                buildNormalTransaction().toBuilder().iban(null).build(),
                buildNormalTransaction().toBuilder().bic(null).build(),
                buildNormalTransaction().toBuilder().amount(null).build(),
                buildNormalTransaction().toBuilder().bookingDate(null).build(),
                buildNormalTransaction().toBuilder().isPreBooked(null).build(),
                buildNormalTransaction().toBuilder().counterHolder(null).build(),
                buildNormalTransaction().toBuilder().categoryId(null).build(),
                buildNormalTransaction().toBuilder().purpose(null).build()
        );
    }

    private DacTransaction buildNormalTransaction() {
        return DacTransaction.builder()
                .iban("a")
                .bic("b")
                .amount(300d)
                .bookingDate(LocalDate.now())
                .isPreBooked(true)
                .purpose("c")
                .counterHolder("d")
                .categoryId("e")
                .build();
    }

    private void adjustLoanRequestModel(final LoanDemandRequest request) {
        request.getAccountDetails().setCurrency(String.valueOf(Currency.EUR));
        request.getContactData().setLivingSince(ShortDate.builder().month(02).year(2016).build());
        request.getContactData().setPreviousAddress(ofNullable(request.getContactData().getPreviousAddress()).map(previousAddress -> {
                    previousAddress.setLivingSince(ShortDate.builder().month(01).year(2013).build());
                    return previousAddress;
                }).
                orElse(null));
        request.getEmploymentDetails().setEmploymentType(EmploymentType.REGULAR_EMPLOYED);
        request.getEmploymentDetails().setEmploymentSince(ShortDate.builder().month(07).year(2014).build());
        request.getEmploymentDetails().setProfessionEndDate(ShortDate.builder().month(03).year(2018).build());
    }

}
