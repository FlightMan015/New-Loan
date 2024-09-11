package de.joonko.loan.partner.auxmoney.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Offers implements Serializable {
    private Integer amount;
    private Integer rkv;
    private List<Price> prices;
}
