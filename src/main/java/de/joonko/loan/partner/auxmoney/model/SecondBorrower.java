package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.joonko.loan.offer.domain.Income;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecondBorrower {
    @JsonProperty("income")
    private Income income;
    @JsonProperty("relationship_to_first_borrower")
    private String relationshipToFirstBorrower;
    @JsonProperty("same_household")
    private boolean sameHousehold;
    @JsonProperty("contact_data")
    private SecondBorrowerContactData contactData;
    private Expenses expenses;


}
