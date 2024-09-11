package de.joonko.loan.partner.aion.model;

public enum BestOfferCategory {
    APR("apr"),
    MONTHLY_INSTALLMENT("monthly_instalment_amount"),
    TOTAL_REPAYMENT("total_repayment_amount");

    private final String label;

    public String getLabel() {
        return label;
    }

    BestOfferCategory(final String label) {
        this.label = label;
    }
}
