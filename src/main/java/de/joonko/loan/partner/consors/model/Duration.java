package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Duration implements Serializable {

    private String maximum;
    private String minimum;


}