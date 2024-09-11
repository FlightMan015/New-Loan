package de.joonko.loan.webhooks.aion.model;

import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AionWebhookRequest {

    private String id;

    private String sourceSystem;

    private ZonedDateTime eventDateTime;

    @NotNull(message = "type must not be null")
    @Pattern(regexp = "credits\\.cashloan\\..*", message = "invalid webhook type")
    private String type;

    @NotNull(message = "payload must not be null")
    @Valid
    private Payload payload;
}
