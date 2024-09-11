package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.offer.domain.LoanProviderApiMapper;
import de.joonko.loan.partner.auxmoney.model.AuxmoneyGetOffersRequest;
import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AuxmoneyApiMapper implements LoanProviderApiMapper<AuxmoneyGetOffersRequest, List<AuxmoneySingleCallResponse>> {

    private AuxmoneyGetOffersRequestMapper requestMapper;

    private AuxmoneyGetOffersResponseMapper responseMapper;

    @Override
    public AuxmoneyGetOffersRequest toLoanProviderRequest(LoanDemand loanDemand, LoanDuration loanDuration) {
        return requestMapper.toAuxmoneyRequest(loanDemand);
    }

    @Override
    public List<LoanOffer> fromLoanProviderResponse(List<AuxmoneySingleCallResponse> auxmoneySingleCallResponse) {
        return responseMapper.fromAuxmoneyResponse(auxmoneySingleCallResponse);
    }
}
