package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class FrameworkSpecificationTest {

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
    void repository_returnsValidData_whenLanguageIdIsNullAndStateIsNull() {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        List<Framework> frameworks = frameworkRepository.saveAll(List.of(
                new Framework("Spring", ResourceState.APPROVED, languages.get(0)),
                new Framework("Django", ResourceState.APPROVED, languages.get(1)),
                new Framework("React", ResourceState.APPROVED, languages.get(2)),
                new Framework("Angular", ResourceState.WAITING, languages.get(2)),
                new Framework("Vue", ResourceState.DECLINED, languages.get(2))
        ));
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(null, null);

        // when
        List<Framework> actual = frameworkRepository.findAll(frameworkSpecification);

        // then
        assertThat(actual.size()).isEqualTo(frameworks.size());
    }

    @Test
    void repository_returnsValidData_whenLanguageIdIsNotNullAndStateIsNull() {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        frameworkRepository.saveAll(List.of(
                new Framework("Spring", ResourceState.APPROVED, languages.get(0)),
                new Framework("Hibernate", ResourceState.APPROVED, languages.get(0))
        ));
        frameworkRepository.saveAll(List.of(
                new Framework("Django", ResourceState.APPROVED, languages.get(1)),
                new Framework("Flask", ResourceState.APPROVED, languages.get(1))
        ));
        List<Framework> language2Frameworks = frameworkRepository.saveAll(List.of(
                new Framework("React", ResourceState.APPROVED, languages.get(2)),
                new Framework("Angular", ResourceState.WAITING, languages.get(2)),
                new Framework("Vue", ResourceState.DECLINED, languages.get(2))
        ));
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(languages.get(2).getId(), null);

        // when
        List<Framework> actual = frameworkRepository.findAll(frameworkSpecification);

        // then
        assertThat(actual.size()).isEqualTo(language2Frameworks.size());
    }

    @Test
    void repository_returnsValidData_whenLanguageIdIsNullAndStateIsNotNull() {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        List<Framework> approvedFrameworks = frameworkRepository.saveAll(List.of(
                new Framework("Spring", ResourceState.APPROVED, languages.get(0)),
                new Framework("Django", ResourceState.APPROVED, languages.get(1)),
                new Framework("React", ResourceState.APPROVED, languages.get(2))

        ));
        frameworkRepository.saveAll(List.of(
                new Framework("Angular", ResourceState.WAITING, languages.get(2))
        ));
        frameworkRepository.saveAll(List.of(
                new Framework("Vue", ResourceState.DECLINED, languages.get(2))
        ));
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(null, ResourceState.APPROVED);

        // when
        List<Framework> actual = frameworkRepository.findAll(frameworkSpecification);

        // then
        assertThat(actual.size()).isEqualTo(approvedFrameworks.size());
    }

    @Test
    void repository_returnsValidData_whenLanguageIdIsNotNullAndStateIsNotNull() {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        frameworkRepository.saveAll(List.of(
                new Framework("Django", ResourceState.APPROVED, languages.get(1)),
                new Framework("Flask", ResourceState.APPROVED, languages.get(1))
        ));
        List<Framework> language2ApprovedFrameworks = frameworkRepository.saveAll(List.of(
                new Framework("React", ResourceState.APPROVED, languages.get(2))
        ));
        frameworkRepository.saveAll(List.of(
                new Framework("Vue", ResourceState.WAITING, languages.get(2)),
                new Framework("Svelte", ResourceState.WAITING, languages.get(2))
        ));

        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(languages.get(2).getId(), ResourceState.APPROVED);

        // when
        List<Framework> actual = frameworkRepository.findAll(frameworkSpecification);

        // then
        assertThat(actual.size()).isEqualTo(language2ApprovedFrameworks.size());
    }

    @Test
    void equals_returnsTrue_whenBothLanguageIdAndStateAreNull() {
        // given
        FrameworkSpecification frameworkSpecification1 = new FrameworkSpecification(null, null);
        FrameworkSpecification frameworkSpecification2 = new FrameworkSpecification(null, null);

        // when
        boolean actual = frameworkSpecification1.equals(frameworkSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void equals_returnsTrue_whenBothLanguageIdAndStateAreEqual() {
        // given
        FrameworkSpecification frameworkSpecification1 = new FrameworkSpecification(1L, ResourceState.APPROVED);
        FrameworkSpecification frameworkSpecification2 = new FrameworkSpecification(1L, ResourceState.APPROVED);

        // when
        boolean actual = frameworkSpecification1.equals(frameworkSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void equals_returnsFalse_whenLanguageIdIsNotEqual() {
        // given
        FrameworkSpecification frameworkSpecification1 = new FrameworkSpecification(1L, ResourceState.APPROVED);
        FrameworkSpecification frameworkSpecification2 = new FrameworkSpecification(2L, ResourceState.APPROVED);

        // when
        boolean actual = frameworkSpecification1.equals(frameworkSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void equals_returnsFalse_whenStateIsNotEqual() {
        // given
        FrameworkSpecification frameworkSpecification1 = new FrameworkSpecification(1L, ResourceState.APPROVED);
        FrameworkSpecification frameworkSpecification2 = new FrameworkSpecification(1L, ResourceState.WAITING);

        // when
        boolean actual = frameworkSpecification1.equals(frameworkSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void equals_returnsTrue_whenSameObject() {
        // given
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(1L, ResourceState.APPROVED);

        // when
        boolean actual = frameworkSpecification.equals(frameworkSpecification);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void equals_returnsFalse_whenObjectIsNull() {
        // given
        FrameworkSpecification frameworkSpecification = new FrameworkSpecification(1L, ResourceState.APPROVED);

        // when
        boolean actual = frameworkSpecification.equals(null);

        // then
        assertThat(actual).isFalse();
    }
}
