package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
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
class FrameworkRepositoryTest {

    @Autowired
    private FrameworkRepository underTest;
    @Autowired
    private LanguageRepository languageRepository;

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

    @Test
    void returnValidDataIfContainsFrameworksOfOnlyQueriedLanguage() {
        // given
        Language language = languageRepository.save(new Language());

        Framework framework1 = new Framework(language);
        Framework framework2 = new Framework(language);
        Framework framework3 = new Framework(language);

        List<Framework> frameworks = List.of(framework1, framework2, framework3);
        underTest.saveAll(frameworks);

        // when
        Page<Framework> pagedData = underTest.findByLanguage_Id(language.getId(), PageRequest.of(0, frameworks.size()));

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(frameworks.size());
        assertThat(pagedData.map(
                (framework) -> framework.getLanguage().getId() == language.getId()
        ).getContent().size()).isEqualTo(frameworks.size());
    }

    @Test
    void returnValidDataIfContainsFrameworksOfMultipleLanguages() {
        // given
        Language language1 = languageRepository.save(new Language());
        Language language2 = languageRepository.save(new Language());

        Framework framework1 = new Framework(language1);
        Framework framework2 = new Framework(language1);
        Framework framework3 = new Framework(language2);

        List<Framework> language1Frameworks = List.of(framework1, framework2);
        List<Framework> language2Frameworks = List.of(framework3);
        underTest.saveAll(language1Frameworks);
        underTest.saveAll(language2Frameworks);

        // when
        Page<Framework> pagedData = underTest.findByLanguage_Id(
                language1.getId(),
                PageRequest.of(
                        0,
                        language1Frameworks.size() + language2Frameworks.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(language1Frameworks.size());
        assertThat(pagedData.map(
                (framework) -> framework.getLanguage().getId() == language1.getId()
        ).getContent().size()).isEqualTo(language1Frameworks.size());
    }

    @Test
    void returnEmptyDataIfContainsFrameworksOfOnlyOtherLanguages() {
        // given
        Language language1 = languageRepository.save(new Language());
        Language language2 = languageRepository.save(new Language());

        Framework framework1 = new Framework(language1);
        Framework framework2 = new Framework(language1);
        Framework framework3 = new Framework(language1);

        List<Framework> language1Frameworks = List.of(framework1, framework2, framework3);
        underTest.saveAll(language1Frameworks);

        // when
        Page<Framework> pagedData = underTest.findByLanguage_Id(
                language2.getId(),
                PageRequest.of(
                        0,
                        language1Frameworks.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (framework) -> framework.getLanguage().getId() == language2.getId()
        ).getContent().size()).isEqualTo(0);
    }

    @Test
    void returnEmptyDataIfDoesNotContainAnyFramework() {
        // given
        Language language = languageRepository.save(new Language());

        // when
        Page<Framework> pagedData = underTest.findByLanguage_Id(
                language.getId(),
                PageRequest.of(0, 10)
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (framework) -> framework.getLanguage().getId() == language.getId()
        ).getContent().size()).isEqualTo(0);
    }

    @Test
    void returnsValidDataIfContainsOnlyOneStateFrameworks() {
        // given
        Language language = languageRepository.save(new Language());
        ResourceState state = ResourceState.APPROVED;

        Framework framework1 = new Framework(language);
        framework1.setState(state);
        Framework framework2 = new Framework(language);
        framework2.setState(state);
        Framework framework3 = new Framework(language);
        framework3.setState(state);

        List<Framework> frameworks = List.of(framework1, framework2, framework3);
        underTest.saveAll(frameworks);

        // when
        Page<Framework> pagedData = underTest.findByStateAndLanguage_Id(
                state,
                language.getId(),
                PageRequest.of(0, frameworks.size())
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(frameworks.size());
        assertThat(pagedData.map(
                (framework) -> framework.getState() == state
        ).getContent().size()).isEqualTo(frameworks.size());
    }

    @Test
    void returnsValidDataIfContainsMultipleStateFrameworks() {
        // given
        Language language = languageRepository.save(new Language());

        ResourceState state1 = ResourceState.APPROVED;
        ResourceState state2 = ResourceState.WAITING;
        ResourceState state3 = ResourceState.DECLINED;

        Framework framework1 = new Framework(language);
        framework1.setState(state1);
        Framework framework2 = new Framework(language);
        framework2.setState(state1);
        Framework framework3 = new Framework(language);
        framework3.setState(state2);
        Framework framework4 = new Framework(language);
        framework3.setState(state2);
        Framework framework5 = new Framework(language);
        framework3.setState(state3);

        List<Framework> state1Frameworks = List.of(framework1, framework2);
        List<Framework> state2Frameworks = List.of(framework3, framework4);
        List<Framework> state3Frameworks = List.of(framework5);
        underTest.saveAll(state1Frameworks);
        underTest.saveAll(state2Frameworks);
        underTest.saveAll(state3Frameworks);

        // when
        Page<Framework> pagedData = underTest.findByStateAndLanguage_Id(
                state1,
                language.getId(),
                PageRequest.of(
                        0,
                        state1Frameworks.size() + state2Frameworks.size() + state3Frameworks.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(state1Frameworks.size());
        assertThat(pagedData.map(
                (framework) -> framework.getState() == state1
        ).getContent().size()).isEqualTo(state1Frameworks.size());
    }

    @Test
    void returnsEmptyDataIfContainsOnlyOtherStateFrameworks() {
        // given
        Language language = languageRepository.save(new Language());

        ResourceState state1 = ResourceState.APPROVED;
        ResourceState state2 = ResourceState.WAITING;
        ResourceState state3 = ResourceState.DECLINED;

        Framework framework1 = new Framework(language);
        framework1.setState(state1);
        Framework framework2 = new Framework(language);
        framework2.setState(state1);
        Framework framework3 = new Framework(language);
        framework3.setState(state2);
        Framework framework4 = new Framework(language);
        framework3.setState(state2);
        Framework framework5 = new Framework(language);
        framework3.setState(state2);

        List<Framework> state1Frameworks = List.of(framework1, framework2);
        List<Framework> state2Frameworks = List.of(framework3, framework4, framework5);
        underTest.saveAll(state1Frameworks);
        underTest.saveAll(state2Frameworks);

        // when
        Page<Framework> pagedData = underTest.findByStateAndLanguage_Id(
                state3,
                language.getId(),
                PageRequest.of(
                        0,
                        state1Frameworks.size() + state2Frameworks.size()
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (framework) -> framework.getState() == state3
        ).getContent().size()).isEqualTo(0);
    }

    @Test
    void returnsEmptyDataIfDoesNotContainAnyFramework() {
        // given
        ResourceState state = ResourceState.APPROVED;

        // when
        Page<Framework> pagedData = underTest.findByStateAndLanguage_Id(
                state,
                1,
                PageRequest.of(
                        0,
                        10
                )
        );

        // then
        assertThat(pagedData.getContent().size()).isEqualTo(0);
        assertThat(pagedData.map(
                (framework) -> framework.getState() == state
        ).getContent().size()).isEqualTo(0);
    }
}