package de.joonko.loan.webhooks.aion.mapper;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.webhooks.aion.model.AionOfferStatus;
import de.joonko.loan.webhooks.aion.model.AionWebhookRequest;
import de.joonko.loan.webhooks.aion.model.AionWebhookType;
import de.joonko.loan.webhooks.aion.repositories.AionWebhookStore;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface AionWebhookStoreMapper {

    @Mapping(target = "id", source = "id", ignore = true)
    @Mapping(target = "aionWebhookId", source = "id")
    @Mapping(target = "type", source = "type")
    AionWebhookStore map(final AionWebhookRequest aionWebhookRequest);

    default OffsetDateTime map(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toOffsetDateTime();
    }

    default LoanApplicationStatus mapStatus(final AionWebhookType type, final AionOfferStatus aionOfferStatus) {
        switch (aionOfferStatus) {
            case FAILED:
                return LoanApplicationStatus.REJECTED;
            case SUCCESS:
                if (AionWebhookType.CASHLOAN_OPEN.equals(type)) {
                    return LoanApplicationStatus.PAID_OUT;
                }
                return LoanApplicationStatus.PENDING;
            default:
                return LoanApplicationStatus.PENDING;
        }
    }
}
