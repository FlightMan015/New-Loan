package de.joonko.loan.webhooks.webid.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomParameters {
    public String additionalProp1;
    public String additionalProp2;
    public String additionalProp3;
}
