package de.joonko.loan.config;


import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub;
import lombok.Data;
import org.apache.axis2.AxisFault;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "santander")
@Data
@Validated
public class SantanderConfig {

    @NotBlank
    private String bcoEndpoint;

    @NotBlank
    private String docEndpoint;

    @NotBlank
    private String webIdHost;

    @NotBlank
    private String webIdAuthToken;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Min(value=0)
    @NotNull
    private Integer acceptedApplicantMaxLoanAmount;

    @Min(value=0)
    @NotNull
    private Integer acceptedApplicantMinLoanAmount;

    @Min(value=0)
    @NotNull
    private Integer acceptedApplicantMinProbationInMonths;

    @Min(value=0)
    @NotNull
    private Integer acceptedApplicantMinIncome;

    @Bean
    public ScbCapsBcoWSStub offerStub() throws AxisFault {
        return new ScbCapsBcoWSStub(bcoEndpoint);
    }

    @Bean
    public ScbCapsDocsWSStub documentStub() throws AxisFault {
        return new ScbCapsDocsWSStub(docEndpoint);
    }
}
