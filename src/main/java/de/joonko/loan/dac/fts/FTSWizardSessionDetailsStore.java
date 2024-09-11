package de.joonko.loan.dac.fts;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@KeySpace("ftsWizardSessionDetailsStore")
@Data
@Builder
@Document("ftsWizardSessionDetailsStore")
public class FTSWizardSessionDetailsStore {

  private String  bankName;
  private String  bankCode;
  private String  wizardSessionKey;
  private String  transactionId;
  private String  status;
  private String[] error;
  private String errorCode;
  private String recoverable;
  @CreatedDate
  private LocalDateTime insertTS;

}
