package de.joonko.loan.data.support.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
public abstract class BaseTopic {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private String id;
    private String createdAt;

    public BaseTopic() {
        setId(UUID.randomUUID().toString());
        setCreatedAt(LocalDateTime.now().format(formatter));
    }
}
