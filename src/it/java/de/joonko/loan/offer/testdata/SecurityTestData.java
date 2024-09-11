package de.joonko.loan.offer.testdata;

import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityTestData {
    public static Jwt mockEmailVerifiedJwt(String userId) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("email_verified", true)
                .claim("sub", userId)
                .claim("scope", "read")
                .build();
    }

    public static Jwt mockEmailNotVerifiedJwt(String userId) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("email_verified", false)
                .claim("sub", userId)
                .claim("scope", "read")
                .build();
    }

}
