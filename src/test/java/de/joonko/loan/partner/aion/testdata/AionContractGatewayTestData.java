package de.joonko.loan.partner.aion.testdata;

import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.partner.aion.model.offerchoice.FileDetails;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceResponse;

import java.util.List;
import java.util.Optional;

public class AionContractGatewayTestData {

    public OfferChoiceResponse getOfferChoiceResponse() {
        return OfferChoiceResponse.builder()
                .draftAgreement(List.of(
                        FileDetails.builder()
                                .fileId("agreement")
                                .fileContent("agreementbase64")
                                .build(),
                        FileDetails.builder()
                                .fileId("secci")
                                .fileContent("seccibase64")
                                .build(),
                        FileDetails.builder()
                                .fileId("schedule")
                                .fileContent("schedulebase64")
                                .build()
                ))
                .build();
    }

    public Optional<CreditApplicationResponseStore> getCreditApplicationResponseStore(String processId) {
        return Optional.of(CreditApplicationResponseStore.builder()
                .processId(processId)
                .build());
    }
}
