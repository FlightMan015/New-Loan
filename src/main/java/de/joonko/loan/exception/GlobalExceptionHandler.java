package de.joonko.loan.exception;


import de.joonko.loan.identification.exception.ExternalIdentIdNotFoundException;
import de.joonko.loan.offer.ResourceNotFoundException;
import de.joonko.loan.user.UserDataNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import io.fusionauth.client.FusionAuthClientException;
import lombok.extern.slf4j.Slf4j;


@ControllerAdvice(annotations = GenericExceptionHandler.class)
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler({WebExchangeBindException.class})
    public ResponseEntity<ErrorDetails> handleBadRequestException(WebExchangeBindException ex) {
        log.error("Exception caught in handleBadRequestException {}", toErrorMessageString(ex), ex);
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDetails("Validation Error", errorMessages));
    }

    @ExceptionHandler({ServerWebInputException.class})
    public ResponseEntity<ErrorDetails> handleBadRequestException(ServerWebInputException ex) {
        log.error("Exception caught in handleBadRequestException {}", toErrorMessageString(ex), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDetails("Json parsing error ", List.of(ex.getMessage())));
    }

    @ExceptionHandler(FusionAuthClientException.class)
    public ResponseEntity handleFusionAuthException(RuntimeException ex) {
        log.error("Exception caught in handleFusionAuthException: {}", toErrorMessageString(ex), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @ExceptionHandler({UserDataNotFoundException.class, ExternalIdentIdNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity handleNotFoundResourceException(RuntimeException ex) {
        log.info("Exception caught in handleResourceNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        log.error("Exception caught in ValidationException {}", toErrorMessageString(ex), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // runtime exception should be last exception to handle
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("Exception caught in handleRuntimeException {}", toErrorMessageString(ex), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    private Object toErrorMessageString(RuntimeException ex) {
        if (null != ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        }
        return null;
    }


}
