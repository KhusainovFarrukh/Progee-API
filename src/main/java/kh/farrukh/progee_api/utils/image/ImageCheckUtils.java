package kh.farrukh.progee_api.utils.image;

import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.exception.ResourceNotFoundException;

public class ImageCheckUtils {

    public static void checkImageId(ImageRepository imageRepository, long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("Image", "id", imageId);
        }
    }
}
