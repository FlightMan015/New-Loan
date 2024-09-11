package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SwkCreditUserBankCardsRequestMapper {

    @Mapping(target = "ecCard", constant = SwkDefaults.SWK_TRUE)
    @Mapping(target = "creditCard", source = ".", qualifiedByName = "creditCardMapper")
    CreditApplicationServiceStub.BankCardsInformation toBankCardsInformation(Integer numberOfCreditCard);

    @Named("creditCardMapper")
    default boolean creditCardMapper(Integer numberOfCreditCard) {
        if (numberOfCreditCard != null)
            return numberOfCreditCard > 0;
        return false;
    }
}
