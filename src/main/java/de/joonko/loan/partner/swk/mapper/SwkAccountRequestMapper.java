package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub.BankingInformation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SwkAccountRequestMapper {

    @Mapping(target = "accountNumber", source = "digitalAccountStatements.iban")
    @Mapping(target = "accountHolder", source = "personalDetails", qualifiedByName = "toAccountHolder")
    BankingInformation toBankingAccount(LoanDemand LoanDemand);

    @Named("toAccountHolder")
    default String toAccountHolder(PersonalDetails personalDetails) {
        return personalDetails.getFirstName()
                .concat(" ")
                .concat(personalDetails.getLastName());
    }

}
