package de.joonko.loan.common.partner.consors.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtToken {
    private String token;

    private String getTokenPlainText() {
        return token.replace("Bearer", "")
                .trim();
    }

    public Consumer<HttpHeaders> bearer() {
        return httpHeaders -> httpHeaders.setBearerAuth(getTokenPlainText());
    }
}
