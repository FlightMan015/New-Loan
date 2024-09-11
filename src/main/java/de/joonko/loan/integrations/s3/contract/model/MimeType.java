package de.joonko.loan.integrations.s3.contract.model;

import lombok.Getter;

@Getter
public enum MimeType {
    PDF("application/pdf", "pdf");

    private final String label;
    private final String fileExtension;

    MimeType(String label, String fileExtension) {
        this.label = label;
        this.fileExtension = fileExtension;
    }
}
