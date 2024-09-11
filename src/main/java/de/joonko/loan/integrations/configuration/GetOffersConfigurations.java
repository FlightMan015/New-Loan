package de.joonko.loan.integrations.configuration;


import de.joonko.loan.common.domain.Bank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "offers")
@Data
@Validated
public class GetOffersConfigurations {

    @NotNull
    private Long offersValidityInDays;

    @NotNull
    private Long transactionalDataValidityInDays;

    @NotNull
    private Long userPersonalInformationValidityInHours;

    @NotNull
    private Long userPersonalInformationValidityInDays;

    @NotNull
    private Long userAdditionalInformationValidityInHours;

    @NotNull
    private Long staleFetchingOffersRequestDurationInSeconds;

    @NotNull
    private Long staleFetchingPersonalDetailsInSeconds;

    @NotNull
    private Long staleFetchingAdditionalUserInformationRequestInSeconds;

    @NotNull
    private Long staleFetchingTransactionalDataRequestInSeconds;

    @NotNull
    private Long staleFetchingSalaryAccountInSeconds;

    @NotNull
    private Long staleFetchingTransactionsClassificationInSeconds;

    @NotNull
    private Integer defaultAskedLoanAmount;

    @NotNull
    private Boolean loanRecommendationsEnabled;

    @NotNull
    @Builder.Default
    private List<Bank> listOfLoanRecommendationsEnabledBanks = List.of();

    @NotNull
    private Integer minimalLoanAmount;

    @NotNull
    private Integer recommendedLoanPercentage;

    @NotNull
    private Integer recommendedDisposableAmountMultiplier;

    @NotNull
    @Min(value = 0)
    @Max(value = 1)
    private Double minMaxFractionOutOfAskedAmountToDisplay;

    @NotNull
    private Integer userAdditionalInputRequiredMinAmount;

    @NotNull
    @Builder.Default
    private Boolean newImplementationEnabled = false;
}


