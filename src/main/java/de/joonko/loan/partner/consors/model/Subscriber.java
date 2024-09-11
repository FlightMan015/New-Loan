package de.joonko.loan.partner.consors.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Subscriber {

    private Income income; // GetOffersRequest.income

    private Gender gender; // can be derived from GetOffersRequest.Salutation

    private AcademicTitle academicTitle; // optional and not supported by UI

    private NobilityTitle nobilityTitle; // optional and not supported by UI

    private String lastName; // GetOffersRequest.PersonalDetails.surname

    private String birthName; // optional and not supported by UI

    private String firstName; // GetOffersRequest.PersonalDetails.forename
    @JsonProperty("dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateOfBirth; // GetOffersRequest.PersonalDetails.birthDate

    private String nationality; // required - can we infer this by contact data?

    private String countryOfBirth; // optional - not supported by UI

    //private String familyStatus; // required GetOffersRequest.PersonalDetails.FamilyStatus

    private RolePlaying rolePlaying; // currently always 'MAIN'

    private ContactAddress contactAddress; // required GetOffersRequest.ContactData (what means validFrom?)

    private PreviousAddress previousAddress; // optional and not supported by UI

    private LegitimationInfo legitimationInfo; // optional and KYC not yet supported by us

    private EmploymentDetails employmentDetails; // required (at least profession)

    private HousingSituation housingSituation; // required - GetOffersRequest.PersonalDetails.HousingType (OWNERSHIP, RENT, WITH_THE_PARENTS)

    private Expense expense; // warmRent required if housingSituation is one of OWNER_WITH_MORTGAGE, EMPLOYER or RENTER - GetOffersRequest.Expenses

    private String subscriberIdentifierExternal; // optional - what is it?

    private String subscriberIdentifierInternal; // optional - what is it?

    private Identity identity; // optional - mandatory for non EU nationals - can we default this?

    private Double interestCap; // optional - do we support this?

    private Consents consents; // required (schufa) - part of AGBs?

    private FamilySituation familySituation;

    private String placeOfBirth;
    private int numberOfChildren;
    private Integer financialLimit;
    private Double customerRating;
    private String germanTaxIdentifier;


}
