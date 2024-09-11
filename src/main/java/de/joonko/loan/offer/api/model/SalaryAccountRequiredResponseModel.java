package de.joonko.loan.offer.api.model;

public class SalaryAccountRequiredResponseModel extends OffersResponse<CustomErrorResponse> {

    public SalaryAccountRequiredResponseModel(OfferResponseState state, CustomErrorResponse data) {
        super(state, data);
    }
}
