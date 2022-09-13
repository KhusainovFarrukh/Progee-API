package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.framework.payloads.FrameworkResponseDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FrameworkMappersTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private ImageRepository imageRepository;

    @Test
    void returnsNullIfFrameworkIsNull() {
        // given
        Framework framework = null;

        // when
        FrameworkResponseDTO frameworkResponseDTO = FrameworkMappers.toFrameworkResponseDTO(framework);

        // then
        assertThat(frameworkResponseDTO).isNull();
    }

    @Test
    void canMapFrameworkToFrameworkResponseDTO() {
        // given
        Framework framework = new Framework(
                1,
                ResourceState.APPROVED,
                "Test",
                "Test",
                new Image(1),
                new AppUser(3),
                ZonedDateTime.now(),
                new Language(5)
        );

        // when
        FrameworkResponseDTO frameworkResponseDTO = FrameworkMappers.toFrameworkResponseDTO(framework);

        // then
        assertThat(frameworkResponseDTO).isNotNull();
        assertThat(frameworkResponseDTO.getId()).isEqualTo(framework.getId());
        assertThat(frameworkResponseDTO.getState()).isEqualTo(framework.getState());
        assertThat(frameworkResponseDTO.getName()).isEqualTo(framework.getName());
        assertThat(frameworkResponseDTO.getDescription()).isEqualTo(framework.getDescription());
        assertThat(frameworkResponseDTO.getCreatedAt()).isEqualTo(framework.getCreatedAt());
        assertThat(frameworkResponseDTO.getLanguage().getId()).isEqualTo(framework.getLanguage().getId());
        assertThat(frameworkResponseDTO.getImage().getId()).isEqualTo(framework.getImage().getId());
        assertThat(frameworkResponseDTO.getAuthor().getId()).isEqualTo(framework.getAuthor().getId());
    }

    @Test
    void returnsNullIfFrameworkRequestDTOIsNull() {
        // given
        FrameworkRequestDTO frameworkRequestDTO = null;

        // when
        Framework framework = FrameworkMappers.toFramework(frameworkRequestDTO, languageRepository, imageRepository);

        // then
        assertThat(framework).isNull();
    }

    @Test
    void canMapFrameworkRequestDTOtoFramework() {
        // given
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language(1)));
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image(1)));
        FrameworkRequestDTO frameworkRequestDTO = new FrameworkRequestDTO(
                "Test",
                "Test",
                1L,
                1L
        );

        // when
        Framework framework = FrameworkMappers.toFramework(frameworkRequestDTO, languageRepository, imageRepository);

        // then
        assertThat(framework).isNotNull();
        assertThat(framework.getId()).isEqualTo(0L);
        assertThat(framework.getName()).isEqualTo(frameworkRequestDTO.getName());
        assertThat(framework.getDescription()).isEqualTo(frameworkRequestDTO.getDescription());
        assertThat(framework.getLanguage().getId()).isEqualTo(frameworkRequestDTO.getLanguageId());
        assertThat(framework.getImage().getId()).isEqualTo(frameworkRequestDTO.getImageId());
    }

    @Test
    void throwsExceptionIfLanguageDoesNotExist() {
        // given
        when(languageRepository.findById(any())).thenReturn(Optional.empty());
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image(1)));
        FrameworkRequestDTO frameworkRequestDTO = new FrameworkRequestDTO(
                "Test",
                "Test",
                1L,
                1L
        );

        // when
        // then
        assertThatThrownBy(() -> FrameworkMappers.toFramework(frameworkRequestDTO, languageRepository, imageRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(frameworkRequestDTO.getLanguageId()));
    }

    @Test
    void throwsExceptionIfImageDoesNotExist() {
        // given
        when(imageRepository.findById(any())).thenReturn(Optional.empty());
        FrameworkRequestDTO frameworkRequestDTO = new FrameworkRequestDTO(
                "Test",
                "Test",
                1L,
                1L
        );

        // when
        // then
        assertThatThrownBy(() -> FrameworkMappers.toFramework(frameworkRequestDTO, languageRepository, imageRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining(String.valueOf(frameworkRequestDTO.getImageId()));
    }
}