package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.EmploymentDetails;
import de.joonko.loan.partner.consors.model.Profession;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

class EmploymentDetailsMapperTest extends BaseMapperTest {

    @Autowired
    EmploymentDetailsMapper mapper;

    @Test
    void should_map_consors_employment_details(@Random de.joonko.loan.offer.domain.EmploymentDetails employerDetailsDomain) {
        employerDetailsDomain.setEmploymentType(EmploymentType.REGULAR_EMPLOYED);
        employerDetailsDomain.setCity("Berlin");
        employerDetailsDomain.setEmploymentSince(LocalDate.of(2015, 5, 1));
        EmploymentDetails employmentDetails = mapper.toEmploymentDetails(employerDetailsDomain);

        assertThat(employmentDetails.getProfession()).isEqualTo(Profession.REGULAR_EMPLOYED);
        assertThat(employmentDetails.getProfessionBeginDate()).isEqualTo("2015-05");
        assertNotNull(employmentDetails.getEmployerAddress());
        assertThat(employmentDetails.getEmployerAddress()
                .getEmployerCity()).isEqualTo("Berlin");
    }

    @Test
    void should_map_consors_profession_end_date(@Random de.joonko.loan.offer.domain.EmploymentDetails employerDetailsDomain) {
        employerDetailsDomain.setEmploymentSince(LocalDate.of(2015, 5, 1));
        employerDetailsDomain.setProfessionEndDate(LocalDate.of(2018, 8, 1));
        EmploymentDetails employmentDetails = mapper.toEmploymentDetails(employerDetailsDomain);
        assertThat(employmentDetails.getProfessionBeginDate()).isEqualTo("2015-05");
        assertThat(employmentDetails.getProfessionEndDate()).isEqualTo("2018-08");
    }
}
