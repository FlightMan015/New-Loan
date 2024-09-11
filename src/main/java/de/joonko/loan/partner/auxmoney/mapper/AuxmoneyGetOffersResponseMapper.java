package de.joonko.loan.partner.auxmoney.mapper;


import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
interface AuxmoneyGetOffersResponseMapper {

    default List<LoanOffer> fromAuxmoneyResponse(List<AuxmoneySingleCallResponse> auxmoneySingleCallResponse) {
        return auxmoneySingleCallResponse.stream()
                .map(this::toSingleOffer)
                .collect(Collectors.toList());


    }

    private LoanOffer toSingleOffer(AuxmoneySingleCallResponse auxmoneySingleCallResponse) {
        return new LoanOffer(auxmoneySingleCallResponse.getLoanAsked(),
                auxmoneySingleCallResponse.getDuration(),
                BigDecimal.valueOf(auxmoneySingleCallResponse.getEffRate()),
                BigDecimal.valueOf(auxmoneySingleCallResponse.getRate()),
                BigDecimal.valueOf(auxmoneySingleCallResponse.getInstallmentAmount()),
                BigDecimal.valueOf(auxmoneySingleCallResponse.getTotalCreditAmount()),
                new LoanProvider(Bank.AUXMONEY.label));
    }


}
