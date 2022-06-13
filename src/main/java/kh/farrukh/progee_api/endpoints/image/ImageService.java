package kh.farrukh.progee_api.endpoints.image;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * A base interface for service of Image endpoints
 *
 * Methods implemented in ImageServiceImpl
 */
public interface ImageService {

    Image addImage(MultipartFile multipartImage);

    Image getImageById(long id);

    Resource downloadImage(long id);
}
