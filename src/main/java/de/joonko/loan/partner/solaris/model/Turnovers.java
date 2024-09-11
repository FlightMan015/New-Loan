package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Turnovers {

    @JsonProperty("booking_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date bookingDate;

    private Double amount;
    private String currency;
    private List<String> purpose;

    @JsonProperty("counter_iban")
    private String counterIban;

    @JsonProperty("counter_bic")
    private String counterBic;

    @JsonProperty("counter_holder")
    private String counterHolder;

    @JsonProperty("prebooked")
    private Boolean preBooked;
    private List<String> tags;
}
