package de.joonko.loan.partner.consors;

import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.FinancialCalculation;
import de.joonko.loan.partner.consors.model.SubscriptionStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ConsorsProfileFilter {

    private final ConsorsPropertiesConfig consorsPropertiesConfig;

    public boolean isGreen(ConsorsAcceptOfferResponse acceptOfferResponse) {
        List<Integer> supportingDocumentsRequired = acceptOfferResponse.getSupportingDocumentsRequired();

        boolean onlyDacDocs = supportingDocumentsRequired == null
                || supportingDocumentsRequired.isEmpty()
                || containsAllIgnored(supportingDocumentsRequired);

        return onlyDacDocs && acceptOfferResponse.getSubscriptionStatus().equals(SubscriptionStatus.APPROVED);
    }

    public boolean isRed(List<FinancialCalculation> list) {
        return list.isEmpty();
    }

    private boolean containsAllIgnored(List<Integer> supportingDocumentsRequired) {
        return consorsPropertiesConfig.getGreenProfileDocIgnoreList().containsAll(supportingDocumentsRequired);
    }
}
