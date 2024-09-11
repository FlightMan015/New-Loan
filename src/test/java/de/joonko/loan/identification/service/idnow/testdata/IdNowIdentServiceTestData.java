package de.joonko.loan.identification.service.idnow.testdata;

import de.joonko.loan.identification.model.idnow.GetIdentResponse;
import de.joonko.loan.identification.model.idnow.IDNowCreateIdentResponse;
import de.joonko.loan.identification.model.idnow.IDNowJwtToken;
import de.joonko.loan.identification.model.idnow.IdentificationProcess;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class IdNowIdentServiceTestData {
    public static IDNowJwtToken getIdNowJwtToken() {
        IDNowJwtToken idNowJwtToken = new IDNowJwtToken();
        idNowJwtToken.setAuthToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkZS5pZG5vdy5nYXRld2F5IiwiYXVkIjoiZGUuaWRub3cuYXBwbGljYXRpb24iLCJleHAiOjE2MTQyMDg3ODAsImp0aSI6InlwYmxKTlZpMVVDS2h1OC1wSU9SLUEiLCJpYXQiOjE2MTQyMDUxODAsIm5iZiI6MTYxNDIwNTA2MCwic3ViIjoiam9vbmtvc3drZXNpZ24iLCJ0eXBlIjoiQ09NUEFOWSIsInBlcm1pc3Npb25zIjoiQUxMIn0.cfJjh43PXPEFGtdeDdeUKxmdWuMxX07rKLVHYrjo_s9bNDo1Dw7apDvBu276izpYaPSEd4z89G9ET2jqjVhULg");

        return idNowJwtToken;
    }

    public static IDNowCreateIdentResponse getCreateIdentResponse() {
        var createIdentResponse = new IDNowCreateIdentResponse();
        createIdentResponse.setId("12345");

        return createIdentResponse;
    }

    public static GetIdentResponse getIdentResponse() {
        GetIdentResponse getIdentResponse = new GetIdentResponse();
        IdentificationProcess identificationProcess = new IdentificationProcess();
        identificationProcess.setResult("FRAUD_SUSPICION_PENDING");
        getIdentResponse.setIdentificationProcess(identificationProcess);

        return getIdentResponse;
    }

    public static Mono<GetIdentResponse> getNotFoundExceptionFromWebClient() {
        return Mono.error(WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not found exception", null, null, null));
    }

    public static Mono<IDNowJwtToken> getUnauthorizedExceptionFromWebClient() {
        return Mono.error(WebClientResponseException.create(HttpStatus.UNAUTHORIZED.value(), "Unauthorized exception", null, null, null));
    }
}
