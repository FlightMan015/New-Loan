package de.joonko.loan.idnow;

import de.joonko.loan.identification.model.idnow.DocumentDefinition;
import de.joonko.loan.identification.model.idnow.IDNowJwtToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Set;

public class IdNowAccountTestData {
    public static Mono<IDNowJwtToken> getIdNowJwtToken() {
        IDNowJwtToken idNowJwtToken = new IDNowJwtToken();
        idNowJwtToken.setAuthToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkZS5pZG5vdy5nYXRld2F5IiwiYXVkIjoiZGUuaWRub3cuYXBwbGljYXRpb24iLCJleHAiOjE2MTQyMDg3ODAsImp0aSI6InlwYmxKTlZpMVVDS2h1OC1wSU9SLUEiLCJpYXQiOjE2MTQyMDUxODAsIm5iZiI6MTYxNDIwNTA2MCwic3ViIjoiam9vbmtvc3drZXNpZ24iLCJ0eXBlIjoiQ09NUEFOWSIsInBlcm1pc3Npb25zIjoiQUxMIn0.cfJjh43PXPEFGtdeDdeUKxmdWuMxX07rKLVHYrjo_s9bNDo1Dw7apDvBu276izpYaPSEd4z89G9ET2jqjVhULg");

        return Mono.just(idNowJwtToken);
    }

    public static Set<DocumentDefinition> getSingleDocumentDefinition() {
        return Set.of(
                DocumentDefinition.builder()
                        .identifier("contract")
                        .mimeType("application/pdf")
                        .optional(false)
                        .name("Contract")
                        .build()
        );
    }

    public static DocumentDefinition[] getArrayOfSingleDocumentDefinition() {
        return getSingleDocumentDefinition().toArray(DocumentDefinition[]::new);
    }

    public static DocumentDefinition[] getArrayOfMultipleDocumentDefinitions() {
        return getMultipleDocumentDefinitions().toArray(DocumentDefinition[]::new);
    }

    public static Set<DocumentDefinition> getMultipleDocumentDefinitions() {
        return Set.of(
                DocumentDefinition.builder()
                        .identifier("contract")
                        .mimeType("application/pdf")
                        .optional(false)
                        .name("Contract")
                        .build(),
                DocumentDefinition.builder()
                        .identifier("agreement")
                        .mimeType("application/pdf")
                        .optional(false)
                        .name("Contract")
                        .build()
        );
    }
}
