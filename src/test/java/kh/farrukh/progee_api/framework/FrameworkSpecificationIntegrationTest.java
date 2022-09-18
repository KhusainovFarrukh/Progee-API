package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class FrameworkSpecificationIntegrationTest {

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private FrameworkRepository frameworkRepository;

    @AfterEach
    void tearDown() {
        languageRepository.deleteAll();
        frameworkRepository.deleteAll();
    }

    @Test
    void returnValidDataIfLanguageIdIsNullAndStataIsNull() {
        // given
        List<Language> languages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        languages = languageRepository.saveAll(languages);
        List<Framework> frameworks = List.of(
                new Framework("Spring", ResourceState.APPROVED, languages.get(0)),
                new Framework("Django", ResourceState.APPROVED, languages.get(1)),
                new Framework("React", ResourceState.APPROVED, languages.get(2)),
                new Framework("Angular", ResourceState.WAITING, languages.get(2)),
                new Framework("Vue", ResourceState.DECLINED, languages.get(2))
        );
        frameworks = frameworkRepository.saveAll(frameworks);
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(null, null);

        // when
        List<Framework> actual = frameworkRepository.findAll(frameworkSpecification);

        // then
        assertThat(actual.size()).isEqualTo(frameworks.size());
    }

    @Test
    void returnsValidDataIfLanguageIdIsNotNullAndStateIsNull() {
        // given
        List<Language> languages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        languages = languageRepository.saveAll(languages);
        List<Framework> frameworks = List.of(
                new Framework("Spring", ResourceState.APPROVED, languages.get(0)),
                new Framework("Django", ResourceState.APPROVED, languages.get(1)),
                new Framework("React", ResourceState.APPROVED, languages.get(2)),
                new Framework("Angular", ResourceState.WAITING, languages.get(2)),
                new Framework("Vue", ResourceState.DECLINED, languages.get(2))
        );
        frameworks = frameworkRepository.saveAll(frameworks);
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(languages.get(2).getId(), null);

        // when
        List<Framework> actual = frameworkRepository.findAll(frameworkSpecification);

        // then
        assertThat(actual.size()).isEqualTo(3);
    }

    @Test
    void returnsValidDataIfLanguageIdIsNullAndStateIsNotNull() {
        // given
        List<Language> languages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        languages = languageRepository.saveAll(languages);
        List<Framework> frameworks = List.of(
                new Framework("Spring", ResourceState.APPROVED, languages.get(0)),
                new Framework("Django", ResourceState.APPROVED, languages.get(1)),
                new Framework("React", ResourceState.APPROVED, languages.get(2)),
                new Framework("Angular", ResourceState.WAITING, languages.get(2)),
                new Framework("Vue", ResourceState.DECLINED, languages.get(2))
        );
        frameworks = frameworkRepository.saveAll(frameworks);
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(null, ResourceState.APPROVED);

        // when
        List<Framework> actual = frameworkRepository.findAll(frameworkSpecification);

        // then
        assertThat(actual.size()).isEqualTo(3);
    }

    @Test
    void returnsValidDataIfLanguageIdIsNotNullAndStateIsNotNull() {
        // given
        List<Language> languages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        languages = languageRepository.saveAll(languages);
        List<Framework> frameworks = List.of(
                new Framework("Spring", ResourceState.APPROVED, languages.get(0)),
                new Framework("Django", ResourceState.APPROVED, languages.get(1)),
                new Framework("React", ResourceState.APPROVED, languages.get(2)),
                new Framework("Angular", ResourceState.WAITING, languages.get(2)),
                new Framework("Vue", ResourceState.DECLINED, languages.get(2))
        );
        frameworks = frameworkRepository.saveAll(frameworks);
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(languages.get(2).getId(), ResourceState.APPROVED);

        // when
        List<Framework> actual = frameworkRepository.findAll(frameworkSpecification);

        // then
        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    void returnsTrueIfBothLanguageIdAndStateAreNull() {
        // given
        FrameworkSpecification frameworkSpecification1 = new FrameworkSpecification(null, null);
        FrameworkSpecification frameworkSpecification2 = new FrameworkSpecification(null, null);

        // when
        boolean actual = frameworkSpecification1.equals(frameworkSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void returnsTrueIfBothLanguageIdAndStateAreEqual() {
        // given
        FrameworkSpecification frameworkSpecification1 = new FrameworkSpecification(1L, ResourceState.APPROVED);
        FrameworkSpecification frameworkSpecification2 = new FrameworkSpecification(1L, ResourceState.APPROVED);

        // when
        boolean actual = frameworkSpecification1.equals(frameworkSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void returnsFalseIfLanguageIdIsNotEqual() {
        // given
        FrameworkSpecification frameworkSpecification1 = new FrameworkSpecification(1L, ResourceState.APPROVED);
        FrameworkSpecification frameworkSpecification2 = new FrameworkSpecification(2L, ResourceState.APPROVED);

        // when
        boolean actual = frameworkSpecification1.equals(frameworkSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void returnsFalseIfStateIsNotEqual() {
        // given
        FrameworkSpecification frameworkSpecification1 = new FrameworkSpecification(1L, ResourceState.APPROVED);
        FrameworkSpecification frameworkSpecification2 = new FrameworkSpecification(1L, ResourceState.WAITING);

        // when
        boolean actual = frameworkSpecification1.equals(frameworkSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void returnsTrueIfSameObject() {
        // given
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(1L, ResourceState.APPROVED);

        // when
        boolean actual = frameworkSpecification.equals(frameworkSpecification);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void returnsFalseIfObjectIsNull() {
        // given
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(1L, ResourceState.APPROVED);

        // when
        boolean actual = frameworkSpecification.equals(null);

        // then
        assertThat(actual).isFalse();
    }
}