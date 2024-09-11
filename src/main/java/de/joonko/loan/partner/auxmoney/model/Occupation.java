package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Occupation {
    VORSTAND_GESCHAEFTSFUEHRER(1),
    LEITENDER_ANGESTELLTER(2),
    ANGESTELLTER(3),
    BEAMTER_IM_GEHOBENEN_DIENST(4),
    BEAMTER_IM_HOEHEREN_DIENST(5),
    BEAMTER_IM_MITTLEREN_DIENST(6),
    BEAMTER_IM_EINFACHEN_DIENST(7),
    ZEITSOLDAT(8),
    BERUFSSOLDAT(9),
    MEISTER(10),
    SELBSTSTANDIG(11),
    FACHARBEITER(12),
    ARBEITER(13),
    STUDENT(14),
    SCHULER(15),
    AUSZUBILDENDER(16),
    GRUNDWEHR_ZIVILDIENSTLEISTENDER(17),
    HAUSFRAU_MANN(18),
    RENTNER_PENSIONAER(19),
    OHNE_BESCHAEFTIGUNG(20),
    KEINE_ANGABE_SONSTIGES(21);

    @JsonValue
    private int occupationId;

    Occupation(int occupationId) {
        this.occupationId = occupationId;
    }
}
