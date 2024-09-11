package de.joonko.loan.offer.api;


import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static de.joonko.loan.common.Regex.ZIPCODE_REGEX;
import static de.joonko.loan.util.DateUtil.isBefore;
import static de.joonko.loan.util.DateUtil.isFirstDateIsBeforeTheLastDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentDetails implements Serializable {

    @NotNull(message = "Employment type must not be null")
    private EmploymentType employmentType;

    @NotNull(message = "Employer name must not be null")
    @Size(min = 1, max = 70, message = "Employer name is too long")
    private String employerName;

    @Valid
    @NotNull(message = "Employment since must not be null")
    private ShortDate employmentSince;

    @AssertTrue(message = "Employment since must be older than today")
    public boolean isEmploymentSinceValid() {
        try {
            if (this.employmentSince != null) {
                LocalDate employmentSinceDate = LocalDate.of(this.employmentSince.getYear(), this.employmentSince.getMonth(), 1);
                return isBefore(employmentSinceDate);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @NotNull(message = "Employer Street name must not be null")
    @Size(min = 1, max = 70, message = "Employer Street name is too long")
    private String streetName;

    @NotNull(message = "Employer postal code must not be null")
    @Pattern(regexp = ZIPCODE_REGEX, message = "Employer Postal code should be 5 digits")
    private String postCode;

    @NotNull(message = "Employer city must not be null")
    @Pattern(regexp = "^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð() ,.'-]+$", message = "Employer City is not valid")
    @Size(min = 1, max = 70, message = "Employer City is too long")
    private String city;

    private ShortDate professionEndDate;

    private String houseNumber;

    @AssertTrue(message = "if Profession end date is not null , it must be on the future than employmentSince")
    public boolean isProfessionEndDateValid() {
        if (this.employmentSince != null && this.professionEndDate != null && professionEndDate.getYear() != null && professionEndDate.getMonth() != null) {
            LocalDate employmentSinceDate = LocalDate.of(this.employmentSince.getYear(), this.employmentSince.getMonth(), 1);
            LocalDate dateToCheck = LocalDate.of(this.professionEndDate.getYear(), this.professionEndDate.getMonth(), 1);
            return isFirstDateIsBeforeTheLastDate(employmentSinceDate, dateToCheck);
        }
        return true;
    }
}
