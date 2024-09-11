package de.joonko.loan.identification.model.webid.uploaddocument;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadDocumentRequest {
    private String fileName;
    private String fileContent;
}
