package de.joonko.loan.user.service.mapper;

import de.joonko.loan.user.api.model.Consent;
import de.joonko.loan.user.api.model.ConsentApiState;
import de.joonko.loan.user.api.model.ConsentApiType;
import de.joonko.loan.user.service.persistence.domain.ConsentState;
import de.joonko.loan.user.service.persistence.domain.ConsentType;
import de.joonko.loan.user.service.persistence.domain.ConsentData;

import java.time.Instant;

public class ConsentMapper {

    public static ConsentData map(final Consent consent, final String clientIP) {
        return ConsentData.builder()
                .consentState(map(consent.getConsent()))
                .consentType(map(consent.getType()))
                .lastUpdatedTimestamp(Instant.now())
                .clientIP(clientIP)
                .build();
    }

    public static Consent map(final ConsentData userConsentStore) {
        return Consent.builder()
                .type(map(userConsentStore.getConsentType()))
                .consent(map(userConsentStore.getConsentState()))
                .build();
    }


    public static  ConsentState map(final ConsentApiState consentApiState) {
        switch (consentApiState) {
            case NONE:
                return ConsentState.NONE;
            case ACCEPTED:
                return ConsentState.ACCEPTED;
            case DECLINED:
                return ConsentState.DECLINED;
            case REVOKED:
                return ConsentState.REVOKED;
            default:
                throw new RuntimeException(String.format("Could not normally map the consent state from - %s", consentApiState));
        }
    }

    public static  ConsentApiState map(final ConsentState consentState) {
        switch (consentState) {
            case NONE:
                return ConsentApiState.NONE;
            case ACCEPTED:
                return ConsentApiState.ACCEPTED;
            case DECLINED:
                return ConsentApiState.DECLINED;
            case REVOKED:
                return ConsentApiState.REVOKED;
            default:
                throw new RuntimeException(String.format("Could not normally map the consent state from - %s", consentState));
        }
    }

    public static  ConsentType map(final ConsentApiType consentApiType) {
        switch (consentApiType) {
            case PHONE:
                return ConsentType.PHONE;
            case EMAIL:
                return ConsentType.EMAIL;
            case SMS:
                return ConsentType.SMS;
            case LETTER:
                return ConsentType.LETTER;
            default:
                throw new RuntimeException(String.format("Could not normally map the consent type from - %s", consentApiType));
        }
    }

    public static ConsentApiType  map(final ConsentType consentApiType) {
        switch (consentApiType) {
            case PHONE:
                return ConsentApiType.PHONE;
            case EMAIL:
                return ConsentApiType.EMAIL;
            case SMS:
                return ConsentApiType.SMS;
            case LETTER:
                return ConsentApiType.LETTER;
            default:
                throw new RuntimeException(String.format("Could not normally map the consent type from - %s", consentApiType));
        }
    }
}
