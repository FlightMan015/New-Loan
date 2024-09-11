package de.joonko.loan.userdata.domain.model;

import de.joonko.loan.offer.api.EmploymentType;
import de.joonko.loan.offer.api.ShortDate;
import de.joonko.loan.offer.api.validator.ValidTaxId;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.time.LocalDate;

import static de.joonko.loan.common.Regex.ZIPCODE_REGEX;
import static de.joonko.loan.util.DateUtil.isFirstDateIsBeforeTheLastDate;

@Data
public class UserEmployment {
    private boolean valid;

    @NotNull(message = "Employment type must not be null")
    private EmploymentType employmentType;

    @NotNull(message = "Employer name must not be null")
    @Size(min = 1, max = 70, message = "Employer name is too long")
    private String employerName;

    @Valid
    @NotNull(message = "Employment since must not be null")
    private ShortDate employmentSince;

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

    @ValidTaxId
    private String taxId;

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
