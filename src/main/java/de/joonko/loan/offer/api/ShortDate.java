package de.joonko.loan.offer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortDate implements Serializable {
    @NotNull(message = "Month must not be null")
    private Integer month;

    @NotNull(message = "Year must not be null")
    private Integer year;

    public boolean isOlderThanMonth(int months) {
        LocalDate thisDate = LocalDate.of(this.year, this.month, 1);
        LocalDate oldDate = LocalDate.now()
                .minusMonths(months)
                .withDayOfMonth(1);
        return thisDate.isBefore(oldDate) || thisDate.isEqual(oldDate);

    }
}
