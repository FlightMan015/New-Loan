package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import de.joonko.loan.offer.api.Nationality;
import de.joonko.loan.user.service.UserPersonalInformationStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import io.fusionauth.domain.User;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FusionauthUserMapperTest {

    FusionauthUserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new FusionauthUserMapper();
    }

    @Test
    void should_merge() {
        UUID id = UUID.randomUUID();
        User actual = mapper.merge(getTestUser(id), getUserPersonalInformationStore(id));
        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals("test", actual.firstName),
                () -> assertEquals(LocalDate.of(1950, 10, 11), actual.birthDate),
                () -> assertEquals("test@email.com", actual.email),
                () -> assertEquals("+49156789", actual.mobilePhone),
                () -> assertEquals("Berlin", actual.data.get("addressCity")),
                () -> assertEquals(Nationality.TC, actual.data.get("nationality"))
        );
    }

    @Test
    void should_handle_mobileFormat() {
        UUID id = UUID.randomUUID();
        UserPersonalInformationStore personalIfo = getUserPersonalInformationStore(id);
        personalIfo.setMobilePhone("0049156789");
        User actual = mapper.merge(getTestUser(id), personalIfo);
        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals("+49156789", actual.mobilePhone)
        );
    }

    private User getTestUser(UUID uuid) {
        User user = new User();
        user.id = uuid;
        user.email = "test@email.com";
        user.mobilePhone = "+49123-456";
        return user;
    }

    private UserPersonalInformationStore getUserPersonalInformationStore(UUID userUuid) {
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID(userUuid.toString());
        userPersonalInformationStore.setAddressCity("Berlin");
        userPersonalInformationStore.setNationality(Nationality.TC);
        userPersonalInformationStore.setFirstName("test");
        userPersonalInformationStore.setLastName("name");
        userPersonalInformationStore.setMobilePhone("49156789");
        userPersonalInformationStore.setBirthDate(LocalDate.of(1950, 10, 11));
        return userPersonalInformationStore;
    }
}
