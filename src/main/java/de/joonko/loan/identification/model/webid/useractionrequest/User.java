
package de.joonko.loan.identification.model.webid.useractionrequest;

import lombok.Data;

@Data
public class User {
    private String title;
    private String sex;
    private String firstname;
    private String lastname;
    private String dateOfBirth;
    private Address address;
    private Contact contact;
}
