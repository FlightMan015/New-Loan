package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.joonko.loan.partner.auxmoney.validation.AuxmoneyLoanAsked;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public class AuxmoneyGetOffersRequest {

    @NotNull(message = "Income Details are mandatory")
    private Income income;

    @JsonProperty("external_id")
    @NotBlank(message = "External id is mandatory")
    private String externalId;

    @JsonProperty("second_borrower")
    private SecondBorrower secondBorrower;

    @JsonProperty("contact_data")
    @NotNull(message = "Contact Data is Mandatory ")
    private BorrowerContactData contactData;

    @JsonProperty("employer_data")
    private EmployerData employerData;

    @JsonProperty("digital_account_statements")
    private List<DigitalAccountStatements> digitalAccountStatements;

    @JsonProperty("duration")
    @NotNull(message = "Duration is mandatory")
    private LoanDuration duration;


    @JsonProperty("personal_data")
    @NotNull(message = "Personal Data is Mandatory")
    private PersonalData personalData;

    @JsonProperty("partner_broker")
    private PartnerBroker partnerBroker;

    @JsonProperty("collection_day")
    @NotNull(message = "Collection day  is mandatory")
    private PaymentCollectionDay collectionDay;

    @JsonProperty("loan_asked")
    @AuxmoneyLoanAsked
    private int loanAsked;

    @JsonProperty(value = "is_accepted_solvency_retrieval")
    @NotNull(message = "acceptedSolvencyRetrieval is mandatory")
    private boolean acceptedSolvencyRetrieval;

    @JsonProperty("household")
    private Household household;

    @JsonProperty("category")
    @NotNull(message = "Category is mandatory")
    private LoanCategory category;

    @JsonProperty("is_accepted_terms_of_service")
    @NotNull(message = "AcceptedTermsOfService is mandatory")
    private boolean acceptedTermsOfService;

    @JsonProperty("expenses")
    @NotNull(message = "Expenses Details are mandatory")
    private Expenses expenses;

    @JsonProperty("bank_data")
    private BankData bankData;

    @JsonProperty("rsv")
    private Integer rsv;


}
