package de.joonko.loan.webhooks.idnow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    @JsonProperty("firstname")
    private UserAttribute<String> firstName;
    @JsonProperty("lastname")
    private UserAttribute<String> lastName;
    private UserAttribute<LocalDate> birthday;
    @JsonProperty("birthname")
    private UserAttribute<String> birthName;
    @JsonProperty("birthplace")
    private UserAttribute<String> birthPlace;
    private UserAttribute<String> title;
    private UserAttribute<String> gender;
    private UserAttribute<String> nationality;
    private Address address;
}
