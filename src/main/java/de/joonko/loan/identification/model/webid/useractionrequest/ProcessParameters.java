
package de.joonko.loan.identification.model.webid.useractionrequest;

import lombok.Data;

import java.util.List;

@Data
public class ProcessParameters {

    private String redirectUrl;
    private String redirectDeclineUrl;
    private String redirectMismatchUrl;
    private String redirectSkipQesUrl;
    private String redirectCancelIdentUrl;
    private Integer redirectTime;
    private List<String> sigField = null;
    private String productType;
    private String clientId;
}
