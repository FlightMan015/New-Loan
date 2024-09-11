package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class AuxmoneySingleCallResponse implements Serializable {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("credit_id")
    private String creditId;

    @JsonProperty("is_success")
    private Boolean isSuccess;

    @JsonProperty("is_error")
    private Boolean isError;

    @JsonProperty("loan")
    private Double loan;

    @JsonProperty("loan_asked")
    private Integer loanAsked;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("rate")
    private Double rate;

    @JsonProperty("eff_rate")
    private Double effRate;

    @JsonProperty("insurance_fee")
    private int insuranceFee;

    @JsonProperty("interest")
    private Double interest;

    @JsonProperty("installment_amount")
    private Double installmentAmount;

    @JsonProperty("manual_quality_assurance")
    private Boolean manualQualityAssurance;

    @JsonProperty("ekf_url")
    private String ekfUrl;

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("skip_ekf")
    private Boolean skipEkf;

    @JsonProperty("idd_rkv_presale_url")
    private String iddRkvPresaleUrl;

    @JsonProperty("rkv")
    private Integer rkv;

    @JsonProperty("violations")
    private List<Violations> violations;

    @JsonProperty("total_credit_amount")
    private Double totalCreditAmount;
}
