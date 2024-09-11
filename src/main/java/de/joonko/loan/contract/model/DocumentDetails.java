package de.joonko.loan.contract.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DocumentDetails {
    private String key;
    private String name;
}
