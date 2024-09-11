package de.joonko.loan.identification.model;

import de.joonko.loan.contract.model.PresignedDocumentDetails;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class GetOfferContractsResponse {

    private String kycURL;

    private List<PresignedDocumentDetails> contracts;
}
