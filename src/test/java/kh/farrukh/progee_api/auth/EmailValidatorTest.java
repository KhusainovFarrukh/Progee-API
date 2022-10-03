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
    void test_returnsTrue_whenEmailIsValid() {
        // given
        String email = "farrukh@mail.com";

        // when
        boolean actual = validator.test(email);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void test_returnsFalse_whenEmailIsInvalid() {
        // given
        String email = "farrukh.mail.com";

        // when
        boolean actual = validator.test(email);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void test_returnsFalse_whenEmailIsEmpty() {
        // given
        String email = "";

        // when
        boolean actual = validator.test(email);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void test_returnsFalse_whenEmailIsNull() {
        // given
        String email = null;

        // when
        boolean actual = validator.test(email);

        // then
        assertThat(actual).isFalse();
    }
}