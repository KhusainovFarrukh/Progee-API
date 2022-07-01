package kh.farrukh.progee_api.endpoints.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EmailValidatorTest {

    EmailValidator validator = new EmailValidator();

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