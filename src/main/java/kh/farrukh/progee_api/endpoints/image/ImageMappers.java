package kh.farrukh.progee_api.endpoints.image;

import kh.farrukh.progee_api.endpoints.image.payloads.ImageResponseDTO;
import org.springframework.beans.BeanUtils;

public class ImageMappers {

    public static ImageResponseDTO toImageDto(Image image) {
        if (image == null) return null;
        ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
        BeanUtils.copyProperties(image, imageResponseDTO);
        return imageResponseDTO;
    }
}