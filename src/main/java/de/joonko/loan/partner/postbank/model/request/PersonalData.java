package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"anrede", "akademgrad", "vorname", "name",
        "geburtsdatum", "geburtsort", "staatsangehoerigkeit",
        "familienstand", "email", "telefon", "ratenschutztarif", "personen",
        "kinder", "gemeinsamerhaushalt", "tin",
        "krediterfahrung", "beruf", "adresse", "einkuenfte", "ausgaben"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalData {

    @JacksonXmlProperty(localName = "anrede")
    private int gender;

    @JacksonXmlProperty(localName = "akademgrad")
    @Builder.Default
    private Integer degree = 0;

    @JacksonXmlProperty(localName = "vorname")
    private String firstName;

    @JacksonXmlProperty(localName = "name")
    private String lastName;

    @JacksonXmlProperty(localName = "geburtsdatum")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthDate;

    @JacksonXmlProperty(localName = "geburtsort")
    private String birthCity;

    @JacksonXmlProperty(localName = "staatsangehoerigkeit")
    private String nationality;

    @JacksonXmlProperty(localName = "familienstand")
    private Integer maritalStatus;

    @JacksonXmlProperty()
    private String email;

    @JacksonXmlProperty(localName = "telefon")
    private String phoneNumber;

    @JacksonXmlProperty(localName = "ratenschutztarif")
    @Builder.Default
    private Integer paymentProtectionInsurance = 0;

    @JacksonXmlProperty(localName = "personen")
    @Builder.Default
    private Integer countOfLoanApplicants = 1;

    @JacksonXmlProperty(localName = "kinder")
    private Integer numberOfChildren;

    @JacksonXmlProperty(localName = "gemeinsamerhaushalt")
    @Builder.Default
    private Integer applicantsLiveInJointHousehold = 0;

    @JacksonXmlProperty(localName = "tin")
    private String taxId;

    @JacksonXmlProperty(localName = "krediterfahrung")
    @Builder.Default
    private Integer creditExperience = 0;

    @JacksonXmlProperty(localName = "beruf")
    @NotNull
    private EmploymentData employmentData;

    @JacksonXmlProperty(localName = "adresse")
    @JacksonXmlElementWrapper(useWrapping = false)
    @NotNull
    @NotEmpty
    private List<AddressData> addressDataList;

    @JacksonXmlProperty(localName = "einkuenfte")
    @NotNull
    private IncomeData incomeData;

    @JacksonXmlProperty(localName = "ausgaben")
    @NotNull
    private ExpensesData expensesData;
}
