package de.joonko.loan.webhooks.postbank.model;

public enum ContractState {
    IM_SYSTEM_GESPEICHERT_10,
    MANUELLE_BEARBEITUNG_20,
    ONLINE_GENEHMIGT_24,
    UNTERLAGEN_EINGEGANGEN_25, // User successfully submitted the signed documents through email
    ALTERNATIV_ANGEBOT_27,
    UNTERLAGEN_NACHGEFORDERT_30,
    NACHGEFORDERTE_UNTERLAGEN_EINGEGANGEN_35,
    ABLOESEBESTAETIGUNG_ANGEFORDERT_40,
    ANGEFORDERTE_ABLOESEBESTAETIGUNG_EINGEGANGEN_45,
    ONLINE_ABGELEHNT_93,
    ONLINE_GENEHMIGT_UND_AUSBEZAHLT_99, // loan is successfully paid out
    MANUELL_ABGEWIESEN_94,
    ALTERNATIV_ANGEBOT_ERFOLGREICH_2724,
    ALTERNATIV_ANGEBOT_GESCHEITERT_2793,
    DIGITALE_SIGNATUR_EINGEGANGEN_80, // User finished a successful e-signature
    HOCHGELADENE_DOKUMENTE_532;

    public static ContractState getSuccessState() {
        return ONLINE_GENEHMIGT_24;
    }

}
