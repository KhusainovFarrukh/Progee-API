package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * A base interface for service of Image endpoints
 * <p>
 * Methods implemented in ImageServiceImpl
 */
public interface ImageService {

    PagingResponse<ImageResponseDTO> getImages(int page, int pageSize);

    ImageResponseDTO addImage(MultipartFile multipartImage);

    ImageResponseDTO getImageById(long id);

    Resource downloadImage(long id);

    void deleteImage(long id);
}
