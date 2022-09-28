package kh.farrukh.progee_api.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EmailValidator.class)
class EmailValidatorTest {

    @Autowired
    private EmailValidator validator;

    @Test
    void returnTrueIfValidEmail() {
        // given
        String email = "farrukh@mail.com";

        // when
        boolean isValid = validator.test(email);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void returnFalseIfInvalidEmail() {
        // given
        String email = "farrukh.mail.com";

        // when
        boolean isValid = validator.test(email);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void returnFalseIfEmpty() {
        // given
        String email = "";

        // when
        boolean isValid = validator.test(email);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void returnFalseIfNull() {
        // given
        String email = null;

        // when
        boolean isValid = validator.test(email);

        // then
        assertThat(isValid).isFalse();
    }
}