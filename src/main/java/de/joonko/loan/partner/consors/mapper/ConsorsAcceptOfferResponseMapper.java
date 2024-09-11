package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.Link;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ConsorsAcceptOfferResponseMapper {


    @Mapping(source = "financialCalculation.creditAmount", target = "loanAsked")
    @Mapping(source = ".", target = "duration", qualifiedByName = "getLoanDuration")
    @Mapping(source = "financialCalculation.monthlyRate", target = "monthlyPayment")
    @Mapping(source = "financialCalculation.effectiveRate", target = "effectiveRate")
    @Mapping(source = ".", target = "status", qualifiedByName = "getStatus")
    @Mapping(source = "financialCalculation.nominalRate", target = "nominalRate")
    @Mapping(source = "financialCalculation.totalInterestAmount", target = "totalInterestPayment")
    @Mapping(source = "financialCalculation.totalPayment", target = "totalPayment")
    @Mapping(target = "kycUrl", ignore = true)
    @Mapping(source = ".", target = "contractDocumentUrl", qualifiedByName = "getContractDocumentUrl")
    OfferStatus fromConsorsResponse(ConsorsAcceptOfferResponse consorsAcceptOfferResponse);

    @Named("getContractDocumentUrl")
    default String getContractDocumentUrl(ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        Optional<Link> contractDocumentLink = consorsAcceptOfferResponse.getLinks().stream().filter(link -> link.getRel().equals("_downloadSubscriptionDocument")).findFirst();

        if (contractDocumentLink.isPresent()) {
            return contractDocumentLink.get().getHref();
        } else {
            throw new RuntimeException("Unable to find download subscription document");
        }
    }

    @Named("getLoanDuration")
    default LoanDuration getLoanDuration(ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        return LoanDuration.fromNumber(consorsAcceptOfferResponse.getFinancialCalculation().getDuration());
    }

    @Named("getStatus")
    default LoanApplicationStatus getStatus(ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        return LoanApplicationStatus.valueOf(consorsAcceptOfferResponse.getSubscriptionStatus().name());
    }

}
