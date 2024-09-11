package de.joonko.loan.customer.support.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class Event {
    private String email;
    private Long createdAt;
    private String comment;
}
