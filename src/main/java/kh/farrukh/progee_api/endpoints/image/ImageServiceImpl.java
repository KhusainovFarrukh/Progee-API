package kh.farrukh.progee_api.endpoints.image;

import kh.farrukh.progee_api.endpoints.image.file_store.FileStoreRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final FileStoreRepository fileStoreRepository;

    @Override
    public Image addImage(MultipartFile multipartImage) {
        try {
            return imageRepository.save(
                    new Image(fileStoreRepository.save(multipartImage.getBytes()))
            );
        } catch (Exception exception) {
            throw new RuntimeException("Error on image upload: " + exception.getMessage());
        }
    }

    @Override
    public Image getImageById(long id) {
        return imageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Image", "id", id)
        );
    }

    @Override
    public Resource downloadImage(long id) {
        return fileStoreRepository.findInFileSystem(getImageById(id).getLocation());
    }
}
