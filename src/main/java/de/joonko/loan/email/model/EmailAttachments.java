package de.joonko.loan.email.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;

@Getter
@Setter
@Builder
@Slf4j
@AllArgsConstructor
public class EmailAttachments {

    @JsonProperty("filename")
    private String fileName;
    private String content;
    private String encoding;
    private String cid;
}
