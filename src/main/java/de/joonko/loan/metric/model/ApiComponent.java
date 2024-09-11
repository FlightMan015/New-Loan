package de.joonko.loan.metric.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiComponent {
    SWK("swk"),
    CONSORS("consors"),
    SANTANDER("santander"),
    AION("aion"),
    POSTBANK("postbank"),
    FUSION_AUTH("fusion_auth"),
    ID_NOW("idnow"),
    WEB_ID("webid"),
    SEGMENT("segment"),
    FTS("fts"),
    DAC_API("dac_api");

    private final String name;
}
