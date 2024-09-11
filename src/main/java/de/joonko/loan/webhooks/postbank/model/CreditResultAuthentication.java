package de.joonko.loan.webhooks.postbank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditResultAuthentication {
    private String login;
    private String password;
}
