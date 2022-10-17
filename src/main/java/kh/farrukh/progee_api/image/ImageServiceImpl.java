package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.utils.file.FileUtils;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static kh.farrukh.progee_api.global.utils.checkers.Checkers.checkPageNumber;

/**
 * It implements the ImageService interface and uses the ImageRepository
 * to save and retrieve images
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final S3Repository s3Repository;
    private final ImageRepository imageRepository;

    public static final String IMAGES_FOLDER = "images";

    @Override
    public PagingResponse<ImageResponseDTO> getImages(int page, int pageSize) {
        checkPageNumber(page);
        return new PagingResponse<>(
                imageRepository.findAll(PageRequest.of(page - 1, pageSize))
                        .map(ImageMappers::toImageResponseDto)
        );
    }

    /**
     * We're taking a multipart file, saving image to the image repository
     *
     * @param multipartImage The image file that is being uploaded.
     * @return The ImageResponseDTO object is being returned.
     */
    @Override
    public ImageResponseDTO addImage(MultipartFile multipartImage) {
        try {
            String name = FileUtils.getUniqueImageName(multipartImage);
            String url = s3Repository.savePublicReadObject(multipartImage.getInputStream(), IMAGES_FOLDER + "/" + name);
            Image image = new Image(name, url, multipartImage.getSize() / 1024f / 1024f);
            return ImageMappers.toImageResponseDto(imageRepository.save(image));
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    @Override
    public void deleteImage(long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", id));
        s3Repository.deleteObject(IMAGES_FOLDER + "/" + image.getName());
        imageRepository.deleteById(id);
    }
}
