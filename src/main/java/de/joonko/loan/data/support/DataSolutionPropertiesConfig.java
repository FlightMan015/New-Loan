package de.joonko.loan.data.support;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class DataSolutionPropertiesConfig {

    @Value("${ds.delayInSec.offers:300}")
    private int delayLoanOffer;
}
