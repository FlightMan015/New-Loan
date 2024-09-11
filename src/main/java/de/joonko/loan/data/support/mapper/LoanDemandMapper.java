package de.joonko.loan.data.support.mapper;

import de.joonko.loan.avro.dto.loan_demand.LoanDemandMessage;
import de.joonko.loan.offer.api.LoanDemandRequest;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface LoanDemandMapper extends BaseMapper {

    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "timestamp", expression = "java(generateTimestamp())")
    @Mapping(source = "loanDemandRequest.isRequestedBonifyLoans", target = "askedForBonifyLoans")
    LoanDemandMessage mapLoanDemand(Long userId, LoanDemandRequest loanDemandRequest);
}

