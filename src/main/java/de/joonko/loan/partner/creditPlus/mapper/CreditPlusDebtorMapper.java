package de.joonko.loan.partner.creditPlus.mapper;


import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        CreditPlusDebtorWorkDataMapper.class,
        CreditPlusDebtorFinancialDataMapper.class,
        CreditPlusDebtorPersonalDataMapper.class,
        CreditPlusDebtorDacDataMapper.class
})
public interface CreditPlusDebtorMapper {

    @Mapping(target = "workdata", source = "employmentDetails")
    @Mapping(target = "rsvType", constant = CreditPlusDefaults.RSV_TYPE)
    @Mapping(target = "replacementType", constant = CreditPlusDefaults.REPLACEMENT_TYPE)
    @Mapping(target = "financialData", source = ".")
    @Mapping(target = "personalData", source = ".")
    @Mapping(target = "dacData", source = ".")
    EfinComparerServiceStub.DebtorDac toDebtor(LoanDemand loanDemand);
}
