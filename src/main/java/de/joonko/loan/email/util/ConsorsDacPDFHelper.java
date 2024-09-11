package de.joonko.loan.email.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ConsorsDacPDFHelper {

    private static final String COVER_PAGE_TEMPLATE = "consors/consorsDACCoverPageTemplate.pdf";

    public static InputStream addCoverLetterToPdf(InputStream inputStream, String customerName, String contractIdentifier) {
        log.debug("Creating Consors PDF document for customer {} and contract {}", customerName, contractIdentifier);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDFMergerUtility pdfMerger = new PDFMergerUtility();
            pdfMerger.setDestinationStream(outputStream);
            pdfMerger.addSource(getCoverPageDocument(customerName, contractIdentifier));
            pdfMerger.addSource(inputStream);
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

            outputStream.flush();
            byte[] byteArray = outputStream.toByteArray();
            log.debug("Finished generating {} byte Consors PDF for customer {} and contract {}",
                    byteArray.length, customerName, contractIdentifier);

            return new ByteArrayInputStream(byteArray);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to add cover letter to DAC pdf", e);
            throw new RuntimeException("Failed to add cover page to PDF: " + e.getMessage());
        }
    }

    private static ByteArrayInputStream getCoverPageDocument(String customerName, String contractIdentifier) throws IOException {
        log.debug("Loading PDF template {} for DAC Cover Page", COVER_PAGE_TEMPLATE);
        Resource resource = new ClassPathResource(COVER_PAGE_TEMPLATE);
        try (ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
             PDDocument coverPageDocument = PDDocument.load(resource.getInputStream())) {

            PDAcroForm acroForm = coverPageDocument.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                throw new IllegalStateException("DAC Cover Page template PDF has no form fields. Expecting a form " +
                        "with 'Kundenname' and 'Anfragenummer' text fields.");
            }

            PDField kundenName = acroForm.getField("Kundenname");
            kundenName.setValue(customerName);

            PDField anfrageNummer = acroForm.getField("Anfragenummer");
            anfrageNummer.setValue(contractIdentifier);

            acroForm.getFields().stream().filter(field -> field.getFieldType().equals("Btn"))
                    .forEach(field -> field.getCOSObject().clear());

            acroForm.flatten();

            coverPageDocument.save(baosPDF);
            return new ByteArrayInputStream(baosPDF.toByteArray());
        }
    }
}
