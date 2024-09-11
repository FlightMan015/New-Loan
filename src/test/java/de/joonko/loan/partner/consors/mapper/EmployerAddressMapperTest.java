package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.ZipCode;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.EmployerAddress;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class EmployerAddressMapperTest extends BaseMapperTest {

    @Autowired
    EmployerAddressMapper mapper;

    @Test
    void should_map_consors_employment_details(@Random de.joonko.loan.offer.domain.EmploymentDetails employmentDetails) {
        employmentDetails.setCity("Berlin");
        employmentDetails.setEmployerName("Joonko AG");
        employmentDetails.setStreetName("Hardenbergstr.");
        employmentDetails.setZipCode(new ZipCode("10587"));
        EmployerAddress employerAddress = mapper.toEmployerAddress(employmentDetails);

        assertThat(employerAddress.getEmployerStreet()).isEqualTo("Hardenbergstr.");
        assertThat(employerAddress.getEmployerCity()).isEqualTo("Berlin");
        assertThat(employerAddress.getEmployerName()).isEqualTo("Joonko AG");
        assertThat(employerAddress.getEmployerZipcode()).isEqualTo("10587");
    }

    @Test
    void should_trim_white_spaces(@Random de.joonko.loan.offer.domain.EmploymentDetails employmentDetails) {
        employmentDetails.setCity(" Berlin");
        employmentDetails.setEmployerName("Joonko AG    ");
        employmentDetails.setStreetName("    Hardenbergstr.");
        employmentDetails.setZipCode(new ZipCode(" 10587"));
        EmployerAddress employerAddress = mapper.toEmployerAddress(employmentDetails);

        assertThat(employerAddress.getEmployerStreet()).isEqualTo("Hardenbergstr.");
        assertThat(employerAddress.getEmployerCity()).isEqualTo("Berlin");
        assertThat(employerAddress.getEmployerName()).isEqualTo("Joonko AG");
        assertThat(employerAddress.getEmployerZipcode()).isEqualTo("10587");
    }

}
