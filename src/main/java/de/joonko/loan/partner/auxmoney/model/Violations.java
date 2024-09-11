package de.joonko.loan.partner.auxmoney.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Violations {
    private String code;
    private String message;
    private List<String> parameters;
}
