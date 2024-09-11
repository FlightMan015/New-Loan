package de.joonko.loan.identification.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Document {

    private byte[] content;
    private String documentId;
}
