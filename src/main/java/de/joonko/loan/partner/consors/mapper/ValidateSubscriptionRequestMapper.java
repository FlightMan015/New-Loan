package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.consors.model.ValidateSubscriptionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ConsorsBankAccountMapper.class, ConsorsSubscriberMapper.class, ConsorsKycPurposeOfLoanMapper.class})
public interface ValidateSubscriptionRequestMapper {

    @Mapping(target = "bankAccount", source = "digitalAccountStatements")
    @Mapping(target = "isEsigned", ignore = true)
    @Mapping(target = "subscriptionIdentifierExternal", expression = "java(de.joonko.loan.db.vo.ExternalIdentifiers.consorsExternalIdentifierExternalIdentifierFromApplicationId(loanDemand.getLoanApplicationId()))")
    @Mapping(target = "externalLoansAmount", ignore = true)
    @Mapping(target = "kycPurposeOfLoan", source = "category")
    @Mapping(target = "subscriptionBasketInfo", ignore = true)
    @Mapping(target = "subscriber", source = ".")
    ValidateSubscriptionRequest toLoanProviderRequest(LoanDemand loanDemand);

    default Integer toLoanTerm(LoanDuration loanDuration) {
        return loanDuration.getValue();
    }
}
