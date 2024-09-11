
package de.joonko.loan.identification.model.webid.useractionrequest;

import lombok.Data;

@Data
public class IdDocument {
    private String documentType;
    private String authority;
    private String dateOfIssue;
    private String dateOfExpiry;
    private String nationality;
    private String idNumber;
    private String mrz;
    private String nameAtBirth;
    private String placeOfBirth;
}
