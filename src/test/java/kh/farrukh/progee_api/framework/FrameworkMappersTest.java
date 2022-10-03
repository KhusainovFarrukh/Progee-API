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

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FrameworkMappersTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private ImageRepository imageRepository;

    @Test
    void toFrameworkResponseDTO_returnsNull_whenFrameworkIsNull() {
        // given
        Framework framework = null;

        // when
        FrameworkResponseDTO actual = FrameworkMappers.toFrameworkResponseDTO(framework);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toFrameworkResponseDTO_canMap_whenFrameworkIsValid() {
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
        FrameworkResponseDTO actual = FrameworkMappers.toFrameworkResponseDTO(framework);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(framework.getId());
        assertThat(actual.getState()).isEqualTo(framework.getState());
        assertThat(actual.getName()).isEqualTo(framework.getName());
        assertThat(actual.getDescription()).isEqualTo(framework.getDescription());
        assertThat(actual.getCreatedAt()).isEqualTo(framework.getCreatedAt());
        assertThat(actual.getLanguage().getId()).isEqualTo(framework.getLanguage().getId());
        assertThat(actual.getImage().getId()).isEqualTo(framework.getImage().getId());
        assertThat(actual.getAuthor().getId()).isEqualTo(framework.getAuthor().getId());
    }

    @Test
    void toFramework_returnsNull_whenFrameworkRequestDTOIsNull() {
        // given
        FrameworkRequestDTO frameworkRequestDTO = null;

        // when
        Framework actual = FrameworkMappers.toFramework(frameworkRequestDTO, languageRepository, imageRepository);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toFramework_canMap_whenFrameworkRequestDTOIsValid() {
        // given
        FrameworkRequestDTO frameworkRequestDTO = new FrameworkRequestDTO(
                "Test",
                "Test",
                1L,
                1L
        );
        when(languageRepository.findById(frameworkRequestDTO.getLanguageId()))
                .thenReturn(Optional.of(new Language(frameworkRequestDTO.getLanguageId())));
        when(imageRepository.findById(frameworkRequestDTO.getImageId()))
                .thenReturn(Optional.of(new Image(frameworkRequestDTO.getImageId())));

        // when
        Framework actual = FrameworkMappers.toFramework(frameworkRequestDTO, languageRepository, imageRepository);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(0L);
        assertThat(actual.getName()).isEqualTo(frameworkRequestDTO.getName());
        assertThat(actual.getDescription()).isEqualTo(frameworkRequestDTO.getDescription());
        assertThat(actual.getLanguage().getId()).isEqualTo(frameworkRequestDTO.getLanguageId());
        assertThat(actual.getImage().getId()).isEqualTo(frameworkRequestDTO.getImageId());
    }

    @Test
    void toFramework_throwsException_whenLanguageDoesNotExist() {
        // given
        FrameworkRequestDTO frameworkRequestDTO = new FrameworkRequestDTO(
                "Test",
                "Test",
                1L,
                1L
        );
        when(languageRepository.findById(frameworkRequestDTO.getLanguageId()))
                .thenReturn(Optional.empty());
        when(imageRepository.findById(frameworkRequestDTO.getImageId()))
                .thenReturn(Optional.of(new Image(frameworkRequestDTO.getImageId())));

        // when
        // then
        assertThatThrownBy(() -> FrameworkMappers.toFramework(frameworkRequestDTO, languageRepository, imageRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(frameworkRequestDTO.getLanguageId()));
    }

    @Test
    void toFramework_throwsException_whenImageDoesNotExist() {
        // given
        FrameworkRequestDTO frameworkRequestDTO = new FrameworkRequestDTO(
                "Test",
                "Test",
                1L,
                1L
        );
        when(imageRepository.findById(frameworkRequestDTO.getImageId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> FrameworkMappers.toFramework(frameworkRequestDTO, languageRepository, imageRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining(String.valueOf(frameworkRequestDTO.getImageId()));
    }
}