package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Income {

    @JsonProperty("child_benefits")
    @Min(value = 0, message = "Child Benifits should be equal or grater than 0")
    private int benefit;

    @Min(value = 0, message = "Total should be equal or grater than 0")
    private int total;

    @Min(value = 0, message = "Other should be equal or grater than 0")
    private int other;
    @JsonProperty("net_income")
    @Min(value = 0, message = "Net Income should be equal or grater than 0")
    private int net;
}
