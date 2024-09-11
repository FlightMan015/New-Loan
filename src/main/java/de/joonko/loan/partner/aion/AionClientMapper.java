package de.joonko.loan.partner.aion;

import de.joonko.loan.partner.aion.model.AionResponseValueType;
import de.joonko.loan.partner.aion.model.offerchoice.FileDetails;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceRawResponse;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AionClientMapper {

    public OfferChoiceResponse mapToOfferChoice(OfferChoiceRawResponse rawResponse) {
        String representativeId = (String) rawResponse.getVariables().stream()
                .filter(variable -> variable.getName() == AionResponseValueType.REPRESENTATIVE_ID)
                .findFirst().orElseThrow(() -> new IllegalStateException("Missing representativeId")).getValue();

        Map<String, Map<String, String>> fileDetails = (Map<String, Map<String, String>>) rawResponse.getVariables().stream()
                .filter(variable -> variable.getName() == AionResponseValueType.DRAFT_AGREEMENT)
                .findFirst().orElseThrow(() -> new IllegalStateException("Missing draftAgreement")).getValue();

        return OfferChoiceResponse.builder()
                .processId(rawResponse.getProcessId())
                .representativeId(representativeId)
                .draftAgreement(List.of(
                        FileDetails.builder()
                                .fileId("agreement")
                                .fileName(fileDetails.get("agreement").get("fileName"))
                                .fileContent(fileDetails.get("agreement").get("fileContent"))
                                .build(),
                        FileDetails.builder()
                                .fileId("schedule")
                                .fileName(fileDetails.get("schedule").get("fileName"))
                                .fileContent(fileDetails.get("schedule").get("fileContent"))
                                .build(),
                        FileDetails.builder()
                                .fileId("secci")
                                .fileName(fileDetails.get("secci").get("fileName"))
                                .fileContent(fileDetails.get("secci").get("fileContent"))
                                .build()
                )).build();
    }
}
