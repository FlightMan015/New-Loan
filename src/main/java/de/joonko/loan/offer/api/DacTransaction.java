package de.joonko.loan.offer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DacTransaction implements Serializable {

    @NotNull(message = "Dac transaction amount must not be null")
    private Double amount;

    private String iban;

    private String bic;

    private String purpose;

    private String categoryId;

    private String counterHolder;

    @NotNull(message = "Dac transaction date must not be null")
    private LocalDate bookingDate;

    private Boolean isPreBooked;
}
