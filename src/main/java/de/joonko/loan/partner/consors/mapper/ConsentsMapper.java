package de.joonko.loan.partner.consors.mapper;


import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.consors.model.Consents;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static de.joonko.loan.partner.consors.ConsorsDefaults.CONSENT_POST_EMAIL_SELL_CONTACT;
import static de.joonko.loan.partner.consors.ConsorsDefaults.CONSENT_SCHUFA_CALL_ALLOWED;

@Mapper(componentModel = "spring")
public interface ConsentsMapper {

    @Mapping(target = "schufaCallAllowed", constant = CONSENT_SCHUFA_CALL_ALLOWED)
    @Mapping(target = "customerContactedByPhoneAndEmailForPromotions", constant = CONSENT_POST_EMAIL_SELL_CONTACT)
    Consents toConsent(LoanDemand loanDemand);
}
