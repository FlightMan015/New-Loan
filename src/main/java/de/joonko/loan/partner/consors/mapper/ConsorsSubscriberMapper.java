package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.Nationality;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.consors.ConsorsDefaults;
import de.joonko.loan.partner.consors.model.HousingSituation;
import de.joonko.loan.partner.consors.model.RolePlaying;
import de.joonko.loan.partner.consors.model.Subscriber;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Mapper(componentModel = "spring",
        uses = {EmploymentDetailsMapper.class,
                ConsentsMapper.class,
                ConsorsIncomeMapper.class,
                ConsorsExpenseMapper.class,
                ConsorsFamilySituationMapper.class,
                ContactAddressMapper.class,
                ConsorsGenderMapper.class,
                ConsorsPreviousAddressMapper.class,
                StringMapper.class
        })
public abstract class ConsorsSubscriberMapper {

    @Autowired
    private GetOffersConfigurations getOffersConfigurations;

    @Mapping(target = "gender", source = "personalDetails.gender")
    @Mapping(target = "academicTitle", qualifiedByName = "trimWhiteSpaces", ignore = true)
    @Mapping(target = "firstName", expression = "java(de.joonko.loan.common.utils.CommonUtils.normalizeString(loanDemand.getPersonalDetails().getFirstName()))")
    @Mapping(target = "lastName", expression = "java(de.joonko.loan.common.utils.CommonUtils.normalizeString(loanDemand.getPersonalDetails().getLastName()))")
    @Mapping(target = "nobilityTitle", ignore = true)
    @Mapping(target = "dateOfBirth", source = "personalDetails.birthDate")
    @Mapping(target = "birthName", qualifiedByName = "trimWhiteSpaces", ignore = true)
    @Mapping(target = "nationality", expression = "java(padZeroes(loanDemand.getPersonalDetails().getNationality()))")
    @Mapping(target = "countryOfBirth", constant = ConsorsDefaults.COUNTRY_OF_BIRTH)
    @Mapping(target = "housingSituation", source = "personalDetails", qualifiedByName = "housingTypeMapper")
    @Mapping(target = "subscriberIdentifierExternal", ignore = true)
    @Mapping(target = "subscriberIdentifierInternal", ignore = true)
    @Mapping(target = "identity", ignore = true)
    @Mapping(target = "interestCap", ignore = true)
    @Mapping(target = "consents", source = ".")
    @Mapping(target = "familySituation", source = "personalDetails.familyStatus")
    @Mapping(target = "placeOfBirth", expression = "java(de.joonko.loan.common.utils.CommonUtils.normalizeString(loanDemand.getPersonalDetails().getPlaceOfBirth()))")
    @Mapping(target = "numberOfChildren", source = "personalDetails.numberOfChildren")
    @Mapping(target = "legitimationInfo", ignore = true)
    @Mapping(target = "contactAddress", source = "contactData")
    @Mapping(target = "previousAddress", source = "contactData.previousAddress")
    @Mapping(target = "employmentDetails", source = "employmentDetails")
    @Mapping(target = "financialLimit", source = "loanAsked", qualifiedByName = "calculateLimit")
    @Mapping(target = "customerRating", ignore = true)
    @Mapping(target = "germanTaxIdentifier", source = "personalDetails.taxId")
    @Mapping(target = "income", source = "personalDetails.finance.income")
    @Mapping(target = "expense", source = "personalDetails")
    @Mapping(target = "rolePlaying", source = "personalDetails.mainEarner", qualifiedByName = "rolePlayingMapper")
    abstract Subscriber toLoanProviderSubscriber(LoanDemand loanDemand);

    protected String padZeroes(Nationality country) {
        if (null != country) {
            return String.format("%03d", country.getCountryCode().getNumeric());
        }
        return "000";
    }

    @Named("rolePlayingMapper")
    protected RolePlaying rolePlayingMapper(boolean mainEarner) {
        return mainEarner ? RolePlaying.MAIN : RolePlaying.PARTNER;
    }


    @Named("housingTypeMapper")
    protected HousingSituation housingTypeMapper(PersonalDetails personalDetails) {
        return getHousingType(personalDetails.getFinance().getExpenses().getMortgages(), personalDetails.getHousingType());
    }

    @Named("calculateLimit")
    protected Integer calculateLimit(Integer loanAsked) {
        int max = (int) (loanAsked * (1 + (getOffersConfigurations.getMinMaxFractionOutOfAskedAmountToDisplay())));
        return (int) Math.floor(max / 500) * 500;
    }

    private HousingSituation getHousingType(BigDecimal mortgages, HousingType housingType) {
        if (housingType.equals(HousingType.RENT)) {
            return HousingSituation.RENTER;
        }
        if (mortgages.compareTo(new BigDecimal(0)) > 0) {
            return HousingSituation.OWNER_WITH_MORTGAGE;
        }
        return HousingSituation.OWNER_WITHOUT_MORTGAGE;
    }

}
