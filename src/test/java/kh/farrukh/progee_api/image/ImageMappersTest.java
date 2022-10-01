package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ImageMappersTest {

    @Test
    void toImageResponseDto_returnsNull_whenImageIsNull() {
        // given
        Image image = null;

        // when
        ImageResponseDTO imageResponseDTO = ImageMappers.toImageResponseDto(image);

        // then
        assertThat(imageResponseDTO).isNull();
    }

    @Test
    void toImageResponseDto_canMap_whenImageIsValid() {
        // given
        Image image = new Image(1, null);

        // when
        ImageResponseDTO imageResponseDTO = ImageMappers.toImageResponseDto(image);

        // then
        assertThat(imageResponseDTO).isNotNull();
        assertThat(imageResponseDTO.getId()).isEqualTo(image.getId());
    }

    @Test
    void toImage_returnsNull_whenImageResponseDTOIsNull() {
        // given
        ImageResponseDTO imageResponseDTO = null;

        // when
        Image image = ImageMappers.toImage(imageResponseDTO);

        // then
        assertThat(image).isNull();
    }

    @Test
    void toImage_canMap_whenImageResponseDTOIsValid() {
        // given
        ImageResponseDTO imageResponseDTO = new ImageResponseDTO(1);

        // when
        Image image = ImageMappers.toImage(imageResponseDTO);

        // then
        assertThat(image).isNotNull();
        assertThat(image.getId()).isEqualTo(imageResponseDTO.getId());
    }
}