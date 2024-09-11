package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.solaris.model.SolarisCreatePersonRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import static de.joonko.loan.util.SolarisBankConstant.MR;
import static de.joonko.loan.util.SolarisBankConstant.MS;

@Mapper(componentModel = "spring", uses = {SolarisAddressMapper.class, SolarisContactAddressMapper.class, SolarisEmploymentStatusMapper.class})
public abstract class SolarisCreatePersonRequestMapper {

    @Autowired
    public IdentificationPropConfig identificationPropConfig;

    @Mapping(target = "firstName", source = ".", qualifiedByName = "toFirstName")
    @Mapping(target = "lastName", source = "personalDetails.lastName")
    @Mapping(target = "email", source = "contactData.email.emailString")
    @Mapping(target = "mobileNumber", source = ".", qualifiedByName = "getMobileNumberInFormat")
    @Mapping(target = "salutation", source = ".", qualifiedByName = "getSalutation")
    @Mapping(target = "birthDate", source = "personalDetails.birthDate")
    @Mapping(target = "nationality", source = "personalDetails.nationality.countryCode")
    @Mapping(target = "contactAddress", source = "contactData.previousAddress")
    @Mapping(target = "address", source = "contactData")
    @Mapping(target = "employmentStatus", source = "employmentDetails.employmentType")
    @Mapping(target = "termsConditionsSignedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "birthCity", source = "personalDetails.placeOfBirth")
    public abstract SolarisCreatePersonRequest toSolarisRequest(LoanDemand loanDemand);

    @Named("getMobileNumberInFormat")
    String getMobileNumberInFormat(LoanDemand loanDemand) {
        return "+" + loanDemand.getContactData().getMobile();
    }

    @Named("getSalutation")
    String getSalutation(LoanDemand loanDemand) {

        return loanDemand.getPersonalDetails()
                .getGender()
                .equals(Gender.MALE) ? MR : MS;
    }

    @Named("toFirstName")
    String toFirstName(LoanDemand loanDemand) {
        if (identificationPropConfig.getAutoidentification()) {
            return "X-MANUALTEST-HAPPYPATH";
        }
        return loanDemand.getPersonalDetails().getFirstName();
    }
}
