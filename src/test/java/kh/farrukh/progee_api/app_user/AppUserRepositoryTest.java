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
    void findByEmail_returnsUser_whenUserExistsByEmail() {
        // given
        String email = "test@mail.com";
        AppUser user = new AppUser(email);
        underTest.save(user);

        // when
        Optional<AppUser> userOptional = underTest.findByEmail(email);

        // then
        assertThat(userOptional.isPresent()).isTrue();
    }

    @Test
    void findByEmail_returnsNull_whenUserDoesNotExistByEmail() {
        // given
        String email = "test@mail.com";
        AppUser user = new AppUser("other-email@mail.com");
        underTest.save(user);

        // when
        Optional<AppUser> userOptional = underTest.findByEmail(email);

        // then
        assertThat(userOptional.isEmpty()).isTrue();
    }

    @Test
    void findByEmail_returnsNull_whenEmailIsEmpty() {
        // given
        AppUser user = new AppUser("test@mail.com");
        underTest.save(user);

        // when
        Optional<AppUser> userOptional = underTest.findByEmail("");

        // then
        assertThat(userOptional.isEmpty()).isTrue();
    }

    @Test
    void findByEmail_returnsNull_whenEmailIsNull() {
        // given
        AppUser user = new AppUser("test@mail.com");
        underTest.save(user);

        // when
        Optional<AppUser> userOptional = underTest.findByEmail(null);

        // then
        assertThat(userOptional.isEmpty()).isTrue();
    }

    @Test
    void existsByEmail_returnsTrue_whenUserExistsByEmail() {
        // given
        String email = "test@mail.com";
        AppUser user = new AppUser(email);
        underTest.save(user);

        // when
        boolean exists = underTest.existsByEmail(email);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_returnsFalse_whenUserDoesNotExistByEmail() {
        // given
        String email = "test@mail.com";
        AppUser user = new AppUser("other-email@mail.com");
        underTest.save(user);

        // when
        boolean exists = underTest.existsByEmail(email);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_returnsFalse_whenEmailIsEmpty() {
        // given
        AppUser user = new AppUser("test@mail.com");
        underTest.save(user);

        // when
        boolean exists = underTest.existsByEmail("");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_returnsFalse_whenEmailIsNull() {
        // given
        AppUser user = new AppUser("test@mail.com");
        underTest.save(user);

        // when
        boolean exists = underTest.existsByEmail(null);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByUniqueUsername_returnsTrue_whenUserExistsByUsername() {
        // given
        String username = "test_user";
        AppUser user = new AppUser("", username);
        underTest.save(user);

        // when
        boolean exists = underTest.existsByUniqueUsername(username);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUniqueUsername_returnsFalse_whenUserDoesNotExistByUsername() {
        // given
        String username = "test_user";

        // when
        boolean exists = underTest.existsByUniqueUsername(username);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByUniqueUsername_returnsFalse_whenUsernameIsEmpty() {
        // given
        String username = "test_user";
        AppUser user = new AppUser("", username);
        underTest.save(user);

        // when
        boolean exists = underTest.existsByUniqueUsername("");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByUniqueUsername_returnsFalse_whenUsernameIsNull() {
        // given
        String username = "test_user";
        AppUser user = new AppUser("", username);
        underTest.save(user);

        // when
        boolean exists = underTest.existsByUniqueUsername(null);

        // then
        assertThat(exists).isFalse();
    }
}