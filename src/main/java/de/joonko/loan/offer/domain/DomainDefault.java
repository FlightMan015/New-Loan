package de.joonko.loan.offer.domain;

import java.time.LocalDate;

public class DomainDefault {

    private DomainDefault() {

    }

    public static final Boolean MAIN_EARNER = true;
    public static final LoanDuration LOAN_DURATION = LoanDuration.FORTY_EIGHT;
    public static final LoanCategory DEFAULT_LOAN_CATEGORY = LoanCategory.OTHER;
    public static final LocalDate ACCOUNT_SINCE = LocalDate.now()
            .minusYears(1);
    public static final BankAccountCategory DEFAULT_BANK_CATEGORY = BankAccountCategory.PRIVATE;
    public static final ClassificationProvider DEFAULT_CLASSIFICATION_PROVIDER = ClassificationProvider.FINTECH_SYSTEMS;
    public static final String COLLECTION_DAY_OF_MONTH = "1";
    public static final String CURRENCY = "EUR";

    public static final String FTS_QUERY_PARAM_VALUE_PDF = "pdf";
    public static final String FTS_QUERY_PARAM_VALUE_JSON = "json";
    public static final String FTS_QUERY_PARAM_VALUE_JSON2 = "json2";

}
