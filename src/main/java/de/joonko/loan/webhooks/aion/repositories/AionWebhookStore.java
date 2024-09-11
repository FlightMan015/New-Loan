package de.joonko.loan.webhooks.aion.repositories;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document("aionWebhookStore")
public class AionWebhookStore {

    @Id
    private String id;

    private String aionWebhookId;

    private String sourceSystem;

    private OffsetDateTime eventDateTime;

    private String type;

    private Payload payload;

    @CreatedDate
    private LocalDateTime insertTS;
}
