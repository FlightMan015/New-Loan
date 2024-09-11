package de.joonko.loan.offer;

import lombok.Builder;
import lombok.Data;

import java.net.InetAddress;
import java.util.Optional;

@Data
@Builder
public class OfferDemandRequest {

    private String userUUID;
    private int requestedLoanAmount;
    private String requestedLoanPurpose;
    private boolean onlyBonify;
    private Optional<InetAddress> inetAddress;
}
