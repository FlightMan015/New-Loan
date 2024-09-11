package de.joonko.loan.email.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class Email {

    private String fromAddress;

    private String toAddress;

    private String ccAddress;

    private String bccAddress;

    private String subject;

    private String text;

    private String html;

    private List<EmailAttachments> attachments;
}
