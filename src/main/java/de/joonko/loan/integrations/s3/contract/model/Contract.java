package de.joonko.loan.integrations.s3.contract.model;

import lombok.Data;

@Data
public class Contract {
    private String name;
    private byte[] content;
    private MimeType mimeType;
}
