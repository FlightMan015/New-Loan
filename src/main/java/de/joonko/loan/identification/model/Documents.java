package de.joonko.loan.identification.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Documents {

    @Builder.Default
    private List<Document> documents = List.of();
}
