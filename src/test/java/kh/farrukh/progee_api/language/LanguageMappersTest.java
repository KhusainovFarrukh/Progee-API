package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.payloads.LanguageRequestDTO;
import kh.farrukh.progee_api.language.payloads.LanguageResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageMappersTest {

    @Mock
    private ImageRepository imageRepository;

    @Test
    void returnsNullIfLanguageIsNull() {
        // given
        Language language = null;

        // when
        LanguageResponseDTO languageResponseDTO = LanguageMappers.toLanguageResponseDTO(language);

        // then
        assertThat(languageResponseDTO).isNull();
    }

    @Test
    void canMapLanguageToLanguageResponseDTO() {
        // given
        Language language = new Language(
                1,
                "Test",
                "test",
                new Image(1),
                Collections.emptyList(),
                Collections.emptyList(),
                new AppUser(1),
                ZonedDateTime.now()
        );

        // when
        LanguageResponseDTO languageResponseDTO = LanguageMappers.toLanguageResponseDTO(language);

        // then
        assertThat(languageResponseDTO).isNotNull();
        assertThat(languageResponseDTO.getId()).isEqualTo(language.getId());
        assertThat(languageResponseDTO.getName()).isEqualTo(language.getName());
        assertThat(languageResponseDTO.getDescription()).isEqualTo(language.getDescription());
        assertThat(languageResponseDTO.getImage().getId()).isEqualTo(language.getImage().getId());
        assertThat(languageResponseDTO.getAuthor().getId()).isEqualTo(language.getAuthor().getId());
        assertThat(languageResponseDTO.getCreatedAt()).isEqualTo(language.getCreatedAt());
    }

    @Test
    void returnsNullIfLanguageRequestDTOIsNull() {
        // given
        LanguageRequestDTO languageRequestDTO = null;

        // when
        Language language = LanguageMappers.toLanguage(languageRequestDTO, null);

        // then
        assertThat(language).isNull();
    }

    @Test
    void canMapLanguageRequestDTOToLanguage() {
        // given
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image(1)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(
                "Test",
                "test",
                1
        );

        // when
        Language language = LanguageMappers.toLanguage(languageRequestDTO, imageRepository);

        // then
        assertThat(language).isNotNull();
        assertThat(language.getName()).isEqualTo(languageRequestDTO.getName());
        assertThat(language.getDescription()).isEqualTo(languageRequestDTO.getDescription());
        assertThat(language.getImage().getId()).isEqualTo(languageRequestDTO.getImageId());
    }

    @Test
    void throwsExceptionIfImageDoesNotExist() {
        // given
        when(imageRepository.findById(any())).thenReturn(Optional.empty());
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(
                "Test",
                "test",
                1
        );

        // when
        // then
        assertThatThrownBy(() -> LanguageMappers.toLanguage(languageRequestDTO, imageRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image")
                .hasMessageContaining(String.valueOf(languageRequestDTO.getImageId()));
    }
}