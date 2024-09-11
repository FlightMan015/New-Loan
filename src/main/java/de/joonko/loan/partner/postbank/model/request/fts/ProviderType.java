package de.joonko.loan.partner.postbank.model.request.fts;

public enum ProviderType {
    FINTEC("FINTEC");

    public final String label;

    ProviderType(String label) {
        this.label = label;
    }
}
