package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class SantanderGetOfferLoanDetailRequestMapper {
    @Mapping(target = "kreditbetragNetto", source = "loanAsked")
    @Mapping(target = "verwendungszweck", expression = "java(de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.VwzType.STANDARD)")
    @Mapping(target = "rateneinzugZum", expression = "java(de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.RateneinzugType.ERSTER_EINES_MONATS)")
    @Mapping(target = "rsv", expression = "java(de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.RsvType.OHNE_RSV)")
    abstract ScbCapsBcoWSStub.FinanzierungXO toFinanzierung(LoanDemand loanDemand);
}
