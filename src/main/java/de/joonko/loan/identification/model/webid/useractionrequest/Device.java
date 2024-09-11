
package de.joonko.loan.identification.model.webid.useractionrequest;

import lombok.Data;

@Data
public class Device {
    private String userAgent;
    private String userIp;
}
