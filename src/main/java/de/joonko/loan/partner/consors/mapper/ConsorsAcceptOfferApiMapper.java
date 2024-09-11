package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.acceptoffer.domain.AcceptOfferApiMapper;
import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferRequest;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConsorsAcceptOfferApiMapper implements AcceptOfferApiMapper<ConsorsAcceptOfferRequest, ConsorsAcceptOfferResponse> {


    private ConsorsAcceptOfferRequestMapper requestMapper;

    private ConsorsAcceptOfferResponseMapper responseMapper;


    @Override
    public ConsorsAcceptOfferRequest toAcceptOfferRequest(OfferRequest offerRequest) {
        return requestMapper.toConsorsRequest(offerRequest);
    }

    @Override
    public OfferStatus fromAcceptOfferResponse(ConsorsAcceptOfferResponse response) {
        return responseMapper.fromConsorsResponse(response);
    }
}
