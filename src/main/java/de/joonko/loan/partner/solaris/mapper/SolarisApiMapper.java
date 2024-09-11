package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.offer.domain.LoanProviderApiMapper;
import de.joonko.loan.partner.solaris.model.SolarisAllApiRequest;
import de.joonko.loan.partner.solaris.model.SolarisGetOffersResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class SolarisApiMapper implements LoanProviderApiMapper<SolarisAllApiRequest, List<SolarisGetOffersResponse>> {

    private SolarisAllApiRequestMapper solarisAllApiRequestMapper;
    private SolarisGetOffersResponseMapper solarisGetOffersResponseMapper;

    @Override
    public SolarisAllApiRequest toLoanProviderRequest(LoanDemand loanDemand, LoanDuration loanDuration) {
        return solarisAllApiRequestMapper.toSolarisRequest(loanDemand);
    }

    @Override
    public List<LoanOffer> fromLoanProviderResponse(List<SolarisGetOffersResponse> response) {
        return solarisGetOffersResponseMapper.fromLoanProviderResponse(response);
    }
}
