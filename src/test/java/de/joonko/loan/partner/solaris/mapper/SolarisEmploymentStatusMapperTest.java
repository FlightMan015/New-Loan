package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.solaris.model.EmploymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolarisEmploymentStatusMapperTest extends BaseMapperTest {

    @Autowired
    SolarisEmploymentStatusMapper solarisEmploymentStatusMapper;

    @Test
    void shouldMap_REGULAR_EMPLOYED_TO_EMPLOYED() {
        EmploymentStatus employmentStatus = solarisEmploymentStatusMapper.toProvidersEmploymentStatus(EmploymentType.REGULAR_EMPLOYED);
        assertEquals(EmploymentStatus.EMPLOYED, employmentStatus);
    }

    @Test
    void shouldMap_OTHER_TO_UNEMPLOYED() {
        EmploymentStatus employmentStatus = solarisEmploymentStatusMapper.toProvidersEmploymentStatus(EmploymentType.OTHER);
        assertEquals(EmploymentStatus.UNEMPLOYED, employmentStatus);
    }
}
