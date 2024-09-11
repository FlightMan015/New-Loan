package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.solaris.model.LoanStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolarisLoanStatusMapperTest extends BaseMapperTest {

    @Autowired
    SolarisLoanStatusMapper solarisLoanStatusMapper;

    @Test
    void shouldMapEsignPending() {
        LoanApplicationStatus loanApplicationStatus = solarisLoanStatusMapper.toLoanApplicationStatus(LoanStatus.ESIGN_PENDING);
        assertEquals(LoanApplicationStatus.ESIGN_PENDING, loanApplicationStatus);
    }

    @Test
    void shouldMapRejected() {
        LoanApplicationStatus loanApplicationStatus = solarisLoanStatusMapper.toLoanApplicationStatus(LoanStatus.REJECTED);
        assertEquals(LoanApplicationStatus.REJECTED, loanApplicationStatus);
    }

    @Test
    void shouldMapExpiredToRejected() {
        LoanApplicationStatus loanApplicationStatus = solarisLoanStatusMapper.toLoanApplicationStatus(LoanStatus.EXPIRED);
        assertEquals(LoanApplicationStatus.REJECTED, loanApplicationStatus);
    }

    @Test
    void shouldMapApproved() {
        LoanApplicationStatus loanApplicationStatus = solarisLoanStatusMapper.toLoanApplicationStatus(LoanStatus.APPROVED);
        assertEquals(LoanApplicationStatus.APPROVED, loanApplicationStatus);
    }

    @Test
    void shouldMapOfferedToApproved() {
        LoanApplicationStatus loanApplicationStatus = solarisLoanStatusMapper.toLoanApplicationStatus(LoanStatus.OFFERED);
        assertEquals(LoanApplicationStatus.APPROVED, loanApplicationStatus);
    }

    @Test
    void shouldMapAccountSnapshotVerification() {
        LoanApplicationStatus loanApplicationStatus = solarisLoanStatusMapper.toLoanApplicationStatus(LoanStatus.ACCOUNT_SNAPSHOT_VERIFICATION);
        assertEquals(LoanApplicationStatus.ACCOUNT_SNAPSHOT_VERIFICATION, loanApplicationStatus);
    }

    @Test
    void shouldMapEsignCompleteToNUll() {
        LoanApplicationStatus loanApplicationStatus = solarisLoanStatusMapper.toLoanApplicationStatus(LoanStatus.ESIGN_COMPLETE);
        assertEquals(null, loanApplicationStatus);
    }

    @Test
    void shouldMapEsignFailedToNUll() {
        LoanApplicationStatus loanApplicationStatus = solarisLoanStatusMapper.toLoanApplicationStatus(LoanStatus.ESIGN_FAILED);
        assertEquals(null, loanApplicationStatus);
    }

    @Test
    void shouldMapLoanCreatedToNUll() {
        LoanApplicationStatus loanApplicationStatus = solarisLoanStatusMapper.toLoanApplicationStatus(LoanStatus.LOAN_CREATED);
        assertEquals(null, loanApplicationStatus);
    }
}
