package de.joonko.loan.identification;

import de.joonko.loan.identification.model.CreateIdentRequest;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotBlank;
import java.lang.reflect.Field;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class CreateIdentRequestTest {

    @Test
    void hsnShouldHaveNotBlankAnnotation() throws NoSuchFieldException {
        assertValidationAnnotation(NotBlank.class, "applicationId");
        assertValidationAnnotation(NotBlank.class, "loanOfferId");
        assertValidationAnnotation(NotBlank.class, "firstName");
        assertValidationAnnotation(NotBlank.class, "lastName");
        assertValidationAnnotation(NotBlank.class, "gender");
        assertValidationAnnotation(NotBlank.class, "birthday");
        assertValidationAnnotation(NotBlank.class, "city");
        assertValidationAnnotation(NotBlank.class, "zipCode");
        assertValidationAnnotation(NotBlank.class, "country");
        assertValidationAnnotation(NotBlank.class, "mobilePhone");
        assertValidationAnnotation(NotBlank.class, "email");
        assertValidationAnnotation(NotBlank.class, "language");
        assertValidationAnnotation(NotBlank.class, "loanProvider");
    }

    private void assertValidationAnnotation(Class annotationCls, String property) throws NoSuchFieldException {
        Field field = CreateIdentRequest.class.getDeclaredField(property);
        assertThat(field.getDeclaredAnnotation(annotationCls), notNullValue());
    }
}