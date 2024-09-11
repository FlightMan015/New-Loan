package de.joonko.loan.reporting.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetOffersStatusResponse {

    private List<GetOfferStatusResponse> offers;
}
