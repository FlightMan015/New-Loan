package de.joonko.loan.partner.aion.model.offerchoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDetails {

    private String fileId;
    private String fileName;
    private String fileContent;
}
