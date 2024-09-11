package de.joonko.loan.webhooks.webid.model.request;


import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProductInfos {
    private String additionalProp1;
    private String additionalProp2;
    private String additionalProp3;
}
