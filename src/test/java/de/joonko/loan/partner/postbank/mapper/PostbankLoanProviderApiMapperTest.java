package de.joonko.loan.partner.postbank.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.postbank.PostbankPropertiesConfig;
import de.joonko.loan.partner.postbank.model.request.AccountType;
import de.joonko.loan.partner.postbank.model.request.EmploymentData;
import de.joonko.loan.partner.postbank.model.request.fts.DocumentType;
import de.joonko.loan.partner.postbank.model.request.fts.ProviderType;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.util.StringUtil;
import de.joonko.loan.webhooks.postbank.model.ContractState;
import de.joonko.loan.webhooks.postbank.model.CreditResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(RandomBeansExtension.class)
class PostbankLoanProviderApiMapperTest {

    private final String MOCKED_IBAN = "DE77100100100135248109";

    private PostbankPropertiesConfig postbankPropertiesConfig;

    private PostbankLoanProviderApiMapper postbankLoanProviderApiMapper;

    @BeforeEach
    void setUp() {

        postbankPropertiesConfig = mock(PostbankPropertiesConfig.class);
        postbankLoanProviderApiMapper = new PostbankLoanProviderApiMapper(postbankPropertiesConfig);
    }

    @Test
    void toLoanProviderRequest_mapsAllFieldsAsExpected(@Random LoanDemand loanDemand, @Random LoanDuration loanDuration) {
        // given
        final var companyId = "someCompany";
        final var password = "somePassword";
        loanDemand.getDigitalAccountStatements().setBankName("Lorem ipsum dolor sit amet consectetur adipiscing");
        loanDemand.getDigitalAccountStatements().setOwner("Melisa und Eugenio Heberzettl");
        // when
        when(postbankPropertiesConfig.getCompanyId()).thenReturn(companyId);
        when(postbankPropertiesConfig.getPassword()).thenReturn(password);
        when(postbankPropertiesConfig.getTweakedIBAN()).thenReturn(MOCKED_IBAN);

        final var mapped = postbankLoanProviderApiMapper.toLoanProviderRequest(loanDemand, loanDuration);
        final var request = mapped.getBody().getContract().getCredit().getRequest();

        assertAll(
                () -> assertEquals(companyId, mapped.getBody().getContract().getCredit().getCompanyId()),
                () -> assertEquals(password, mapped.getBody().getContract().getCredit().getPassword()),
                () -> assertEquals(loanDuration.getValue(), request.getDuration()),
                () -> assertEquals(loanDemand.getLoanAsked(), request.getLoanAmount()),
                () -> assertEquals("antrag", request.getType()),
                () -> assertEquals(loanDemand.getLoanApplicationId(), request.getApplicationId()),
                () -> assertEquals(0, request.getRefinancingLoanAmount()),
                () -> assertEquals(0, request.getInterestRate()),
                () -> assertEquals(15, request.getPaymentDayOfMonth()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFirstName(), request.getPersonalData().getFirstName()),
                () -> assertEquals(loanDemand.getPersonalDetails().getLastName(), request.getPersonalData().getLastName()),
                () -> assertEquals(loanDemand.getPersonalDetails().getBirthDate(), request.getPersonalData().getBirthDate()),
                () -> assertEquals(loanDemand.getPersonalDetails().getPlaceOfBirth(), request.getPersonalData().getBirthCity()),
                () -> assertEquals(loanDemand.getPersonalDetails().getNationality().getCountryCode().getAlpha2(), request.getPersonalData().getNationality()),
                () -> assertEquals(0, request.getPersonalData().getDegree()),
                () -> assertEquals(ofNullable(loanDemand.getPersonalDetails().getGender()).map(g -> Gender.MALE.equals(g) ? 1 : 2).orElse(2), request.getPersonalData().getGender()),
                () -> assertEquals(buildMaritalStatus(loanDemand), request.getPersonalData().getMaritalStatus()),
                () -> assertEquals(loanDemand.getContactData().getEmail().getEmailString(), request.getPersonalData().getEmail()),
                () -> assertEquals(loanDemand.getContactData().getMobile(), request.getPersonalData().getPhoneNumber()),
                () -> assertEquals(0, request.getPersonalData().getPaymentProtectionInsurance()),
                () -> assertEquals(Math.max(loanDemand.getPersonalDetails().getNumberOfDependants(), loanDemand.getPersonalDetails().getNumberOfChildren()) + 1, request.getPersonalData().getCountOfLoanApplicants()),
                () -> assertEquals(loanDemand.getPersonalDetails().getNumberOfChildren(), request.getPersonalData().getNumberOfChildren()),
                () -> assertEquals(0, request.getPersonalData().getApplicantsLiveInJointHousehold()),
                () -> assertEquals(loanDemand.getPersonalDetails().getTaxId(), request.getPersonalData().getTaxId()),
                () -> assertEquals(0, request.getPersonalData().getCreditExperience()),


                () -> assertEquals(2, request.getAccounts().size()),
                () -> assertTrue(request.getAccounts().stream().allMatch(account ->
                        account.getAccountHolder().equals("Melisa und Eugenio") &&
                                account.getIban().equals(MOCKED_IBAN) &&
                                account.getBank().equals(StringUtil.limitAndTrimLastWord(loanDemand.getDigitalAccountStatements().getBankName(), 27)) &&
                                List.of(AccountType.TRANSFER_ACCOUNT, AccountType.LOAN_PAY_BACK_ACCOUNT).contains(account.getAccountType())
                )),
                () -> assertEquals(buildEmploymentData(loanDemand), request.getPersonalData().getEmploymentData()),

                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome().setScale(2, RoundingMode.HALF_UP), request.getPersonalData().getIncomeData().getNetIncome()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getRentalIncome().setScale(2, RoundingMode.HALF_UP), request.getPersonalData().getIncomeData().getRentalIncome()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getAlimonyPayments().setScale(2, RoundingMode.HALF_UP), request.getPersonalData().getIncomeData().getAlimonyPayments()),
                () -> assertEquals(BigDecimal.ZERO, request.getPersonalData().getIncomeData().getFreelanceIncome()),
                () -> assertEquals(BigDecimal.ZERO, request.getPersonalData().getIncomeData().getMinijob()),
                () -> assertEquals(BigDecimal.ZERO, request.getPersonalData().getIncomeData().getHousingBenefits()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getAlimony().setScale(2, RoundingMode.HALF_UP), request.getPersonalData().getExpensesData().getAlimonyPayments()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getAcknowledgedRent().setScale(2, RoundingMode.HALF_UP), request.getPersonalData().getExpensesData().getWarmRent()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getLoanInstalments().setScale(2, RoundingMode.HALF_UP), request.getPersonalData().getExpensesData().getLoanInstalments()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getMortgages().setScale(2, RoundingMode.HALF_UP), request.getPersonalData().getExpensesData().getConstructionOrMortgageExpenses()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getPrivateHealthInsurance().setScale(2, RoundingMode.HALF_UP), request.getPersonalData().getExpensesData().getPrivateHealthInsurance()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getInsuranceAndSavings().setScale(2, RoundingMode.HALF_UP), request.getPersonalData().getExpensesData().getSavings()),
                () -> assertEquals(BigDecimal.ZERO, request.getPersonalData().getExpensesData().getLeasingExpenses()),
                () -> assertEquals(BigDecimal.ZERO, request.getPersonalData().getExpensesData().getBewirtschaftung()),

                () -> assertEquals(MOCKED_IBAN, request.getFtsData().getIban()),
                () -> assertEquals(DocumentType.JSON, request.getFtsData().getDocumentType()),
                () -> assertEquals("dac", request.getFtsData().getDocumentName()),
                () -> assertEquals(ProviderType.FINTEC, request.getFtsData().getSource()),
                () -> assertEquals(ProviderType.FINTEC, request.getFtsData().getTarget())
        );
    }


    private EmploymentData buildEmploymentData(final LoanDemand loanDemand) {
        return EmploymentData.builder()
                .employmentType(1)
                .employmentSince(loanDemand.getEmploymentDetails().getEmploymentSince().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .employmentUntil(Optional.ofNullable(loanDemand.getEmploymentDetails().getProfessionEndDate()).map(endDate -> endDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))).orElse(null))
                .build();
    }

    private Integer buildMaritalStatus(final LoanDemand loanDemand) {
        int maritalStatus = 1;
        switch (loanDemand.getPersonalDetails().getFamilyStatus()) {
            case MARRIED:
                maritalStatus = 2;
                break;
            case DIVORCED:
                maritalStatus = 3;
                break;
            case WIDOWED:
                maritalStatus = 4;
                break;
            case LIVING_SEPARATELY:
                maritalStatus = 5;
                break;
            case LIVING_IN_LONGTERM_RELATIONSHIP:
                maritalStatus = 6;
                break;
        }
        return maritalStatus;
    }

    @Test
    void fromLoanProviderResponse_mapsToEmptyList_whenNoCreditResultAvailable() {
        // given
        final var response = PostbankLoanDemandStore.builder().build();

        // when
        List<LoanOffer> loanOffers = postbankLoanProviderApiMapper.fromLoanProviderResponse(response);

        assertEquals(0, loanOffers.size());
    }

    @Test
    void fromLoanProviderResponse_mapsToEmptyList_whenNoSuccessfulOfferInCreditResultAvailable() {
        // given
        final var response = PostbankLoanDemandStore.builder()
                .creditResults(Set.of(CreditResult.builder()
                        .contractState(ContractState.UNTERLAGEN_EINGEGANGEN_25)
                        .build()))
                .build();

        // when
        List<LoanOffer> loanOffers = postbankLoanProviderApiMapper.fromLoanProviderResponse(response);

        assertEquals(0, loanOffers.size());
    }

    @Test
    void fromLoanProviderResponse_mapsToEmptyList_whenSuccessfulOfferInCreditResultAvailable(@Random CreditResult creditResult) {
        // given
        final var response = PostbankLoanDemandStore.builder()
                .contractNumber("38979385")
                .creditResults(Set.of(creditResult.toBuilder()
                        .contractState(ContractState.ONLINE_GENEHMIGT_24)
                        .build()))
                .build();

        // when
        List<LoanOffer> loanOffers = postbankLoanProviderApiMapper.fromLoanProviderResponse(response);
        assertEquals(1, loanOffers.size());

        final LoanOffer loanOffer = loanOffers.stream().findFirst().get();

        assertAll(
                () -> assertEquals(creditResult.getLoanAmount().intValue(), loanOffer.getAmount()),
                () -> assertEquals(creditResult.getDuration(), loanOffer.getDurationInMonth()),
                () -> assertEquals(LoanProvider.builder().name(Bank.POSTBANK.label).build(), loanOffer.getLoanProvider()),
                () -> assertEquals(response.getContractNumber(), loanOffer.getLoanProviderOfferId()),
                () -> assertEquals(creditResult.getEffectiveInterest(), loanOffer.getEffectiveInterestRate()),
                () -> assertEquals(creditResult.getNominalInterest(), loanOffer.getNominalInterestRate()),
                () -> assertEquals(creditResult.getLoanAmountTotal(), loanOffer.getTotalPayment()),
                () -> assertEquals(creditResult.getMonthlyRate(), loanOffer.getMonthlyRate())
        );
    }

}
