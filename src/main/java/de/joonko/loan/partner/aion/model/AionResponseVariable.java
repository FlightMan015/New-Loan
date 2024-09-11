package de.joonko.loan.partner.aion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AionResponseVariable<T> {

    private AionResponseValueType name;

    private T value;
}
