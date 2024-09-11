package de.joonko.loan.customer.support.mapper;

import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.customer.support.model.OfferAcceptedEvent;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.util.EncrDecrService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public abstract class OfferAcceptedEventMapper {

    @Autowired
    LoanDemandStoreService loanDemandStoreService;

    @Autowired
    EncrDecrService encrDecrService;

    @Mapping(target = "bank", expression = "java(loanOffer.getLoanProvider().getName().toString())")
    @Mapping(target = "createdAt", expression = "java(System.currentTimeMillis())")
    @Mapping(target = "duration", source = "loanOffer.durationInMonth")
    @Mapping(target = "email", source = "applicationId", qualifiedByName = "getEmail")
    @Mapping(target = "loanAmountAsked", source = "loanOffer.amount")
    @Mapping(target = "loanApplicationId", source = "applicationId")
    @Mapping(target = "loanApplicationStatus", expression = "java(offerStatus.getStatus().name())")
    @Mapping(target = "interestRate", source = "loanOffer.effectiveInterestRate")
    @Mapping(target = "kycUrl", ignore = true)
    @Mapping(target = "monthlyRate", source = "loanOffer.monthlyRate")
    @Mapping(target = "totalInterestPayment", source = "loanOffer", qualifiedByName = "getTotalInterestPayment")
    @Mapping(target = "totalPayment", source = "loanOffer.totalPayment")
    public abstract OfferAcceptedEvent mapToOfferAcceptedEvent(String applicationId, OfferStatus offerStatus, LoanOffer loanOffer);

    @Named("getEmail")
    String getEmail(String applicationId) {
        LoanDemandStore loanDemandStore = loanDemandStoreService
                .findById(applicationId)
                .orElseThrow(() -> new RuntimeException("applicationId not found " + applicationId));
        return encrDecrService.deAnonymize(loanDemandStore.getEmailId());
    }

    @Named("getTotalInterestPayment")
    String getTotalInterestPayment(LoanOffer loanOffer) {
        return loanOffer.getTotalPayment().subtract(new BigDecimal(loanOffer.getAmount())).toString();
    }
}
