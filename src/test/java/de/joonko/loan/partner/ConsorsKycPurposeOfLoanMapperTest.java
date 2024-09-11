package de.joonko.loan.partner;

import de.joonko.loan.offer.domain.LoanCategory;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.mapper.ConsorsKycPurposeOfLoanMapper;
import de.joonko.loan.partner.consors.model.KycPurposeOfLoan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConsorsKycPurposeOfLoanMapperTest extends BaseMapperTest {

    @Autowired
    private ConsorsKycPurposeOfLoanMapper consorsKycPurposeOfLoanMapper;

    @ParameterizedTest(name = "Should  Not Miss any Mapping from LoanCategory with Value [{arguments}]")
    @EnumSource(LoanCategory.class)
    void toProvidersKycPurposeOfLoan(LoanCategory loanCategory) {
        assertNotNull(consorsKycPurposeOfLoanMapper.toProvidersKycPurposeOfLoan(loanCategory));
    }

    @Test
    void ama() {
        assertEquals(KycPurposeOfLoan.AMA, consorsKycPurposeOfLoanMapper.toProvidersKycPurposeOfLoan(LoanCategory.CAR_LOAN));
    }

    @Test
    void ERM() {
        assertEquals(KycPurposeOfLoan.ERM, consorsKycPurposeOfLoanMapper.toProvidersKycPurposeOfLoan(LoanCategory.FURNITURE_RENOVATION_MOVE));
    }

    @Test
    void rue() {
        assertEquals(KycPurposeOfLoan.RUE, consorsKycPurposeOfLoanMapper.toProvidersKycPurposeOfLoan(LoanCategory.VACATION));
    }

    @Test
    void fsh() {
        assertEquals(KycPurposeOfLoan.FSH, consorsKycPurposeOfLoanMapper.toProvidersKycPurposeOfLoan(LoanCategory.OTHER));
    }

    @Test
    void elk() {
        assertEquals(KycPurposeOfLoan.ELK, consorsKycPurposeOfLoanMapper.toProvidersKycPurposeOfLoan(LoanCategory.ELECTRONICS));
    }


}
