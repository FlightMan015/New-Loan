package de.joonko.loan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "swk")
@Data
@Validated
public class SwkConfig {
    @NotBlank
    private String partnerid;
    @NotBlank
    private String password;
    @NotNull
    private Integer requestType;
    @NotBlank
    private String username;
    @NotBlank
    private String host;
    @NotBlank
    private String creditApplicationService;
    @NotBlank
    private String pdfGenerationService;
    @NotBlank
    private String preCheckServiceHttpSoap12Endpoint;

    @Min(value=18)
    @Max(value=100)
    @NotNull
    private Integer acceptedApplicantMinAge;
    @Min(value=18)
    @Max(value=100)
    @NotNull
    private Integer acceptedApplicantMaxAge;
    @Min(value=0)
    @NotNull
    private Integer acceptedApplicantMaxGamblingAmountInLast90Days;
    @Min(value=0)
    @NotNull
    private Integer acceptedApplicantMinIncome;
    @NotNull
    private Double acceptedCashWithdrawalsOutOfTotalIncomeInLast90DaysRatio;
    @NotNull
    private Integer acceptedApplicantMinProbationInMonths;
    @NotNull
    private Integer acceptedApplicantMinLoanAmount;
}
