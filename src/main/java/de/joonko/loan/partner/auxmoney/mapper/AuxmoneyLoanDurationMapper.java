package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.partner.auxmoney.model.LoanDuration;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface AuxmoneyLoanDurationMapper {


    @ValueMappings({
            @ValueMapping(source = "SIX", target = "TWELVE"),
            @ValueMapping(source = "TWELVE", target = "TWELVE"),
            @ValueMapping(source = "EIGHTEEN", target = "TWENTY_FOUR"),
            @ValueMapping(source = "TWENTY_FOUR", target = "TWENTY_FOUR"),
            @ValueMapping(source = "THIRTY_SIX", target = "THIRTY_SIX"),
            @ValueMapping(source = "FORTY_EIGHT", target = "FORTY_EIGHT"),
            @ValueMapping(source = "SIXTY", target = "SIXTY"),
            @ValueMapping(source = "SEVENTY_TWO", target = "SEVENTY_TWO"),
            @ValueMapping(source = "EIGHTY_FOUR", target = "EIGHTY_FOUR"),
            @ValueMapping(source = "NINETY_SIX", target = "EIGHTY_FOUR"),
            @ValueMapping(source = "ONE_HUNDRED_EIGHT", target = "EIGHTY_FOUR"),
            @ValueMapping(source = "ONE_HUNDRED_TWENTY", target = "EIGHTY_FOUR")
    })
    LoanDuration toAuxmoneyLoanDuration(de.joonko.loan.offer.domain.LoanDuration loanDuration);
}
