package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.offer.domain.ZipCode;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.EmployerData;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AuxmoneyEmployerDataMapperTest extends BaseMapperTest {

    @Autowired
    AuxmoneyEmployerDataMapper employerDataMapper;

    @Test
    void should_map_consors_employment_details(@Random de.joonko.loan.offer.domain.EmploymentDetails employerDetailsDomain) {
        employerDetailsDomain.setEmploymentType(EmploymentType.REGULAR_EMPLOYED);
        employerDetailsDomain.setCity("Berlin");
        employerDetailsDomain.setEmployerName("Joonko AG");
        employerDetailsDomain.setEmploymentSince(LocalDate.of(2015, 5, 1));
        employerDetailsDomain.setStreetName("Hardenbergstr.");
        employerDetailsDomain.setZipCode(new ZipCode("10587"));

        EmployerData employerData = employerDataMapper.toAuxmoneyEmployerData(employerDetailsDomain);

        assertThat(employerData.getZip()).isEqualTo("10587");
        assertThat(employerData.getCity()).isEqualTo("Berlin");
        assertThat(employerData.getStreet()).isEqualTo("Hardenbergstr.");
        assertThat(employerData.getCompany()).isEqualTo("Joonko AG");
        assertThat(employerData.getSince()).isEqualTo("2015-05-01");
    }

}
