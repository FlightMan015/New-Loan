package de.joonko.loan.partner.santander;

import de.joonko.loan.config.SantanderConfig;
import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import de.joonko.loan.partner.santander.model.entry.ContractDomain;
import de.joonko.loan.partner.santander.model.entry.ContractEntryRequest;
import de.joonko.loan.partner.santander.stub.FaultException;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import de.joonko.loan.partner.santander.stub.ScbCapsDocsWSCallbackHandler;
import de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Arrays;

import static org.apache.commons.io.IOUtils.toByteArray;

@Slf4j
@Component
@RequiredArgsConstructor
public class SantanderClientApi {

    private final ScbCapsDocsWSStub docsWSStub;
    private final ScbCapsBcoWSStub bcoWSStub;

    @Qualifier("santanderWebIdCoreClient")
    private final WebClient santanderWebIdCoreClient;
    private final ApiMetric apiMetric;

    private final SantanderConfig santanderConfig;

    public ScbCapsBcoWSStub.GetKreditvertragsangebotResponse getOffer(ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot) throws FaultException, RemoteException {
        getKreditvertragsangebot.getGetKreditvertragsangebot().setAuthentisierung(buildBcoAuth());

        return bcoWSStub.getKreditvertragsangebot(getKreditvertragsangebot);
    }

    public ScbCapsBcoWSStub.GetKreditantragsstatusResult getApplicationStatus(ScbCapsBcoWSStub.GetKreditantragsstatusParams getKreditantragsstatusParams) throws FaultException, RemoteException {
        getKreditantragsstatusParams.setAuthentisierung(buildBcoAuth());

        final var request = new ScbCapsBcoWSStub.GetKreditantragsstatus();
        request.setGetKreditantragsstatus(getKreditantragsstatusParams);

        return bcoWSStub.getKreditantragsstatus(request).getGetKreditantragsstatusResponse();
    }

    public void setDocument(ByteArrayDataSource encodedAccountSnapshot, String scbAntragId) throws RemoteException {
        ScbCapsDocsWSStub.SetDocument setDocument = new ScbCapsDocsWSStub.SetDocument();
        ScbCapsDocsWSStub.SetDocumentParams setDocumentParams = new ScbCapsDocsWSStub.SetDocumentParams();

        ScbCapsDocsWSStub.DocumentXO documentXO = new ScbCapsDocsWSStub.DocumentXO();
        documentXO.setName(SantanderConstants.DAC_DOCUMENT_NAME);
        documentXO.setDescription(SantanderConstants.DAC_DOCUMENT_DESCRIPTION);
        documentXO.setApplicant(1);
        documentXO.setOrigin(ScbCapsDocsWSStub.DocOriginType.ACCOUNT_SCREENING);

        DataHandler dataHandler = new DataHandler(encodedAccountSnapshot);
        documentXO.setFile(dataHandler);
        documentXO.setFilename(SantanderConstants.DAC_DOCUMENT_FILENAME_PREFIX + scbAntragId + ".pdf");

        setDocumentParams.setApplicationId(scbAntragId);
        setDocumentParams.setDepartment(ScbCapsDocsWSStub.DepartmentType.DIRECT);
        setDocumentParams.setDocument(documentXO);
        setDocumentParams.setAuthentication(buildDocsAuth());
        setDocument.setSetDocument(setDocumentParams);

        docsWSStub.startsetDocument(setDocument, new ScbCapsDocsWSCallbackHandler() {});
    }

    public byte[] getContract(final String scbAntragId, final boolean advertisingAgreement) {
        try {
            ScbCapsDocsWSStub.GetContractListParams getContractListParams = new ScbCapsDocsWSStub.GetContractListParams();
            getContractListParams.setApplicationId(scbAntragId);
            getContractListParams.setDepartment(ScbCapsDocsWSStub.DepartmentType.DIRECT);

            ScbCapsDocsWSStub.GetContractList getContractList = new ScbCapsDocsWSStub.GetContractList();
            getContractList.setGetContractList(getContractListParams);

            getContractListParams.setAuthentication(buildDocsAuth());

            final var contractList = docsWSStub.getContractList(getContractList);
            return Arrays.stream(contractList.getGetContractListResponse().getContractList())
                    .filter(contract ->
                            (advertisingAgreement ? ScbCapsDocsWSStub.DocType.CONTRACT_QES_WITH_ADVERTISING_AGREEMENT : ScbCapsDocsWSStub.DocType.CONTRACT_QES_WITHOUT_ADVERTISING_AGREEMENT)
                                    .equals(contract.getType()))
                    .findFirst()
                    .map(contract -> deserializeFile(contract.getFile(), scbAntragId))
                    .orElseThrow(() -> new RuntimeException(String.format("IDENTIFICATION: Could not find the right type of file in the contract list for scbAntragId %s and advertisementConsent - %s", scbAntragId, advertisingAgreement)));
        } catch (Exception ex) {
            throw new RuntimeException("IDENTIFICATION: Error while fetching getContract for scbAntragId " + scbAntragId, ex);
        }
    }

    private ScbCapsBcoWSStub.AuthentisierungXO buildBcoAuth() {
        ScbCapsBcoWSStub.AuthentisierungXO authentisierungXO = new ScbCapsBcoWSStub.AuthentisierungXO();
        authentisierungXO.setBenutzer(santanderConfig.getUsername());
        authentisierungXO.setPasswort(santanderConfig.getPassword());
        return authentisierungXO;
    }

    private ScbCapsDocsWSStub.AuthenticationXO buildDocsAuth() {
        ScbCapsDocsWSStub.AuthenticationXO authenticationXO = new ScbCapsDocsWSStub.AuthenticationXO();
        authenticationXO.setUser(santanderConfig.getUsername());
        authenticationXO.setPassword(santanderConfig.getPassword());
        return authenticationXO;
    }

    private byte[] deserializeFile(final javax.activation.DataHandler file, final String scbAntragId) {
        try (final InputStream contract = file.getInputStream()) {
            return toByteArray(contract);
        } catch (final IOException ex) {
            throw new RuntimeException("IDENTIFICATION: Error happened while deserializing file for scbAntragId " + scbAntragId, ex);
        }
    }

    // https://bonify.atlassian.net/browse/B2B-733
    // https://bonify.atlassian.net/browse/B2B-771
    // When user action is created with webId, mdti property is passed in request, check: SantanderCreateUserActionRequestMapper class.
    // It is requested to send transactionId as mdti, when creating contract entry. This is the reason transactionId is passed as scbAntragId.
    public Mono<ResponseEntity<Void>> createContractEntry(String scbAntragId, String actionId) {
        return santanderWebIdCoreClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/entry").build())
                .bodyValue(ContractEntryRequest.builder()
                        .applicationNo(scbAntragId)
                        .transactionId("bc_" + scbAntragId)
                        .actionId(actionId)
                        .contractDomain(ContractDomain.DIRECT)
                        .build())
                .retrieve()
                .onStatus(httpStatus -> {
                    apiMetric.incrementStatusCounter(httpStatus, ApiComponent.SANTANDER, ApiName.CREATE_CONTRACT);
                    return false;
                }, clientResponse -> Mono.empty())
                .toBodilessEntity()
                .doOnError(throwable -> log.error("Failed creating contract entry for scbAntragId: {}", scbAntragId, throwable));
    }
}
