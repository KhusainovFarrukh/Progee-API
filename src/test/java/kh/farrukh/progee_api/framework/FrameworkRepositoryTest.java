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
        Framework framework = new Framework(name);
        underTest.save(framework);

        // when
        boolean exists = underTest.existsByName(name);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_returnsFalse_whenFrameworkDoesNotExistWithName() {
        // given
        String name = "Spring Boot";
        Framework framework = new Framework("Ktor");
        underTest.save(framework);

        // when
        boolean exists = underTest.existsByName(name);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByName_returnsFalse_whenNameIsEmpty() {
        // given
        Framework framework = new Framework("Spring Boot");
        underTest.save(framework);

        // when
        boolean exists = underTest.existsByName("");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByName_returnsFalse_whenNameIsNull() {
        // given
        Framework framework = new Framework("Spring Boot");
        underTest.save(framework);

        // when
        boolean exists = underTest.existsByName(null);

        // then
        assertThat(exists).isFalse();
    }
}