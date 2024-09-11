package de.joonko.loan.partner.auxmoney;

import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

@KeySpace("auxmoneySingleOfferCallResponseStore")
@Data
@Builder
@Document
public class AuxmoneySingleOfferCallResponseStore {

    @Id
    private String auxmoneySingleOfferCallResponseId;
    private String loanApplicationId;
    private AuxmoneySingleCallResponse auxmoneySingleCallResponse;


}
