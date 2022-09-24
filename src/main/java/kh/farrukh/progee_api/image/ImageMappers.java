package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import org.springframework.beans.BeanUtils;

/**
 * It contains two methods that map between an ImageRequestDTO, ImageResponseDTO and an Image
 */
public class ImageMappers {

    public static ImageResponseDTO toImageResponseDto(Image image) {
        if (image == null) return null;
        ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
        BeanUtils.copyProperties(image, imageResponseDTO);
        return imageResponseDTO;
    }

    public static Image toImage(ImageResponseDTO imageResponseDTO) {
        if (imageResponseDTO == null) return null;
        Image image = new Image();
        BeanUtils.copyProperties(imageResponseDTO, image);
        return image;
    }
}
