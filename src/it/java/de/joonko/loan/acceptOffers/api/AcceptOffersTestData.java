package de.joonko.loan.acceptOffers.api;

import de.joonko.loan.acceptoffer.api.AcceptOfferRequest;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.*;
import de.joonko.loan.offer.domain.Currency;
import de.joonko.loan.partner.aion.model.BestOfferValue;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.partner.aion.model.OfferDetails;
import de.joonko.loan.partner.consors.PersonalizedCalculationsStore;
import de.joonko.loan.partner.consors.model.FinancialCalculation;
import de.joonko.loan.partner.consors.model.FinancialCalculations;
import de.joonko.loan.partner.consors.model.LinkRelation;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.webhooks.postbank.model.ContractState;
import de.joonko.loan.webhooks.postbank.model.CreditResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;

public class AcceptOffersTestData {

    public static PersonalizedCalculationsStore getEditedPersonalizedCalculationsStore(PersonalizedCalculationsStore personalizedCalculationsStore) {
        List<LinkRelation> consorsLinks = new ArrayList<>();
        consorsLinks.add(new LinkRelation("Finalize Subscription",
                "/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription?version=5.0",
                "PUT",
                "_finalizesubscription"));

        PersonalizedCalculationsResponse personalizedCalculationsResponse = PersonalizedCalculationsResponse.builder()
                .financialCalculations(FinancialCalculations.builder()
                        .amountStepping(List.of(12L, 24L, 48L))
                        .financialCalculation(List.of(FinancialCalculation.builder()
                                .creditAmount(15000)
                                .duration(48)
                                .build()))
                        .links(consorsLinks)
                        .build()).build();
        personalizedCalculationsStore.setPersonalizedCalculationsResponse(personalizedCalculationsResponse);

        return personalizedCalculationsStore;
    }

    public static LoanDemandStore getEditedLoanDemandStore(LoanDemandStore loanDemandStore, String applicationId) {
        loanDemandStore.setApplicationId(applicationId);
        loanDemandStore.setLastName("94c95261af23ed7a559c81a81961e1b8af2910657477000b54e5b8a77ad35015e6a0eaf137f9c5c13ab7e891214d77a3");
        loanDemandStore.setFirstName("f55310661c2c1c4426b3da4c2489fca0b75f221dd4f0b680b2232f08dac78241d2a3b87670882dcf71e802d60ec05381");
        loanDemandStore.setEmailId("4cdcd7f2400113c2cab2f4580295e3e983bc643d1e940db986009f7e7ae9a779");

        return loanDemandStore;
    }

    public static LoanDemandRequest getEditedLoanDemandRequest(LoanDemandRequest request, String applicationId) {
        request.setApplicationId(applicationId);
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
        return request;
    }

    public static PostbankLoanDemandStore getPostbankLoanDemandStore(String applicationId, String loanProviderReferenceId) {
        return PostbankLoanDemandStore.builder()
                .applicationId(applicationId)
                .contractNumber(loanProviderReferenceId)
                .creditResults(
                        Set.of(CreditResult.builder()
                                        .partnerContractNumber(applicationId)
                                        .contractState(ContractState.ONLINE_GENEHMIGT_24)
                                        .build(),
                                CreditResult.builder()
                                        .partnerContractNumber(applicationId)
                                        .contractState(ContractState.UNTERLAGEN_EINGEGANGEN_25)
                                        .build()
                        )
                ).build();
    }

    public static CreditApplicationResponseStore getEditedCreditApplicationResponseStore(CreditApplicationResponseStore creditApplicationResponseStore, String applicationId) {
        creditApplicationResponseStore.setApplicationId(applicationId);
        creditApplicationResponseStore.setOffersProvided(List.of(
                BestOfferValue.builder()
                        .offerDetails(OfferDetails.builder()
                                .id("8718e9d2-58f5-4877-9bc9-7546215b2cec")
                                .amount(BigDecimal.valueOf(123))
                                .maturity(6)
                                .monthlyInstalmentAmount(BigDecimal.ONE)
                                .totalRepaymentAmount(BigDecimal.TEN)
                                .build())
                        .build()
        ));
        return creditApplicationResponseStore;
    }

    public static LoanOfferStore getEditedLoanOfferStore(LoanOfferStore loanOfferStore, String applicationId, Bank bank) {
        loanOfferStore.setApplicationId(applicationId);
        loanOfferStore.setOffer(LoanOffer.builder()
                .loanProvider(new LoanProvider(bank.getLabel()))
                .amount(123)
                .durationInMonth(LoanDuration.SIX.getValue())
                .monthlyRate(BigDecimal.ONE)
                .totalPayment(BigDecimal.TEN)
                .build());

        return loanOfferStore;
    }

    public static AcceptOfferRequest getAcceptOfferRequestWithoutLoanProvider(String applicationId, String loanOfferId) {
        return AcceptOfferRequest.builder()
                .loanOfferId(loanOfferId)
                .build();
    }

    public static String getRequestWithInvalidData() {
        return "{" +
                "  \"loanAsked\": 1500," +
                "  \"loanOfferId\": \"12345\"," +
                "  \"duration\": 48," +
                "  \"loanProvider\": \"Consors Finanz\"," +
                "  \"applicationId\": \"fa18b221-1159-4179-a42d-588c2d01e8a6\"" +
                "}";
    }

    public static String getRequestWithValidData(String loanOfferId, String applicationId) {
        return "{" +
                "  \"loanAsked\": 15000," +
                "  \"loanOfferId\": \"" + loanOfferId + "\"," +
                "  \"duration\": 48," +
                "  \"loanProvider\": \"Consors Finanz\"," +
                "  \"applicationId\": \"" + applicationId + "\"," +
                "\"loanDemandRequest\" : {\n" +
                "    \"dacId\": \"12345\",\n" +
                "    \"ftsTransactionId\": \"632de617d7233757d2a0a2744157cd46:12647eb9278f512300249f5df4b0dcbe00581b9217316f3f8c3291478fc2dc0a\",\n" +
                "    \"loanAsked\": 5000,\n" +
                "    \"termsAccepted\": true,\n" +
                "    \"personalDetails\": {\n" +
                "      \"gender\": \"MALE\",\n" +
                "      \"familyStatus\": \"SINGLE\",\n" +
                "      \"firstName\": \"HARTMUTS\",\n" +
                "      \"lastName\": \"MUSTEERMANN\",\n" +
                "      \"nationality\": \"DE\",\n" +
                "      \"birthDate\": \"1990-10-11\",\n" +
                "      \"numberOfChildren\": \"0\",\n" +
                "      \"housingType\": \"RENT\",\n" +
                "      \"placeOfBirth\": \"Berlin\",\n" +
                "      \"numberOfCreditCard\": 1,\n" +
                "      \"carInformation\": \"true\"\n" +
                "    },\n" +
                "    \"expenses\": {\n" +
                "      \"alimony\": 0,\n" +
                "      \"insuranceAndSavings\": 86.37,\n" +
                "      \"loanInstalments\": 257.38,\n" +
                "      \"mortgages\": 0,\n" +
                "      \"privateHealthInsurance\": 1510.3899999999999,\n" +
                "      \"rent\": 1924,\n" +
                "      \"acknowledgedMortgages\": 0,\n" +
                "      \"loanInstallmentsSwk\": 100,\n" +
                "      \"acknowledgedRent\": 1924,\n" +
                "      \"vehicleInsurance\": 0\n" +
                "    },\n" +
                "    \"income\": {\n" +
                "      \"alimonyPayments\": 0,\n" +
                "      \"childBenefits\": 0,\n" +
                "      \"netIncome\": 10000,\n" +
                "      \"otherRevenue\": 856.79,\n" +
                "      \"pensionBenefits\": 0,\n" +
                "      \"rentalIncome\": 0,\n" +
                "      \"acknowledgedNetIncome\": 10000\n" +
                "    },\n" +
                "    \"contactData\": {\n" +
                "      \"city\": \"Berlin\",\n" +
                "      \"streetName\": \"Hardenbergstraße\",\n" +
                "      \"houseNumber\": \"32\",\n" +
                "      \"postCode\": \"10623\",\n" +
                "      \"mobile\": \"4917624088579\",\n" +
                "      \"email\": \"qa.user@joonko.io\",\n" +
                "      \"livingSince\": {\n" +
                "        \"month\": 1,\n" +
                "        \"year\": 2020\n" +
                "      },\n" +
                "      \"previousAddress\": {\n" +
                "        \"streetName\": \"Mllerstr. 77\",\n" +
                "        \"postCode\": \"10623\",\n" +
                "        \"country\": \"DE\",\n" +
                "        \"city\": \"Berlin\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"employmentDetails\": {\n" +
                "      \"employmentType\": \"REGULAR_EMPLOYED\",\n" +
                "      \"employerName\": \"Joonko AG\",\n" +
                "      \"employmentSince\": {\n" +
                "        \"month\": 5,\n" +
                "        \"year\": 2012\n" +
                "      },\n" +
                "      \"streetName\": \"Hardenberstr.\",\n" +
                "      \"postCode\": \"10587\",\n" +
                "      \"city\": \"Berlin\"\n" +
                "    },\n" +
                "    \"customDACData\": {\n" +
                "      \"has3IncomeTags\": false,\n" +
                "      \"netIncomeHasGovSupport\": false,\n" +
                "      \"hasSalary\": true,\n" +
                "      \"hasConsorsPreventionTags\": false,\n" +
                "      \"carInformation\": true\n" +
                "    },\n" +
                "    \"accountDetails\": {\n" +
                "      \"balance\": 2123,\n" +
                "      \"balanceDate\": \"2020-03-13\",\n" +
                "      \"createdAt\": \"2014-03-13T00:00:00\",\n" +
                "      \"bic\": \"BYLADEM1001\",\n" +
                "      \"currency\": \"EUR\",\n" +
                "      \"iban\": \"DE65500000000012305678\",\n" +
                "      \"limit\": 1000,\n" +
                "      \"nameOnAccount\": \"HARTMUT HARTMUT\",\n" +
                "      \"transactions\": [\n" +
                "        {\n" +
                "          \"amount\": -9.55,\n" +
                "          \"iban\": \"\",\n" +
                "          \"bic\": \"\",\n" +
                "          \"bookingDate\": \"2020-03-13\",\n" +
                "          \"purpose\": \"ABSCHLUSS KEINE BELEG INFORMATIONEN, SIEHE GGF. KONTOAUSZUG !\",\n" +
                "          \"isPreBooked\": false\n" +
                "        },\n" +
                "        {\n" +
                "          \"amount\": -962,\n" +
                "          \"iban\": \"\",\n" +
                "          \"bic\": \"\",\n" +
                "          \"bookingDate\": \"2020-03-13\",\n" +
                "          \"purpose\": \"SEPA-DAUERAUFTRAG EMPFAENGER HAUSVERWALTUNG Musterhaus IBAN DE234234324234 BIC GENODEF1AAAA Order-Nr. 00022688654 VERWENDUNGSZWECK MIETE MUSTERMANN\",\n" +
                "          \"isPreBooked\": false\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }" +
                "}";
    }
}
