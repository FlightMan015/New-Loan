package de.joonko.loan.integrations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class})
public class IntegrationIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void whenNewUserThenValidResponse() {
        //when mongodb state
        //then response should be

    }

    @Test
    void whenExistingUserThenValidResponse() {
        //when mongodb state
        //then response should be

    }
}
