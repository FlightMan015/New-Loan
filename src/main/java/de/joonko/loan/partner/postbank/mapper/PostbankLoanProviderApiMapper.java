package de.joonko.loan.partner.postbank.mapper;

import com.google.common.base.Strings;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.offer.domain.LoanProviderApiMapper;
import de.joonko.loan.partner.postbank.PostbankPropertiesConfig;
import de.joonko.loan.partner.postbank.model.request.AccountData;
import de.joonko.loan.partner.postbank.model.request.AccountType;
import de.joonko.loan.partner.postbank.model.request.AddressData;
import de.joonko.loan.partner.postbank.model.request.EmploymentData;
import de.joonko.loan.partner.postbank.model.request.ExpensesData;
import de.joonko.loan.partner.postbank.model.request.FtsData;
import de.joonko.loan.partner.postbank.model.request.IncomeData;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequest;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestContract;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestCredit;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestSoapBody;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestSoapEnvelope;
import de.joonko.loan.partner.postbank.model.request.PersonalData;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.util.StringUtil;
import de.joonko.loan.webhooks.postbank.model.ContractState;

import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostbankLoanProviderApiMapper implements LoanProviderApiMapper<LoanDemandPostbankRequestSoapEnvelope, PostbankLoanDemandStore> {

    private final PostbankPropertiesConfig postbankPropertiesConfig;

    @Override
    public LoanDemandPostbankRequestSoapEnvelope toLoanProviderRequest(LoanDemand loanDemand, LoanDuration loanDuration) {
        final var request = LoanDemandPostbankRequestSoapEnvelope.builder()
                .body(LoanDemandPostbankRequestSoapBody.builder()
                        .contract(LoanDemandPostbankRequestContract.builder()
                                .credit(LoanDemandPostbankRequestCredit.builder()
                                        .companyId(postbankPropertiesConfig.getCompanyId())
                                        .password(postbankPropertiesConfig.getPassword())
                                        .request(LoanDemandPostbankRequest.builder()
                                                .applicationId(loanDemand.getLoanApplicationId())
                                                .loanAmount(loanDemand.getLoanAsked())
                                                .duration(loanDuration.getValue())
                                                .accounts(buildAccounts(loanDemand.getDigitalAccountStatements()))
                                                .personalData(PersonalData.builder()
                                                        .gender(buildGender(loanDemand))
                                                        .firstName(loanDemand.getPersonalDetails().getFirstName())
                                                        .lastName(loanDemand.getPersonalDetails().getLastName())
                                                        .birthDate(loanDemand.getPersonalDetails().getBirthDate())
                                                        .birthCity(loanDemand.getPersonalDetails().getPlaceOfBirth())
                                                        .maritalStatus(buildMaritalStatus(loanDemand))
                                                        .email(loanDemand.getContactData().getEmail().getEmailString())
                                                        .phoneNumber(loanDemand.getContactData().getMobile())
                                                        .numberOfChildren(loanDemand.getPersonalDetails().getNumberOfChildren())
                                                        .countOfLoanApplicants(calculateNumberOfDependents(loanDemand))
                                                        .taxId(loanDemand.getPersonalDetails().getTaxId())
                                                        .employmentData(buildEmploymentData(loanDemand))
                                                        .nationality(loanDemand.getPersonalDetails().getNationality().getCountryCode().getAlpha2())
                                                        .addressDataList(buildAddresses(loanDemand))
                                                        .incomeData(buildIncome(loanDemand))
                                                        .expensesData(buildExpenses(loanDemand))
                                                        .build())
                                                .ftsData(FtsData.builder()
                                                        .iban(loanDemand.getDigitalAccountStatements().getIban())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        if (!Strings.isNullOrEmpty(postbankPropertiesConfig.getTweakedIBAN())) {
            log.info("Env variable TWEAKED_IBAN_IN_LOAN_DEMAND_REQUEST is only meant for test environments. Modifying IBAN for postbank with {}", postbankPropertiesConfig.getTweakedIBAN());
            setFakeIban(request.getBody().getContract().getCredit().getRequest());
        }

        return request;
    }

    private Integer calculateNumberOfDependents(final LoanDemand loanDemand) {
        return Math.max(loanDemand.getPersonalDetails().getNumberOfDependants(), loanDemand.getPersonalDetails().getNumberOfChildren()) + 1;
    }

    private void setFakeIban(LoanDemandPostbankRequest loanDemandPostbankRequest) {
        loanDemandPostbankRequest.getFtsData().setIban(postbankPropertiesConfig.getTweakedIBAN());
        loanDemandPostbankRequest.getAccounts().forEach(account -> account.setIban(postbankPropertiesConfig.getTweakedIBAN()));
    }

    private List<AccountData> buildAccounts(final DigitalAccountStatements digitalAccountStatements) {
        final var bankName = StringUtil.limitAndTrimLastWord(digitalAccountStatements.getBankName(), 27);
        final var accountHolder = StringUtil.limitAndTrimLastWord(digitalAccountStatements.getOwner(), 27);

        return List.of(
                AccountData.builder()
                        .accountType(AccountType.TRANSFER_ACCOUNT)
                        .accountHolder(accountHolder)
                        .iban(digitalAccountStatements.getIban())
                        .bank(bankName)
                        .build(),
                AccountData.builder()
                        .accountType(AccountType.LOAN_PAY_BACK_ACCOUNT)
                        .accountHolder(accountHolder)
                        .iban(digitalAccountStatements.getIban())
                        .bank(bankName)
                        .build()
        );
    }

    private ExpensesData buildExpenses(final LoanDemand loanDemand) {
        return ExpensesData.builder()
                .warmRent(loanDemand.getPersonalDetails().getFinance().getExpenses().getAcknowledgedRent().setScale(2, RoundingMode.HALF_UP))
                .loanInstalments(loanDemand.getPersonalDetails().getFinance().getExpenses().getLoanInstalments().setScale(2, RoundingMode.HALF_UP))
                .constructionOrMortgageExpenses(loanDemand.getPersonalDetails().getFinance().getExpenses().getMortgages().setScale(2, RoundingMode.HALF_UP))
                .savings(loanDemand.getPersonalDetails().getFinance().getExpenses().getInsuranceAndSavings().setScale(2, RoundingMode.HALF_UP))
                .alimonyPayments(loanDemand.getPersonalDetails().getFinance().getExpenses().getAlimony().setScale(2, RoundingMode.HALF_UP))
                .privateHealthInsurance(loanDemand.getPersonalDetails().getFinance().getExpenses().getPrivateHealthInsurance().setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private IncomeData buildIncome(final LoanDemand loanDemand) {
        return IncomeData.builder()
                .netIncome(loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome().setScale(2, RoundingMode.HALF_UP))
                .rentalIncome(loanDemand.getPersonalDetails().getFinance().getIncome().getRentalIncome().setScale(2, RoundingMode.HALF_UP))
                .alimonyPayments(loanDemand.getPersonalDetails().getFinance().getIncome().getAlimonyPayments().setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private Integer buildMaritalStatus(final LoanDemand loanDemand) {
        int maritalStatus;
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
            default:
                maritalStatus = 1;
        }
        return maritalStatus;
    }

    private int buildGender(final LoanDemand loanDemand) {
        if (Gender.MALE.equals(loanDemand.getPersonalDetails().getGender())) {
            return 1;
        } else {
            return 2;
        }
    }

    private List<AddressData> buildAddresses(final LoanDemand loanDemand) {
        final var addresses = new ArrayList<AddressData>();

        addresses.add(AddressData.builder()
                .street(loanDemand.getContactData().getStreetName())
                .building(loanDemand.getContactData().getStreetNumber())
                .city(loanDemand.getContactData().getCity())
                .postalCode(loanDemand.getContactData().getZipCode().getCode())
                .since(loanDemand.getContactData().getLivingSince().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .build());

        if (Objects.nonNull(loanDemand.getContactData().getPreviousAddress())) {
            addresses.add(AddressData.builder()
                    .street(loanDemand.getContactData().getPreviousAddress().getStreet())
                    .building(loanDemand.getContactData().getPreviousAddress().getHouseNumber())
                    .city(loanDemand.getContactData().getPreviousAddress().getCity())
                    .postalCode(loanDemand.getContactData().getPreviousAddress().getPostCode())
                    .since(loanDemand.getContactData().getPreviousAddress().getLivingSince().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .build());
        }

        return addresses;
    }

    private EmploymentData buildEmploymentData(final LoanDemand loanDemand) {
        return EmploymentData.builder()
                .employmentType(1)
                .employmentSince(loanDemand.getEmploymentDetails().getEmploymentSince().format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .employmentUntil(Optional.ofNullable(loanDemand.getEmploymentDetails().getProfessionEndDate()).map(endDate -> endDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))).orElse(null))
                .build();
    }

    @Override
    public List<LoanOffer> fromLoanProviderResponse(final PostbankLoanDemandStore response) {
        return response.getCreditResults()
                .stream().filter(credit -> credit.getContractState() == ContractState.getSuccessState())
                .map(creditResult -> LoanOffer.builder()
                        .amount(creditResult.getLoanAmount().intValue())
                        .nominalInterestRate(creditResult.getNominalInterest())
                        .effectiveInterestRate(creditResult.getEffectiveInterest())
                        .loanProvider(LoanProvider.builder().name(Bank.POSTBANK.label).build())
                        .loanProviderOfferId(response.getContractNumber())
                        .durationInMonth(creditResult.getDuration())
                        .monthlyRate(creditResult.getMonthlyRate())
                        .totalPayment(creditResult.getLoanAmountTotal())
                        .build()
                ).collect(toList());
    }
}
