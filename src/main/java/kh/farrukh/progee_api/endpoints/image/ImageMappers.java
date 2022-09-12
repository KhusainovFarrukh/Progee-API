package kh.farrukh.progee_api.endpoints.image;

import kh.farrukh.progee_api.endpoints.image.payloads.ImageResponseDTO;
import org.springframework.beans.BeanUtils;

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
