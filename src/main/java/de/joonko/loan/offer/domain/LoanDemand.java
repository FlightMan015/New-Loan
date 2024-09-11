package de.joonko.loan.offer.domain;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.user.service.persistence.domain.ConsentData;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class LoanDemand {

    private String loanApplicationId;
    private String parentLoanApplicationId;
    private final Integer loanAsked;
    private String fundingPurpose;
    private final LoanDuration duration;
    private final LoanCategory category;
    private final PersonalDetails personalDetails;
    private final CreditDetails creditDetails;
    private final EmploymentDetails employmentDetails;
    private final ContactData contactData;
    private final String ftsTransactionId;
    private final String dacId;
    private String requestIp;
    private String requestCountryCode;
    private final CustomDACData customDACData;
    private List<ConsentData> consents;
    private Set<Precheck> preChecks;

    private final DigitalAccountStatements digitalAccountStatements;

    private final String userUUID;

    public LoanDemand(
            String loanApplicationId,
            Integer loanAsked,
            String fundingPurpose,
            LoanDuration duration,
            LoanCategory category,
            PersonalDetails personalDetails,
            CreditDetails creditDetails,
            EmploymentDetails employmentDetails,
            ContactData contactData,
            DigitalAccountStatements digitalAccountStatements,
            String ftsTransactionId,
            String dacId,
            String requestIp,
            String requestCountryCode,
            CustomDACData customDACData,
            List<ConsentData> consents,
            String userUUID) {
        this.loanApplicationId = loanApplicationId;
        this.loanAsked = loanAsked;
        this.fundingPurpose = fundingPurpose;
        this.duration = duration;
        this.category = category;
        this.personalDetails = personalDetails;
        this.creditDetails = creditDetails;
        this.employmentDetails = employmentDetails;
        this.contactData = contactData;
        this.digitalAccountStatements = digitalAccountStatements;
        this.ftsTransactionId = ftsTransactionId;
        this.dacId = dacId;
        this.requestIp = requestIp;
        this.requestCountryCode = requestCountryCode;
        this.customDACData = customDACData;
        this.consents = consents;
        this.userUUID = userUUID;
    }

    public CreditDetails getCreditDetails() {
        return creditDetails;
    }

    public String getLoanApplicationId() {
        return loanApplicationId;
    }

    public LoanDuration getDuration() {
        return duration;
    }

    public Integer getLoanAsked() {
        return loanAsked;
    }

    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public ContactData getContactData() {
        return contactData;
    }

    public DigitalAccountStatements getDigitalAccountStatements() {
        return digitalAccountStatements;
    }

    public LoanCategory getCategory() {
        return category;
    }

    public EmploymentDetails getEmploymentDetails() {
        return this.employmentDetails;
    }

    public String getFtsTransactionId() {
        return this.ftsTransactionId;
    }

    public String getParentLoanApplicationId() {
        return parentLoanApplicationId;
    }

    public String getFundingPurpose() {
        return fundingPurpose;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public String getRequestCountryCode() {
        return requestCountryCode;
    }

    public Set<Precheck> getPreChecks() {
        return preChecks;
    }

    public String getDacId() {
        return dacId;
    }

    public CustomDACData getCustomDACData() {
        return customDACData;
    }

    public List<ConsentData> getConsents() {
        return consents;
    }

    public String getUserUUID() {
        return userUUID;
    }

    @Override
    public String toString() {
        return "LoanDemand{" +
                "id=" + loanApplicationId +
                ", loanAsked=" + loanAsked +
                ", duration=" + duration +
                '}';
    }

    public boolean isRecommended() {
        return parentLoanApplicationId != null;
    }

    public void addPreCheck(final Bank bank, final PreCheckEnum preCheckEnum, final boolean flag) {
        if (this.preChecks == null) {
            this.preChecks = new HashSet<>();
        }
        preChecks.add(new Precheck(bank.name(), preCheckEnum, flag));
    }
}
