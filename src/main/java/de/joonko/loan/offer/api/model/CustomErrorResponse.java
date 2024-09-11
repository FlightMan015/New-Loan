package de.joonko.loan.offer.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomErrorResponse {
    CustomErrorMessageKey messageKey;
}
