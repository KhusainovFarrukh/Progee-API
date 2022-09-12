package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * It implements the ImageService interface and uses the ImageRepository and FileStoreRepository
 * to save and retrieve images
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    /**
     * We're taking a multipart file, saving image to the image repository
     *
     * @param multipartImage The image file that is being uploaded.
     * @return The image object is being returned.
     */
    @Override
    public ImageResponseDTO addImage(MultipartFile multipartImage) {
        try {
            return ImageMappers.toImageResponseDto(imageRepository.save(new Image(multipartImage.getBytes())));
        } catch (Exception exception) {
            throw new RuntimeException("Error on image upload: " + exception.getMessage());
        }
    }

    /**
     * If the image exists, return it, otherwise throw an exception.
     *
     * @param id The id of the image to be retrieved.
     * @return The image with the given id.
     */
    @Override
    public ImageResponseDTO getImageById(long id) {
        return imageRepository.findById(id)
                .map(ImageMappers::toImageResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", id));
    }

    /**
     * Find the image in the db and return its content as a resource.
     *
     * @param id The id of the image you want to download.
     * @return A resource
     */
    @Override
    public Resource downloadImage(long id) {
        byte[] content = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", id))
                .getContent();

        return new ByteArrayResource(content);
    }
}
