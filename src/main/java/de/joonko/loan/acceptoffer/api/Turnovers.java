package de.joonko.loan.acceptoffer.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Turnovers {

    private List<String> tags;
    private List<String> purpose;

    @JsonProperty("prebooked")
    private Boolean preBooked;

    private String currency;

    @JsonProperty("counter_iban")
    private String counterIban;

    @JsonProperty("counter_holder")
    private String counterHolder;

    @JsonProperty("counter_bic")
    private String counterBic;

    @JsonProperty("booking_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    private Double amount;

}
