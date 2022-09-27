package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class LanguageRepositoryTest {

    @Autowired
    private LanguageRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void returnsTrueIfLanguageExistsByName() {
        // given
        String name = "Java";
        Language language = new Language(name);
        underTest.save(language);

        // when
        boolean exists = underTest.existsByName(name);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void returnsFalseIfLanguageDoesNotExistByName() {
        // given
        String name = "Java";
        Language language = new Language("Kotlin");
        underTest.save(language);

        // when
        boolean exists = underTest.existsByName(name);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void returnsFalseIfNameIsEmpty() {
        // given
        Language language = new Language("Java");
        underTest.save(language);

        // when
        boolean exists = underTest.existsByName("");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void returnsFalseIfNameIsNull() {
        // given
        Language language = new Language("Java");
        underTest.save(language);

        // when
        boolean exists = underTest.existsByName("");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void returnsValidDataIfContainsOnlyOneStateLanguages() {
        // given
        ResourceState state = ResourceState.APPROVED;

        Language language1 = new Language();
        language1.setState(state);
        Language language2 = new Language();
        language2.setState(state);
        Language language3 = new Language();
        language3.setState(state);

        List<Language> languages = List.of(language1, language2, language3);
        underTest.saveAll(languages);

        // when
        Page<Language> pagedData = underTest.findByState(state, PageRequest.of(0, languages.size()));

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(languages.size());
        assertThat(pagedData.map(
                (language) -> language.getState() == state
        ).getContent().size()).isEqualTo(languages.size());
    }

    @Test
    void returnsValidDataIfContainsMultipleStateLanguages() {
        // given
        ResourceState state1 = ResourceState.APPROVED;
        ResourceState state2 = ResourceState.WAITING;
        ResourceState state3 = ResourceState.DECLINED;

        Language language1 = new Language();
        language1.setState(state1);
        Language language2 = new Language();
        language2.setState(state1);
        Language language3 = new Language();
        language3.setState(state2);
        Language language4 = new Language();
        language3.setState(state2);
        Language language5 = new Language();
        language3.setState(state3);

        List<Language> state1Languages = List.of(language1, language2);
        List<Language> state2Languages = List.of(language3, language4);
        List<Language> state3Languages = List.of(language5);
        underTest.saveAll(state1Languages);
        underTest.saveAll(state2Languages);
        underTest.saveAll(state3Languages);

        // when
        Page<Language> pagedData = underTest.findByState(
                state1,
                PageRequest.of(
                        0,
                        state1Languages.size() + state2Languages.size() + state3Languages.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(state1Languages.size());
        assertThat(pagedData.map(
                (language) -> language.getState() == state1
        ).getContent().size()).isEqualTo(state1Languages.size());
    }

    @Test
    void returnsEmptyDataIfContainsOnlyOtherStateLanguages() {
        // given
        ResourceState state1 = ResourceState.APPROVED;
        ResourceState state2 = ResourceState.WAITING;
        ResourceState state3 = ResourceState.DECLINED;

        Language language1 = new Language();
        language1.setState(state1);
        Language language2 = new Language();
        language2.setState(state1);
        Language language3 = new Language();
        language3.setState(state2);
        Language language4 = new Language();
        language3.setState(state2);
        Language language5 = new Language();
        language3.setState(state2);

        List<Language> state1Languages = List.of(language1, language2);
        List<Language> state2Languages = List.of(language3, language4, language5);
        underTest.saveAll(state1Languages);
        underTest.saveAll(state2Languages);

        // when
        Page<Language> pagedData = underTest.findByState(
                state3,
                PageRequest.of(
                        0,
                        state1Languages.size() + state2Languages.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (language) -> language.getState() == state3
        ).getContent().size()).isEqualTo(0);
    }

    @Test
    void returnsEmptyDataIfDoesNotContainAnyLanguage() {
        // given
        ResourceState state = ResourceState.APPROVED;

        // when
        Page<Language> pagedData = underTest.findByState(
                state,
                PageRequest.of(
                        0,
                        10
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (language) -> language.getState() == state
        ).getContent().size()).isEqualTo(0);
    }
}