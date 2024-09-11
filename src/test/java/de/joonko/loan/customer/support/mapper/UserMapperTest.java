package de.joonko.loan.customer.support.mapper;

import de.joonko.loan.customer.support.model.User;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import io.github.glytching.junit.extension.random.Random;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserMapperTest extends BaseMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("Should convert LoanDemand.id to User.loanApplicationId")
    void toAmount(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getLoanApplicationId()
                .toString(), user.getLoanApplicationId());
    }

    @Test
    @DisplayName("Should convert LoanDemand.loanAsked to User.loanAsked")
    void toLoanAsked(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getLoanAsked(), user.getLoanAsked());
    }

    @Test
    @DisplayName("Should convert LoanDemand.contactData.email.emailString to User.email")
    void toEmail(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getContactData().getEmail().getEmailString(), user.getEmail());
    }

    @Test
    @DisplayName("Should convert LoanDemand.contactData.mobile to User.phone")
    void toPhone(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getContactData().getMobile(), user.getPhone());
    }

    @Test
    @DisplayName("Should convert LoanDemand.personalDetails.firstName to User.firstName")
    void toFirstName(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getPersonalDetails().getFirstName(), user.getFirstName());
    }

    @Test
    @DisplayName("Should convert LoanDemand.personalDetails.lastName to User.lastName")
    void toLastName(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getPersonalDetails().getLastName(), user.getLastName());
    }

    @Test
    @DisplayName("Should convert LoanDemand.contactData.streetNumber to User.houseNumber")
    void toHouseNumber(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getContactData().getStreetNumber(), user.getHouseNumber());
    }

    @Test
    @DisplayName("Should convert LoanDemand.contactData.streetName to User.street")
    void toStreet(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getContactData().getStreetName(), user.getStreet());
    }

    @Test
    @DisplayName("Should convert LoanDemand.contactData.city to User.city")
    void toCity(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getContactData().getCity(), user.getCity());
    }

    @Test
    @DisplayName("Should convert LoanDemand.contactData.zipCode.code to User.postalCode")
    void toPostalCode(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals(loanDemand.getContactData().getZipCode().getCode(), user.getPostalCode());
    }

    @Test
    @DisplayName("Should set constant 1 to User.payment_date")
    void toPaymentDate(@Random LoanDemand loanDemand) {
        User user = userMapper.mapToUser(loanDemand);
        Assert.assertEquals("1", user.getPayment_date());
    }

}
