package de.joonko.loan.offer.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails implements Serializable {
    @NotNull(message = "Name on dac account must not be null")
    private String nameOnAccount;

    @NotNull(message = "Account iban must not be null")
    private String iban;

    @NotNull(message = "Account bic must not be null")
    private String bic;

    @NotNull(message = "Account balance must not be null")
    private Double balance;

    @NotNull(message = "Amount limit of account must not be null")
    private Double limit;

    @NotNull(message = "Account balance date must not be null")
    private LocalDate balanceDate;

    @NotNull(message = "Account currency must not be null")
    private String currency;

    @JsonIgnore
    private List<DacTransaction> transactions;

    @NotNull(message = "days must not be null")
    private int days;

    @NotNull(message = "cratedAt must not be null")
    private LocalDateTime createdAt;

    private Boolean isJointlyManaged;

    private String bankName;
}
