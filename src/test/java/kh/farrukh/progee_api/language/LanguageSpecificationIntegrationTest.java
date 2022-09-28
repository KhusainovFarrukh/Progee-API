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
    void returnsValidDataIfStateIsNull() {
        // given
        List<Language> languages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        languages = languageRepository.saveAll(languages);
        LanguageSpecification languageSpecification = new LanguageSpecification(null);

        // when
        List<Language> actual = languageRepository.findAll(languageSpecification);

        // then
        assertThat(actual.size()).isEqualTo(languages.size());
    }

    @Test
    void returnsValidDataIfStateIsNotNull() {
        // given
        List<Language> approvedLanguages = List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("Python", ResourceState.APPROVED),
                new Language("JavaScript", ResourceState.APPROVED)
        );
        List<Language> waitingLanguages = List.of(
                new Language("C#", ResourceState.WAITING),
                new Language("C++", ResourceState.WAITING)
        );
        approvedLanguages = languageRepository.saveAll(approvedLanguages);
        waitingLanguages = languageRepository.saveAll(waitingLanguages);
        LanguageSpecification languageSpecification = new LanguageSpecification(ResourceState.APPROVED);

        // when
        List<Language> actual = languageRepository.findAll(languageSpecification);

        // then
        assertThat(actual.size()).isEqualTo(approvedLanguages.size());
    }

    @Test
    void returnsTrueIfStateIsNull() {
        // given
        LanguageSpecification languageSpecification1 = new LanguageSpecification(null);
        LanguageSpecification languageSpecification2 = new LanguageSpecification(null);

        // when
        boolean actual = languageSpecification1.equals(languageSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void returnsTrueIfStateIsEqual() {
        // given
        LanguageSpecification languageSpecification1 = new LanguageSpecification(ResourceState.APPROVED);
        LanguageSpecification languageSpecification2 = new LanguageSpecification(ResourceState.APPROVED);

        // when
        boolean actual = languageSpecification1.equals(languageSpecification2);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void returnsFalseIfStateIsNotEqual() {
        // given
        LanguageSpecification languageSpecification1 = new LanguageSpecification(ResourceState.APPROVED);
        LanguageSpecification languageSpecification2 = new LanguageSpecification(ResourceState.WAITING);

        // when
        boolean actual = languageSpecification1.equals(languageSpecification2);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void returnsTrueIfSameObject() {
        // given
        LanguageSpecification languageSpecification = new LanguageSpecification(ResourceState.APPROVED);

        // when
        boolean actual = languageSpecification.equals(languageSpecification);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void returnsFalseIfObjectIsNull() {
        // given
        LanguageSpecification languageSpecification = new LanguageSpecification(ResourceState.APPROVED);

        // when
        boolean actual = languageSpecification.equals(null);

        // then
        assertThat(actual).isFalse();
    }

}