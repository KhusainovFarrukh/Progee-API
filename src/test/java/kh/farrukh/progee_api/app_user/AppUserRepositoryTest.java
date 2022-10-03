package kh.farrukh.progee_api.app_user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByEmail_returnsValidData_whenUserExistsByEmail() {
        // given
        String email = "test@mail.com";
        underTest.save(new AppUser(email));

        // when
        Optional<AppUser> actual = underTest.findByEmail(email);

        // then
        assertThat(actual.isPresent()).isTrue();
    }

    @Test
    void findByEmail_returnsEmptyData_whenUserDoesNotExistByEmail() {
        // given
        String email = "test@mail.com";
        underTest.save(new AppUser("other-email@mail.com"));

        // when
        Optional<AppUser> actual = underTest.findByEmail(email);

        // then
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void findByEmail_returnsEmptyData_whenEmailIsEmpty() {
        // given
        underTest.save(new AppUser("test@mail.com"));

        // when
        Optional<AppUser> actual = underTest.findByEmail("");

        // then
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void findByEmail_returnsEmptyData_whenEmailIsNull() {
        // given
        underTest.save(new AppUser("test@mail.com"));

        // when
        Optional<AppUser> actual = underTest.findByEmail(null);

        // then
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    void existsByEmail_returnsTrue_whenUserExistsByEmail() {
        // given
        String email = "test@mail.com";
        underTest.save(new AppUser(email));

        // when
        boolean actual = underTest.existsByEmail(email);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void existsByEmail_returnsFalse_whenUserDoesNotExistByEmail() {
        // given
        String email = "test@mail.com";
        underTest.save(new AppUser("other-email@mail.com"));

        // when
        boolean actual = underTest.existsByEmail(email);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByEmail_returnsFalse_whenEmailIsEmpty() {
        // given
        underTest.save(new AppUser("test@mail.com"));

        // when
        boolean actual = underTest.existsByEmail("");

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByEmail_returnsFalse_whenEmailIsNull() {
        // given
        underTest.save(new AppUser("test@mail.com"));

        // when
        boolean actual = underTest.existsByEmail(null);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByUniqueUsername_returnsTrue_whenUserExistsByUsername() {
        // given
        String username = "test_user";
        underTest.save(new AppUser("", username));

        // when
        boolean actual = underTest.existsByUniqueUsername(username);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void existsByUniqueUsername_returnsFalse_whenUserDoesNotExistByUsername() {
        // given
        String username = "test_user";

        // when
        boolean actual = underTest.existsByUniqueUsername(username);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByUniqueUsername_returnsFalse_whenUsernameIsEmpty() {
        // given
        String username = "test_user";
        underTest.save(new AppUser("", username));

        // when
        boolean actual = underTest.existsByUniqueUsername("");

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByUniqueUsername_returnsFalse_whenUsernameIsNull() {
        // given
        String username = "test_user";
        underTest.save(new AppUser("", username));

        // when
        boolean actual = underTest.existsByUniqueUsername(null);

        // then
        assertThat(actual).isFalse();
    }
}