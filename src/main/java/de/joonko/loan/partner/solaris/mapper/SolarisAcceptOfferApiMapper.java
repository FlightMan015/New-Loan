package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.acceptoffer.domain.AcceptOfferApiMapper;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.solaris.model.SolarisAcceptOfferRequest;
import de.joonko.loan.partner.solaris.model.SolarisAcceptOfferResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SolarisAcceptOfferApiMapper implements AcceptOfferApiMapper<SolarisAcceptOfferRequest, SolarisAcceptOfferResponse> {

    private SolarisAcceptOfferRequestMapper requestMapper;

    private SolarisAcceptOfferResponseMapper responseMapper;

    @Override
    public SolarisAcceptOfferRequest toAcceptOfferRequest(OfferRequest offerRequest) {
        return requestMapper.toSolarisRequest(offerRequest);
    }

    @Override
    public OfferStatus fromAcceptOfferResponse(SolarisAcceptOfferResponse response) {
        return responseMapper.fromSolarisResponse(response);
    }
}
