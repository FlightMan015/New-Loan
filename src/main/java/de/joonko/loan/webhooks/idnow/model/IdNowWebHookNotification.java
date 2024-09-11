package de.joonko.loan.webhooks.idnow.model;

import lombok.Data;

import java.util.List;

@Data
public class IdNowWebHookNotification {
    private List<Identification> identifications ;
}
