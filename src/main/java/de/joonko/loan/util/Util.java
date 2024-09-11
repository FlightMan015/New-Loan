package de.joonko.loan.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Optional;

@UtilityClass
public class Util {

    public static int toEuroCent(BigDecimal euro) {
        BigDecimal bigDecimal = Optional.ofNullable(euro)
                .orElse(BigDecimal.ZERO);
        BigDecimal euroCent = bigDecimal.multiply(BigDecimal.valueOf(100));
        return euroCent.intValue();
    }
}
