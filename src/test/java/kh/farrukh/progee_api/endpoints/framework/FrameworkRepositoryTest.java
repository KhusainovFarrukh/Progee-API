package kh.farrukh.progee_api.endpoints.framework;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class FrameworkRepositoryTest {

    @Autowired
    private FrameworkRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void returnsTrueIfFrameworkExistsByName() {
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
    void returnsFalseIfFrameworkDoesNotExistByName() {
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
    void returnsFalseIfNameIsEmpty() {
        // given
        Framework framework = new Framework("Spring Boot");
        underTest.save(framework);

        // when
        boolean exists = underTest.existsByName("");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void returnsFalseIfNameIsNull() {
        // given
        Framework framework = new Framework("Spring Boot");
        underTest.save(framework);

        // when
        boolean exists = underTest.existsByName(null);

        // then
        assertThat(exists).isFalse();
    }
}