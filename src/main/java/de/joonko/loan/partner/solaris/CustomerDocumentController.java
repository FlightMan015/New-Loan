package de.joonko.loan.partner.solaris;

import de.joonko.loan.exception.GenericExceptionHandler;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
@Validated
public class CustomerDocumentController {


    private final CustomerDocumentService customerDocumentService;

    @GetMapping(value = "api/v1/loan/customer/document", produces = MediaType.APPLICATION_JSON_VALUE)
    public void sendCustomerDocs() {
        log.info("Started scan for which docs needs to be sent ");
        customerDocumentService.getCustomerDocs();
    }

}
