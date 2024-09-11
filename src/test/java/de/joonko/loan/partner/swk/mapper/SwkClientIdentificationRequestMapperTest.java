package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SwkCreateIdentRequestMapper extends BaseMapperTest {

    @Autowired
    private SwkClientIdentificationRequestMapper clientIdentificationRequestMapper;
    @Random
    private LoanDemand loanDemand;

    @Test
    void toRequestId() {
        CreditApplicationServiceStub.ClientIdentification clientIdentification = clientIdentificationRequestMapper.toClientIdentification(loanDemand);
        Assert.assertEquals(loanDemand.getLoanApplicationId()
                .toString()
                .substring(0, 23), clientIdentification.getRequestId());
    }

    @Test
    void toClientSessionId() {
        CreditApplicationServiceStub.ClientIdentification clientIdentification = clientIdentificationRequestMapper.toClientIdentification(loanDemand);
        Assert.assertNull(clientIdentification.getClientSessionId());
    }

    @Test
    void toPartnerId() {
        CreditApplicationServiceStub.ClientIdentification clientIdentification = clientIdentificationRequestMapper.toClientIdentification(loanDemand);
        Assert.assertEquals("joonko_soap", clientIdentification.getPartnerId());
    }

    @Test
    void toPassword() {
        CreditApplicationServiceStub.ClientIdentification clientIdentification = clientIdentificationRequestMapper.toClientIdentification(loanDemand);
        Assert.assertEquals("somepassword", clientIdentification.getPassword());
    }

    @Test
    void toUserName() {
        CreditApplicationServiceStub.ClientIdentification clientIdentification = clientIdentificationRequestMapper.toClientIdentification(loanDemand);
        Assert.assertEquals("username", clientIdentification.getUsername());
    }

    @Test
    void toRequestType() {
        CreditApplicationServiceStub.ClientIdentification clientIdentification = clientIdentificationRequestMapper.toClientIdentification(loanDemand);
        Assert.assertEquals(1, clientIdentification.getRequestType());
    }
}
