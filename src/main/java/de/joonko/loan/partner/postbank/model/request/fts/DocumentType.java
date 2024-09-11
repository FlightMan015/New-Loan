package de.joonko.loan.partner.postbank.model.request.fts;

public enum DocumentType {
    JSON("json");

    public final String label;

    DocumentType(String label) {
        this.label = label;
    }
}
