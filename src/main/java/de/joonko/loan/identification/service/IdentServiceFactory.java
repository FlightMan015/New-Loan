package de.joonko.loan.identification.service;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.identification.service.idnow.*;
import de.joonko.loan.identification.service.solaris.SolarisIdentService;
import de.joonko.loan.identification.service.webid.ConsorsAlternativeIdentService;
import de.joonko.loan.identification.service.webid.PostbankIdentService;
import de.joonko.loan.identification.service.webid.SantanderIdentService;
import de.joonko.loan.partner.consors.ConsorsPropertiesConfig;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentServiceFactory {

    private final AuxmoneyIdentService auxmoneyIdentService;
    private final ConsorsIdentService consorsIdentService;
    private final SwkIdentService swkIdentService;
    private final AionIdentService aionIdentService;
    private final SolarisIdentService solarisIdentService;
    private final CreditPlusIdentService creditPlusIdentificationService;
    private final SantanderIdentService santanderIdentService;
    private final PostbankIdentService postbankIdentService;
    private final ConsorsPropertiesConfig consorsPropertiesConfig;
    private final ConsorsAlternativeIdentService consorsAlternativeIdentService;

    public IdentService getIdentService(String provider) {
        switch (Bank.fromLabel(provider)) {
            case AUXMONEY:
                return auxmoneyIdentService;
            case CONSORS:
                return Boolean.TRUE.equals(consorsPropertiesConfig.getWebidEnabled()) ? consorsAlternativeIdentService : consorsIdentService;
            case SWK_BANK:
                return swkIdentService;
            case AION:
                return aionIdentService;
            case DEUTSCHE_FINANZ_SOZIETÄT:
                return solarisIdentService;
            case CREDIT_PLUS:
                return creditPlusIdentificationService;
            case SANTANDER:
                return santanderIdentService;
            case POSTBANK:
                return postbankIdentService;
            default:
                throw new RuntimeException("Invalid Provider");
        }
    }
}
