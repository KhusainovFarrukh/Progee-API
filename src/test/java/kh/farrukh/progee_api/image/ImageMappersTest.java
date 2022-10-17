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
        ImageResponseDTO actual = ImageMappers.toImageResponseDto(image);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toImageResponseDto_canMap_whenImageIsValid() {
        // given
        Image image = new Image(1);

        // when
        ImageResponseDTO actual = ImageMappers.toImageResponseDto(image);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(image.getId());
    }

    @Test
    void toImage_returnsNull_whenImageResponseDTOIsNull() {
        // given
        ImageResponseDTO imageResponseDTO = null;

        // when
        Image actual = ImageMappers.toImage(imageResponseDTO);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toImage_canMap_whenImageResponseDTOIsValid() {
        // given
        ImageResponseDTO imageResponseDTO = new ImageResponseDTO(1, "test.png", "", 1f);

        // when
        Image actual = ImageMappers.toImage(imageResponseDTO);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(imageResponseDTO.getId());
        assertThat(actual.getName()).isEqualTo(imageResponseDTO.getName());
        assertThat(actual.getUrl()).isEqualTo(imageResponseDTO.getUrl());
        assertThat(actual.getSize()).isEqualTo(imageResponseDTO.getSize());
    }
}