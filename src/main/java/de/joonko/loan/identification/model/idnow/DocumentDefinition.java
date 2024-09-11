package de.joonko.loan.identification.model.idnow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DocumentDefinition {
    private String name;
    private String identifier;
    private String mimeType;
    private Boolean optional;
}
