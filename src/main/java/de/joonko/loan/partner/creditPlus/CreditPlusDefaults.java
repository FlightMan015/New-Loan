package de.joonko.loan.partner.creditPlus;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CreditPlusDefaults {
    public static final String FALSE = "false";
    public static final String MALE = "1";
    public static final String FEMALE = "2";
    public static final String DEFAULT_DURATION = "18";
    public static final String ACCOUNT_OWNER = "1";
    public static final String TRANSFER_DAY = "30";
    public static final String DISPATCH_TYPE = "2";
    public static final String CATEGORY = "99";
    public static final String INDUSTRY = "16";
    public static final String RSV_TYPE = "10";
    public static final String REPLACEMENT_TYPE = "3";
    public static final String OWNER = "1";
    public static final String RENTER = "2";
    public static final String OTHER_INCOME_TYPE = "2";
    public static final String SCORE = "1";

    public static final String SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String USERNAME_TOKEN_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest";
    public static final String ENCODING_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";
    public static final String SECURITY_UTILITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

    public static final int STATUS_IN_PROGRESS = 20;//yellow
    public static final int STATUS_IN_PROGRESS_2 = 22;//yellow can be ignored
    public static final int STATUS_APPROVED = 24;//green
    public static final int STATUS_DOCS_ISSUE = 25;//yellow
    public static final int STATUS_SOFT_DECLINE = 92;//red
    public static final int STATUS_DECLINE = 93;//red
    public static final int STATUS_WITHDRAW = 95;//green
    public static final int STATUS_94 = 94;//red
    public static final int STATUS_PAID = 99;

    public static final int WAIT_TIME_FREQ = 7000;
    public static final List<String> CATEGORY_ID_RENT = List.of("K.2.1");
    public static final List<String> CATEGORY_HOUSE_COSTS = List.of("K.2.3.2");
    public static final List<String> CATEGORY_PRIVATE_HEALTH_INSURANCE = List.of("K.8.1.4.1");
    public static final List<String> CATEGORY_ALIMENT = List.of("A.3");
    public static final List<String> CATEGORY_OTHER_CREDIT_RATES = List.of("A.5.1");
    public static final List<String> CATEGORY_INCOME = List.of("E.1", "E.1.1", "E.1.1.1", "E.1.1.2");
    public static final List<String> CATEGORY_OTHER_INCOME = List.of("E.1.2", "E.1.2.1", "E.1.3", "E.3", "E.3.1", "E.3.2", "E.3.4", "E.3.4.1", "E.3.4.2", "E.3.4.3", "E.3.4.4", "E.3.4.5", "E.3.4.6", "E.3.4.7", "E.3.4.8", "E.3.4.9", "E.3.4.9.3", "E.3.4.9.4", "E.3.4.9.5", "E.3.4.9.6", "E.3.5", "E.4", "E.4.1", "E.4.1.1", "E.4.1.4", "E.4.1.7", "E.4.1.8", "E.4.3", "E.4.3.1", "E.4.3", "E.4.3.3", "E.5", "E.5.1", "E.5.2", "E.5.3", "E.5.4", "E.6", "E.99", "E.99.2", "E.99.3", "E.99.4", "E.99.5", "E.99.9");
    public static final List<String> CATEGORY_RENTAl_INCOME = List.of("E.2", "E.2.1", "E.2.1");
    public static final List<String> CATEGORY_PENSION = List.of("E.4.2", "E.4.2.1", "E.4.2.2", "E.4.2.3", "E.4.2.4", "E.4.2.5", "E.4.2.6");
    public static final List<String> CATEGORY_CHILD_BENEFIT = List.of("E.4.1.3");
    public static final List<String> CATEGORY_VEHICLE = List.of("K.4.2");
    public static final List<Integer> NON_GREEN_STATUS_LIST = List.of(STATUS_IN_PROGRESS_2, STATUS_DOCS_ISSUE, STATUS_SOFT_DECLINE, STATUS_DECLINE, STATUS_PAID, STATUS_94);
    public static final String FRONTEND_TYPE = "Efin";
    public static final String DEBTOR_TYPE = "0";
    public static final String ENCRYPTION_ALGORITHM = "PBEWithMD5AndDES";
    public static final String ENCRYPTIOn_PASSWORD = "31Nz-hn3H6!)e#p~lg5G" ; //In essence its not a password but a constant for Credit+ use-case
    public static final Integer POOL_SIZE = 4;
    public static final String DELIMITER = "-";

}
