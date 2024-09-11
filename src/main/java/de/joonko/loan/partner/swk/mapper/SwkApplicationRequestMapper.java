package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.DomainDefault;
import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {
        SwkClientIdentificationRequestMapper.class,
        SwkAccountRequestMapper.class,
        SwkCreditUserArrayMapper.class,
        SwkCreditUsersRequestMapper.class})
public abstract class SwkApplicationRequestMapper {


    @Mapping(target = "clientIdentification", source = ".")
    @Mapping(target = "collectionAccount", source = ".")
    @Mapping(target = "paymentAccount", source = ".")
    @Mapping(target = "amount", source = "loanAsked")
    @Mapping(target = "balloonInstallment", ignore = true)
    @Mapping(target = "calculationMode", ignore = true)
    @Mapping(target = "collectionDayOfMonth", constant = DomainDefault.COLLECTION_DAY_OF_MONTH)
    @Mapping(target = "creditUsers", source = ".")
    @Mapping(target = "creditTransfers", ignore = true)//TODO
    @Mapping(target = "disagioPercentage", ignore = true)
    @Mapping(target = "duration", constant = SwkDefaults.SWK_CREDIT_DURATION)
    @Mapping(target = "id", ignore = true)//TODO
    @Mapping(target = "dateOfBirth", source = "personalDetails.birthDate")
    @Mapping(target = "extraInfo", source = "ftsTransactionId", qualifiedByName = "toExtraInfoArray")
    @Mapping(target = "gender", source = "personalDetails.gender", qualifiedByName = "toGender")
    @Mapping(target = "intendedUse", constant = SwkDefaults.SONSTIGES)
    @Mapping(target = "currency", constant = DomainDefault.CURRENCY)
    public abstract CreditApplicationServiceStub.ApplicationRequest toApplicationRequest(LoanDemand loanDemand);


    @Named("toGender")
    String toGender(Gender gender) {
        return gender.equals(Gender.MALE) ? SwkDefaults.SWK_MALE : SwkDefaults.SWK_FEMALE;
    }

    @Named("toExtraInfoArray")
    CreditApplicationServiceStub.Property[] toExtraInfoArray(String transactionId) {
        CreditApplicationServiceStub.Property property = new CreditApplicationServiceStub.Property();
        CreditApplicationServiceStub.Property[] properties = new CreditApplicationServiceStub.Property[1];

        property.setName("transactionId");
        property.setValue(transactionId);
        properties[0] = property;
        return properties;
    }
}
