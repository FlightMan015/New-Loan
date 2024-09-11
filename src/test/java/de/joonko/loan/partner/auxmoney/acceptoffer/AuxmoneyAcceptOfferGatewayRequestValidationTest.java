//package de.joonko.loan.partner.auxmoney.acceptoffer;
//
//import de.joonko.loan.partner.auxmoney.model.AuxmoneyAcceptOfferRequest;
//import io.github.glytching.junit.extension.random.Random;
//import io.github.glytching.junit.extension.random.RandomBeansExtension;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import java.util.Set;
//
//@ExtendWith(RandomBeansExtension.class)
//class AuxmoneyAcceptOfferGatewayRequestValidationTest {
//
//    @Random
//    private AuxmoneyAcceptOfferRequest auxMoneyAcceptOfferRequest;
//
//    private static Validator validator;
//
//
//    @BeforeAll
//    static void setup() {
//        validator = Validation.buildDefaultValidatorFactory().getValidator();
//    }
//
//    @Test
//    @DisplayName("Should Return Validation Error For Null Credit Id")
//    void shouldValidateNullCreditId() {
//        auxMoneyAcceptOfferRequest.setCreditId(null);
//        Set<ConstraintViolation<AuxmoneyAcceptOfferRequest>> validate = validator.validate(auxMoneyAcceptOfferRequest);
//        long creditIdIsMandatory = validate.stream()
//                .filter(it -> it.getMessage().contains("Credit id is mandatory"))
//                .count();
//        Assertions.assertEquals(1, creditIdIsMandatory);
//    }
//
//    @Test
//    @DisplayName("Should Not Return Validation Error For valid Credit Id")
//    void shouldValidateValidCreditId() {
//        Set<ConstraintViolation<AuxmoneyAcceptOfferRequest>> validate = validator.validate(auxMoneyAcceptOfferRequest);
//        long creditIdIsMandatory = validate.stream()
//                .filter(it -> it.getMessage().contains("Credit id is mandatory"))
//                .count();
//        Assertions.assertEquals(0, creditIdIsMandatory);
//    }
//
//    @Test
//    @DisplayName("Should Return Validation Error For Null User Id")
//    void shouldValidateNullUserId() {
//        auxMoneyAcceptOfferRequest.setUserId(null);
//        Set<ConstraintViolation<AuxmoneyAcceptOfferRequest>> validate = validator.validate(auxMoneyAcceptOfferRequest);
//        long userIdIsMandatory = validate.stream()
//                .filter(it -> it.getMessage().contains("User id is mandatory"))
//                .count();
//        Assertions.assertEquals(1, userIdIsMandatory);
//    }
//
//    @Test
//    @DisplayName("Should not return validation Error For valid User Id")
//    void shouldValidateValidUserId() {
//        Set<ConstraintViolation<AuxmoneyAcceptOfferRequest>> validate = validator.validate(auxMoneyAcceptOfferRequest);
//        long userIdIsMandatory = validate.stream()
//                .filter(it -> it.getMessage().contains("User id is mandatory"))
//                .count();
//        Assertions.assertEquals(0, userIdIsMandatory);
//    }
//
//    @Test
//    @DisplayName("Should return validation error for Null price Id")
//    void shouldValidateNullPriceId () {
//        auxMoneyAcceptOfferRequest.setPriceId(null);
//        Set<ConstraintViolation<AuxmoneyAcceptOfferRequest>> validate = validator.validate(auxMoneyAcceptOfferRequest);
//        long priceIdIsMandatory = validate.stream()
//                .filter(it -> it.getMessage().contains("Price id is mandatory"))
//                .count();
//        Assertions.assertEquals(1, priceIdIsMandatory);
//    }
//
//    @Test
//    @DisplayName("Should not return validation error for valid price Id")
//    void shouldValidateValidPriceId () {
//        Set<ConstraintViolation<AuxmoneyAcceptOfferRequest>> validate = validator.validate(auxMoneyAcceptOfferRequest);
//        long priceIdIsMandatory = validate.stream()
//                .filter(it -> it.getMessage().contains("Price id is mandatory"))
//                .count();
//        Assertions.assertEquals(0, priceIdIsMandatory);
//    }
//
//    @Test
//    @DisplayName("Should return validation error for Null bank data")
//    void shouldValidateNullBankData () {
//        auxMoneyAcceptOfferRequest.setBankData(null);
//        Set<ConstraintViolation<AuxmoneyAcceptOfferRequest>> validate = validator.validate(auxMoneyAcceptOfferRequest);
//        long bankDataIsMandatory = validate.stream()
//                .filter(it -> it.getMessage().contains("Bank data is mandatory"))
//                .count();
//        Assertions.assertEquals(1, bankDataIsMandatory);
//    }
//
//    @Test
//    @DisplayName("Should not return validation error for valid bank data")
//    void shouldValidateValidBankData () {
//        Set<ConstraintViolation<AuxmoneyAcceptOfferRequest>> validate = validator.validate(auxMoneyAcceptOfferRequest);
//        long bankDataIsMandatory = validate.stream()
//                .filter(it -> it.getMessage().contains("Bank data is mandatory"))
//                .count();
//        Assertions.assertEquals(0, bankDataIsMandatory);
//    }
//
//}
