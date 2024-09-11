package de.joonko.loan.webhooks.postbank.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.util.Objects.nonNull;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditResultWithContracts {
    @NotNull
    private Integer contractNumber;

    private ContractState contractState;
    private String dateOfFirstRate;
    private String dateOfLastRate;
    private String decisionText;
    private Integer duration;
    private BigDecimal effectiveInterest;
    private BigDecimal freeIncome;
    private BigDecimal interestRate;

    private SchufaInformations schufaInformations;
    private BigDecimal lastRate;
    private BigDecimal loanAmount;
    private BigDecimal loanAmountTotal;
    private BigDecimal monthlyRate;
    private BigDecimal nominalInterest;

    @NotNull
    private String partnerContractNumber;

    private BigDecimal residualDebtAmount;
    private BigDecimal serviceFee;
    private Boolean alternativeOffer;
    private String insurance;
    private Integer score;
    private String rapClass;

    private DebtorInformation debtorInformation;

    private String contract;

    private String contractShort;

    public boolean hasContracts() {
        return nonNull(contract) || nonNull(contractShort);
    }
}
