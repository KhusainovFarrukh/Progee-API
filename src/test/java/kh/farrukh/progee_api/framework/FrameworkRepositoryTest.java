package kh.farrukh.progee_api.framework;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class FrameworkRepositoryTest {

    @Autowired
    private FrameworkRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void existsByName_returnsTrue_whenFrameworkExistsByName() {
        // given
        String name = "Spring Boot";
        underTest.save(new Framework(name));

        // when
        boolean actual = underTest.existsByName(name);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void existsByName_returnsFalse_whenFrameworkDoesNotExistWithName() {
        // given
        String name = "Spring Boot";
        underTest.save(new Framework("Ktor"));

        // when
        boolean actual = underTest.existsByName(name);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByName_returnsFalse_whenNameIsEmpty() {
        // given
        underTest.save(new Framework("Spring Boot"));

        // when
        boolean actual = underTest.existsByName("");

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByName_returnsFalse_whenNameIsNull() {
        // given
        underTest.save(new Framework("Spring Boot"));

        // when
        boolean actual = underTest.existsByName(null);

        // then
        assertThat(actual).isFalse();
    }
}