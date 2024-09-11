package de.joonko.loan.exception;

import lombok.Data;

import java.util.List;

@Data
public class ErrorDetails {
    private String message;
    private List<String> errors;

    public ErrorDetails(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }

}
