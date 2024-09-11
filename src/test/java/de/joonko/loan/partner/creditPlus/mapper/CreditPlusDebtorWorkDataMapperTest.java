package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.EmploymentDetails;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class CreditPlusDebtorWorkDataMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusDebtorWorkDataMapper mapper;

    @Random
    EmploymentDetails employmentDetails;

    private EfinComparerServiceStub.WorkData workData = new EfinComparerServiceStub.WorkData();

    @BeforeEach
    void setUp() {
        workData = mapper.toWorkData(employmentDetails);
    }


    @Test
    void toWorkData() {
        assertNotNull(workData);
    }

    @Test
    void toCategory() {
        assertEquals(workData.getCategory(), Integer.valueOf(CreditPlusDefaults.CATEGORY));
    }

    @Test
    void toIndustry() {
        assertEquals(workData.getIndustry(), Integer.valueOf(CreditPlusDefaults.INDUSTRY));
    }

    @Test
    void toEmployerName() {
        assertEquals(workData.getEmployerName(), employmentDetails.getEmployerName());
    }

    @Test
    void toEmployerStreet() {
        assertEquals(workData.getEmployerStreet(), employmentDetails.getStreetName());
    }

    @Test
    void toEmployerPostalCode() {
        assertEquals(workData.getEmployerPostalCode(), employmentDetails.getZipCode().getCode());
    }

    @Test
    void toEmployerCity() {
        assertEquals(workData.getEmployerCity(), employmentDetails.getCity());
    }

    @Test
    void toEmployedDate() {
        assertEquals(workData.getEmployedDate(), localDateToCalendar(employmentDetails.getEmploymentSince()));
    }

    @Test
    void toEmployeeLimitationFalse() {
        employmentDetails.setProfessionEndDate(null);
        workData = mapper.toWorkData(employmentDetails);
        assertFalse(workData.getEmployeeLimitation());
    }

    @Test
    void toEmployeeLimitationTrue() {
        employmentDetails.setProfessionEndDate(LocalDate.now());
        mapper.toWorkData(employmentDetails);
        assertTrue(workData.getEmployeeLimitation());
    }

    @Test
    void toDateOfEmployeeLimitation() {
        assertEquals(workData.getDateOfEmployeeLimitation(), localDateToCalendar(employmentDetails.getProfessionEndDate()));
    }

    /*
    @Mapping(target = "category",constant = CreditPlusDefaults.CATEGORY)//TODO ask Jana!
    @Mapping(target = "industry",constant = CreditPlusDefaults.INDUSTRY)//TODO ask Jana!
    @Mapping(target = "employerName", source = "employerName")
    @Mapping(target = "employerStreet", source = "streetName")
    @Mapping(target = "employerPostalCode", source = "zipCode.code")
    @Mapping(target = "employerCity", source = "city")
    @Mapping(target = "employedDate", source = "employmentSince")
    @Mapping(target = "employeeLimitation", source = "professionEndDate", qualifiedByName = "getEmployeeLimitation")
    @Mapping(target = "dateOfEmployeeLimitation", source = "professionEndDate")
     */

    @Test
    void getEmployeeLimitationTrue() {
        assertTrue(mapper.getEmployeeLimitation(LocalDate.now()));
    }

    @Test
    void getEmployeeLimitationFalse() {
        assertFalse(mapper.getEmployeeLimitation(null));
    }

    public Calendar localDateToCalendar(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        return calendar;
    }
}