package de.joonko.loan.identification.model.idnow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class IdentificationDocument {

    private StatusValue country;
    private StatusValue number;
    private StatusValue issuedby;
    private StatusValue dateissued;
    private StatusValue type;
    private StatusValue validuntil;

}
