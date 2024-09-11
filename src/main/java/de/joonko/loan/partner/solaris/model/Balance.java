package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

    private Double limit;
    private Double available;
    private String currency;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private Double balance;
}
