package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class LanguageSpecificationIntegrationTest {

    @Autowired
    private LanguageRepository languageRepository;

    @AfterEach
    void tearDown() {
        languageRepository.deleteAll();
    }

    @Test
    void repository_returnsValidData_whenStateIsNull() {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        LanguageSpecification languageSpecification = new LanguageSpecification(null);

        // when
        List<Language> actual = languageRepository.findAll(languageSpecification);

        // then
        assertThat(actual.size()).isEqualTo(languages.size());
    }

    @Test
    void repository_returnsValidData_whenStateIsNotNull() {
        // given
        List<Language> approvedLanguages = languageRepository.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        ));
        languageRepository.saveAll(List.of(
                new Language("C#", ResourceState.WAITING),
                new Language("C++", ResourceState.WAITING)
        ));
        LanguageSpecification languageSpecification = new LanguageSpecification(ResourceState.APPROVED);

        // when
        List<Language> actual = languageRepository.findAll(languageSpecification);

        // then
        assertThat(actual.size()).isEqualTo(approvedLanguages.size());
        List<Long> expectedIds = approvedLanguages.stream().map(Language::getId).toList();
        assertThat(actual.stream().allMatch(language -> expectedIds.contains(language.getId()))).isTrue();
    }

    @Test
    void equals_returnsTrue_whenStateIsNull() {
        // given
        LanguageSpecification languageSpecification1 = new LanguageSpecification(null);
        LanguageSpecification languageSpecification2 = new LanguageSpecification(null);

        // when
        boolean actual = languageSpecification1.equals(languageSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void equals_returnsTrue_whenStateIsEqual() {
        // given
        LanguageSpecification languageSpecification1 = new LanguageSpecification(ResourceState.APPROVED);
        LanguageSpecification languageSpecification2 = new LanguageSpecification(ResourceState.APPROVED);

        // when
        boolean actual = languageSpecification1.equals(languageSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void equals_returnsFalse_whenStateIsNotEqual() {
        // given
        LanguageSpecification languageSpecification1 = new LanguageSpecification(ResourceState.APPROVED);
        LanguageSpecification languageSpecification2 = new LanguageSpecification(ResourceState.WAITING);

        // when
        boolean actual = languageSpecification1.equals(languageSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void equals_returnsTrue_whenSameObject() {
        // given
        LanguageSpecification languageSpecification1 = new LanguageSpecification(ResourceState.APPROVED);
        LanguageSpecification languageSpecification2 = languageSpecification1;

        // when
        boolean actual = languageSpecification1.equals(languageSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void equals_returnsFalse_whenObjectIsNull() {
        // given
        LanguageSpecification languageSpecification = new LanguageSpecification(ResourceState.APPROVED);

        // when
        boolean actual = languageSpecification.equals(null);

        // then
        assertThat(actual).isFalse();
    }
}