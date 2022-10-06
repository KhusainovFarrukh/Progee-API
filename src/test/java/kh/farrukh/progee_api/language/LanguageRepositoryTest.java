package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class LanguageRepositoryTest {

    @Autowired
    private LanguageRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void existsByName_returnsTrue_whenLanguageExistsByName() {
        // given
        String name = "Java";
        underTest.save(new Language(name));

        // when
        boolean actual = underTest.existsByName(name);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void existsByName_returnsFalse_whenLanguageDoesNotExistByName() {
        // given
        String name = "Java";
        underTest.save(new Language("Kotlin"));

        // when
        boolean actual = underTest.existsByName(name);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByName_returnsFalse_whenNameIsEmpty() {
        // given
        underTest.save(new Language("Java"));

        // when
        boolean actual = underTest.existsByName("");

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void existsByName_returnsFalse_whenNameIsNull() {
        // given
        underTest.save(new Language("Java"));

        // when
        boolean actual = underTest.existsByName(null);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void findByState_returnsValidData_whenContainsOnlyOneStateLanguages() {
        // given
        ResourceState state = ResourceState.APPROVED;

        List<Language> languages = underTest.saveAll(List.of(
                new Language("Java", state),
                new Language("C#", state),
                new Language("C++", state)
        ));

        // when
        Page<Language> actual = underTest.findByState(state, PageRequest.of(0, languages.size()));

        // then
        assertThat(actual.getContent().size()).isEqualTo(languages.size());
        assertThat(actual.stream().allMatch((language) -> language.getState() == state)).isTrue();
    }

    @Test
    void findByState_returnsValidData_whenContainsMultipleStateLanguages() {
        // given
        List<Language> approvedLanguages = underTest.saveAll(List.of(
                new Language("Java", ResourceState.APPROVED),
                new Language("C#", ResourceState.APPROVED),
                new Language("C++", ResourceState.APPROVED)
        ));

        List<Language> waitingLanguages = underTest.saveAll(List.of(
                new Language("Python", ResourceState.WAITING)
        ));

        List<Language> declinedLanguages = underTest.saveAll(List.of(
                new Language("Go", ResourceState.DECLINED),
                new Language("Rust", ResourceState.DECLINED)
        ));

        // when
        Page<Language> actual = underTest.findByState(
                ResourceState.APPROVED,
                PageRequest.of(
                        0,
                        approvedLanguages.size() + waitingLanguages.size() + declinedLanguages.size()
                )
        );

        // then
        assertThat(actual.getContent().size()).isEqualTo(approvedLanguages.size());
        assertThat(actual.stream().allMatch((language) -> language.getState() == ResourceState.APPROVED)).isTrue();
    }

    @Test
    void findByState_returnsEmptyData_whenContainsOnlyOtherStateLanguages() {
        // given
        List<Language> waitingLanguages = underTest.saveAll(List.of(
                new Language("Python", ResourceState.WAITING)
        ));
        List<Language> declinedLanguages = underTest.saveAll(List.of(
                new Language("Go", ResourceState.DECLINED),
                new Language("Rust", ResourceState.DECLINED)
        ));

        // when
        Page<Language> actual = underTest.findByState(
                ResourceState.APPROVED,
                PageRequest.of(
                        0,
                        waitingLanguages.size() + declinedLanguages.size()
                )
        );

        // then
        assertThat(actual.getContent().size()).isEqualTo(0);
    }

    @Test
    void findByState_returnsEmptyData_whenDoesNotContainAnyLanguage() {
        // when
        Page<Language> pagedData = underTest.findByState(
                ResourceState.APPROVED,
                PageRequest.of(
                        0,
                        10
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
    }
}