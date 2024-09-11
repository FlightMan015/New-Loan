package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkRelation {
    private String name;
    private String href;
    private String method;
    private String rel;

    public boolean hasRel(String rel) {
        return rel.equals(this.rel);
    }
}
