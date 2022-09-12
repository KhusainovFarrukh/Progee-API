package kh.farrukh.progee_api.endpoints.auth;

import kh.farrukh.progee_api.auth.EmailValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
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