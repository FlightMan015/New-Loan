package de.joonko.loan.acceptoffer.api;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.GetOffersMapper;
import de.joonko.loan.offer.domain.LoanDuration;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {LoanDuration.class, GetOffersMapper.class})
public interface OfferRequestMapper {

    @Mapping(source = ".", target = "duration", qualifiedByName = "setLoanDuration")
    AcceptOfferResponse toResponse(OfferStatus offerStatus);

    @Mapping(target = "loanAsked", source = "offer.amount")
    @Mapping(target = "duration", source = "offer.durationInMonth", qualifiedByName = "getLoanDuration")
    @Mapping(target = "loanProvider", source = "offer.loanProvider.name")
    OfferRequest fromRequest(LoanOfferStore loanOfferStore);

    @Named("getLoanDuration")
    default LoanDuration getLoanDuration(int number) {
        return LoanDuration.fromNumber(number);
    }

    @Named("setLoanDuration")
    default Integer setLoanDuration(OfferStatus offerStatus) {
        if (null == offerStatus.getDuration()) {
            return null;
        } else {
            return offerStatus.getDuration().getValue();
        }

    }
}
