package de.joonko.loan.identification.service.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.model.idnow.DocumentDefinition;
import de.joonko.loan.identification.model.idnow.IdNowAccount;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class IdNowAccountMapper {

    private final IdentificationPropConfig idNowProps;

    public String getApiKey(IdNowAccount account) {
        switch (account) {
            case AUXMONEY:
                return idNowProps.getAuxmoneyApiKey();
            case CONSORS:
                return idNowProps.getConsorsApiKey();
            case SWK:
                return idNowProps.getSwkApiKey();
            case CREDITPLUS:
                return idNowProps.getCreditPlusApiKey();
            case AION:
                return idNowProps.getAionApiKey();
            default:
                throw new IllegalArgumentException("Invalid IdNow account: " + account);
        }
    }

    public String getAccountId(IdNowAccount account) {
        switch (account) {
            case AUXMONEY:
                return idNowProps.getAuxmoneyAccountId();
            case CONSORS:
                return idNowProps.getConsorsAccountId();
            case SWK:
                return idNowProps.getSwkAccountId();
            case CREDITPLUS:
                return idNowProps.getCreditPlusAccountId();
            case AION:
                return idNowProps.getAionAccountId();
            default:
                throw new IllegalArgumentException("Invalid IdNow account: " + account);
        }
    }

    public Set<DocumentDefinition> getDocumentDefinitions(IdNowAccount account) {
        switch (account) {
            case AUXMONEY:
                return AUXMONEY_DOCUMENT_DEFINITIONS;
            case CONSORS:
                return CONSORS_DOCUMENT_DEFINITIONS;
            case SWK:
                return SWK_DOCUMENT_DEFINITIONS;
            case AION:
                return AION_DOCUMENT_DEFINITIONS;
            case CREDITPLUS:
                return CREDITPLUS_DOCUMENT_DEFINITIONS;
            default:
                throw new IllegalArgumentException("Invalid IdNow account: " + account);
        }
    }

    private static final Set<DocumentDefinition> AUXMONEY_DOCUMENT_DEFINITIONS = Set.of(
            DocumentDefinition.builder()
                    .optional(false)
                    .identifier("contract")
                    .name("Contract")
                    .mimeType("application/pdf").build());

    private static final Set<DocumentDefinition> CONSORS_DOCUMENT_DEFINITIONS = Set.of(
            DocumentDefinition.builder()
                    .optional(false)
                    .identifier("contract")
                    .name("Contract")
                    .mimeType("application/pdf").build());

    private static final Set<DocumentDefinition> SWK_DOCUMENT_DEFINITIONS = Set.of(
            DocumentDefinition.builder()
                    .optional(false)
                    .identifier("contract")
                    .name("Contract")
                    .mimeType("application/pdf").build());

    private static final Set<DocumentDefinition> AION_DOCUMENT_DEFINITIONS = Set.of(
            DocumentDefinition.builder()
                    .optional(false)
                    .identifier("agreement")
                    .name("Agreement")
                    .mimeType("application/pdf").build(),
            DocumentDefinition.builder()
                    .optional(false)
                    .identifier("schedule")
                    .name("Schedule")
                    .mimeType("application/pdf").build(),
            DocumentDefinition.builder()
                    .optional(false)
                    .identifier("secci")
                    .name("Secci")
                    .mimeType("application/pdf").build());

    private static final Set<DocumentDefinition> CREDITPLUS_DOCUMENT_DEFINITIONS = Set.of(
            DocumentDefinition.builder()
                    .optional(false)
                    .identifier("antrag1")
                    .name("Contract")
                    .mimeType("application/pdf").build());
}
