package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub.CreditUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = {
        SwkCreditUserBankCardsRequestMapper.class,
        SwkCreditUserBudgetInformationRequestMapper.class,
        SwkCreditUserEmploymentInformationRequestMapper.class,
        SwkCreditUserFamilyInformationRequestMapper.class,
        SwkCreditUserPersonRequestMapper.class,
        SwkCreditUserResidencyInformationRequestMapper.class
})
public interface SwkCreditUsersRequestMapper {

    @Mapping(target = "agreedToSchufaRequest", constant = SwkDefaults.SWK_TRUE)
    @Mapping(target = "bankCardsInformation", source = "personalDetails.numberOfCreditCard")
    @Mapping(target = "budgetInformation", source = "personalDetails")
    @Mapping(target = "carInformation", qualifiedByName = "toCarInformation", source = ".")
    @Mapping(target = "employmentInformation", source = "employmentDetails")
    @Mapping(target = "familyInformation", source = "personalDetails")
    @Mapping(target = "person", source = ".")
    @Mapping(target = "residencyInformation", source = "personalDetails")
    CreditUser toCreditUsers(LoanDemand loanDemand);

    @Named("toCarInformation")
    default CreditApplicationServiceStub.CarInformation toCarInformation(LoanDemand loanDemand) {
        CreditApplicationServiceStub.CarInformation carInformation = new CreditApplicationServiceStub.CarInformation();
        if (loanDemand.getCustomDACData().getCarInformation()) {
            carInformation.setCar(true);
            carInformation.setYearOfManufacturing(SwkDefaults.CAR_YEAR_OF_MAKING);
            carInformation.setPower(SwkDefaults.CAR_POWER);
        }
        return carInformation;
    }
}
