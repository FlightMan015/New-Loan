package de.joonko.loan.partner.auxmoney;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.partner.auxmoney.model.AuxmoneyAcceptOfferRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuxmoneyAcceptOfferRequestMapper {
    AuxmoneyAcceptOfferRequest toAuxmoneyAcceptOfferRequest(OfferRequest offerRequest);
}
