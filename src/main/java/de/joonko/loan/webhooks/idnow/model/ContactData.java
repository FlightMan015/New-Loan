package de.joonko.loan.webhooks.idnow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactData {
    private String email;
    @JsonProperty("mobilephone")
    private String mobilePhone;
}
