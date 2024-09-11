package de.joonko.loan.contract.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PresignedDocumentDetails {
    private String url;
    private String name;
}
