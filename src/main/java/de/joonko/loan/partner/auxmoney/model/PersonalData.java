package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalData {

    @NotNull(message = "Address is Mandatory")
    private Salutation address;

    @JsonProperty("family_status")
    @NotNull(message = "Family Status is mandatory")
    private FamilyStatus familyStatus;

    @JsonProperty("occupation")
    private Occupation occupation;

    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull(message = "Birth Date is mandatory")
    private LocalDate birthDate;

    @JsonProperty("has_credit_card")
    @NotNull(message = "Has Credit card is mandatory")
    private Integer hasCreditCard;

    @JsonProperty("has_real_estate")
    @NotNull(message = "Has ral eastate is mandatory")
    private Integer hasRealEstate;

    @NotBlank(message = "Forename is mandatory")
    // @NotNull
    private String forename;

    @NotNull(message = "Nationality is mandatory")
    private String nationality;

    @JsonProperty("has_ec_card")
    @NotNull(message = "Has ec card is mandatory")
    private Integer hasEcCard;

    @NotBlank(message = "Surname is mandatory")
    private String surname;

    @JsonProperty("main_earner")
    @NotNull(message = "Main earner is mandatory")
    private MainEarner mainEarner;

    @JsonProperty("car_owner")
    private int carOwner;

    @JsonProperty("housing_type")
    @NotNull
    private HousingType housingType;
    @JsonProperty("tax_identification_number")
    private Long taxIdentificationNumber;
}
