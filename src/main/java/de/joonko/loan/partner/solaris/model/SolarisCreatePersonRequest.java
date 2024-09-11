package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolarisCreatePersonRequest {

    @JsonProperty("salutation")
    @NotNull(message = "Salutation cannot be null")
    private String salutation;

    @JsonProperty("title")
    private String title;

    @JsonProperty("first_name")
    @NotNull(message = "First Name cannot be null")
    private String firstName;

    @JsonProperty("last_name")
    @NotNull(message = "Last name cannot be null")
    private String lastName;

    @JsonProperty("address")
    @NotNull(message = "Address cannot be null")
    @Valid
    private Address address;

    @JsonProperty("contact_address")
    private Address contactAddress;

    @Email(message = "Email is invalid")
    @NotNull(message = "Email must not be null")
    private String email;


    @JsonProperty("mobile_number")
    @NotNull(message = "Mobile number must not be null")
    private String mobileNumber;

    @JsonProperty("birth_name")
    private String birthName;

    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull(message = "Birthdate cannot be null")
    private LocalDate birthDate;

    @JsonProperty("birth_city")
    @NotNull(message = "Birth city must not be null") // To be confirmed from Solarisbank
    private String birthCity;

    @JsonProperty("birth_country")
    private String birthCountry;

    @JsonProperty("nationality")
    @NotNull(message = "Nationality cannot be null")
    private String nationality;

    @JsonProperty("employment_status")
    @NotNull(message = "EmploymentStatus cannot be null")
    private EmploymentStatus employmentStatus;

    @JsonProperty("job_title")
    private String jobTitle;

    @JsonProperty("fatca_relevant")
    private Boolean fatcaRelevant;

    @JsonProperty("fatca_crs_confirmed_at")
    private Instant fatcaCrsConfirmedAt;

    @JsonProperty("business_purpose")
    private String businessPurpose;

    @JsonProperty("terms_conditions_signed_at")
    @NotNull(message = "TermsConditionsSignedAt must not be null")
    private Instant termsConditionsSignedAt;

}
