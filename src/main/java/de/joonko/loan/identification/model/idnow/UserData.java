package de.joonko.loan.identification.model.idnow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserData {

    private Birthday birthday;

    private Address address;

    @JsonProperty("firstname")
    private StatusValue firstName;

    @JsonProperty("birthplace")
    private StatusValue birthplace;

    @JsonProperty("nationality")
    private StatusValue nationality;

    @JsonProperty("gender")
    private StatusValue gender;

    @JsonProperty("identlanguage")
    private StatusValue identlanguage;

    @JsonProperty("title")
    private StatusValue title;

    @JsonProperty("lastname")
    private StatusValue lastname;
}
