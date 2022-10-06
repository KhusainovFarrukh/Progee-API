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
    void toLanguageResponseDTO_returnsNull_whenLanguageIsNull() {
        // given
        Language language = null;

        // when
        LanguageResponseDTO actual = LanguageMappers.toLanguageResponseDTO(language);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toLanguageResponseDTO_canMap_whenLanguageIsValid() {
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
        LanguageResponseDTO actual = LanguageMappers.toLanguageResponseDTO(language);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(language.getId());
        assertThat(actual.getName()).isEqualTo(language.getName());
        assertThat(actual.getDescription()).isEqualTo(language.getDescription());
        assertThat(actual.getImage().getId()).isEqualTo(language.getImage().getId());
        assertThat(actual.getAuthor().getId()).isEqualTo(language.getAuthor().getId());
        assertThat(actual.getCreatedAt()).isEqualTo(language.getCreatedAt());
    }

    @Test
    void toLanguage_returnsNull_whenLanguageRequestDTOIsNull() {
        // given
        LanguageRequestDTO languageRequestDTO = null;

        // when
        Language actual = LanguageMappers.toLanguage(languageRequestDTO, null);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toLanguage_canMap_whenLanguageRequestDTOIsValid() {
        // given
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image(1)));
        LanguageRequestDTO languageRequestDTO = new LanguageRequestDTO(
                "Test",
                "test",
                1
        );

        // when
        Language actual = LanguageMappers.toLanguage(languageRequestDTO, imageRepository);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(languageRequestDTO.getName());
        assertThat(actual.getDescription()).isEqualTo(languageRequestDTO.getDescription());
        assertThat(actual.getImage().getId()).isEqualTo(languageRequestDTO.getImageId());
    }

    @Test
    void toLanguage_throwsException_whenImageDoesNotExist() {
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
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(languageRequestDTO.getImageId()));
    }
}