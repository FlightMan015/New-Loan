package de.joonko.loan.email.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

import static de.joonko.loan.email.util.ConsorsDacPDFHelper.addCoverLetterToPdf;
import static org.assertj.core.api.Assertions.*;


class ConsorsDacPDFHelperTest {

    private static final String CONTRACT_PDF = "webid_contract_470493003_NjI4NDYzNzZfMjU3NTI3Mg_Contract_62846376.pdf";
    private static final int CONTRACT_PDF_PAGES = 19;
    private static final String CUSTOMER = "Hartmut Mustermann";
    private static final String CONTRACT_ID = "62846376";

    @Test
    public void helperGeneratesCorrectNumberOfPagesInOutputPdf() throws IOException {
        Resource resource = new ClassPathResource(CONTRACT_PDF);
        try (InputStream is = addCoverLetterToPdf(resource.getInputStream(), CUSTOMER, CONTRACT_ID);
             PDDocument document = PDDocument.load(is)) {

            int numberOfPages = document.getNumberOfPages();
            assertThat(numberOfPages).isEqualTo(CONTRACT_PDF_PAGES + 1);
        }
    }

    @Test
    public void helperRaisesRuntimeExceptionOnNonPdfInputStream() {
        Resource resource = new ClassPathResource("application-integration.properties");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> addCoverLetterToPdf(resource.getInputStream(), CUSTOMER, CONTRACT_ID));
    }

}
