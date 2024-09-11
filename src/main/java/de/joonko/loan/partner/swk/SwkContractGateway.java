package de.joonko.loan.partner.swk;

import de.joonko.loan.config.SwkConfig;
import de.joonko.loan.partner.swk.stub.PdfGenerationServiceStub;

import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.net.URL;
import java.rmi.RemoteException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class SwkContractGateway {

    private final SwkStoreService swkStoreService;
    private final SwkConfig swkConfig;

    public Mono<byte[]> getContract(String applicationId, Integer duration) {
        log.info("Fetching contract for SWK for applicationId {} ", applicationId);
        PdfGenerationServiceStub pdfGenerationServiceStub;
        PdfGenerationServiceStub.GeneratePdfResponse generatePdfResponse;
        try {
            pdfGenerationServiceStub = new PdfGenerationServiceStub(swkConfig.getHost()
                    .concat(swkConfig.getPdfGenerationService()));
            PdfGenerationServiceStub.GeneratePdf generatePdf = new PdfGenerationServiceStub.GeneratePdf();
            generatePdf.setRequest(createPdfGenerationRequest(applicationId, duration));
            generatePdfResponse = pdfGenerationServiceStub.generatePdf(generatePdf);
            log.info("Response for PDF Service {}", generatePdfResponse.get_return()
                    .getSuccess());
            if (!generatePdfResponse.get_return()
                    .getSuccess()) {
                log.info("Error occured while downloading contract from SWK with error message {}", generatePdfResponse.get_return()
                        .getErrorDetails()
                        .getMessage());
                throw new RuntimeException("Error occured while downloading contract from SWK" + generatePdfResponse.get_return()
                        .getErrorDetails()
                        .getMessage());
            }
            return Mono.just(readBytesFromPdfUrl(generatePdfResponse.get_return()
                    .getUrl()));
        } catch (RemoteException e) {
            log.error("Error occured while downloading contract from SWK bank", e);
            throw new RuntimeException("Error occured while downloading contract from SWK due remote exception", e);
        }
    }

    private byte[] readBytesFromPdfUrl(String pdfUrl) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(pdfUrl).openStream())) {
            return in.readAllBytes();
        } catch (Exception e) {
            log.info("Exception occurred while reading file to byte {} ", e.getMessage());
        }
        return null;
    }

    private PdfGenerationServiceStub.PdfGenerationRequest createPdfGenerationRequest(String applicationId, Integer duration) {

        PdfGenerationServiceStub.PdfGenerationRequest pdfGenerationRequest = new PdfGenerationServiceStub.PdfGenerationRequest();
        PdfGenerationServiceStub.ClientIdentification clientIdentification = new PdfGenerationServiceStub.ClientIdentification();

        clientIdentification.setUsername(swkConfig.getUsername());
        clientIdentification.setPassword(swkConfig.getPassword());
        clientIdentification.setPartnerId(swkConfig.getPartnerid());
        clientIdentification.setRequestType(swkConfig.getRequestType());
        clientIdentification.setRequestId(applicationId.substring(0, 23));

        pdfGenerationRequest.setClientIdentification(clientIdentification);
        pdfGenerationRequest.setPdfGenerationRequestType(2);
        pdfGenerationRequest.setCustomerAccountNumber(swkStoreService.getCustomerNumber(applicationId, duration));

        return pdfGenerationRequest;
    }
}
