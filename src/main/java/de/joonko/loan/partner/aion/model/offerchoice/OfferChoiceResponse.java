package de.joonko.loan.partner.aion.model.offerchoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferChoiceResponse {

    private String processId;

    private String representativeId;

    private List<FileDetails> draftAgreement;
}
