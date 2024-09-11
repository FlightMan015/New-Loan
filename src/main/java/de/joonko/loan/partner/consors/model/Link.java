package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Link {
    private String name;
    private String rel;
    private String href;
    private String method;
}
