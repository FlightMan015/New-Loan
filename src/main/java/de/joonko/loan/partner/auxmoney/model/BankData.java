package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.joonko.loan.validator.ValidIBAN;
import lombok.Data;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class BankData {

    @ValidIBAN
    private String iban;

    @NotNull(message = "Bic is mandatory")
    private String bic;
}
