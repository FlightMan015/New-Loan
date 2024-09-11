package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SolarisAcceptOfferResponse {

    private String id;
    private String reference;
    private String state;
    private LoanStatus status;
    private String url;
    private byte[] contract;
    private byte[] preContract;
    private String signingId;
    @JsonProperty("identification_id")
    private String identificationId;

    private List<String> documents;

    private String method;

}
