package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.global.utils.file.NotEmptyFile;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.image.payloads.ImageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static kh.farrukh.progee_api.image.ImageConstants.ENDPOINT_IMAGE;

/**
 * Controller that exposes endpoints for managing images
 */
@RestController
@RequestMapping(ENDPOINT_IMAGE)
@RequiredArgsConstructor
@Validated
public class ImageController {

    private final ImageService imageService;

    /**
     * Get a list of images, with pagination.
     *
     * @param page     The page number of the images to be returned.
     * @param pageSize The number of items to be returned in a page.
     * @return A list of images
     */
    @GetMapping
    public ResponseEntity<PagingResponse<ImageResponseDTO>> getImages(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(imageService.getImages(page, pageSize));
    }

    /**
     * It takes a multipart image, saves it, and then returns a response entity with the status code of 201 and the
     * body of the image response DTO
     *
     * @param multipartImage The image file that is being uploaded.
     * @return A ResponseEntity object is being returned.
     */
    @PostMapping
    public ResponseEntity<ImageResponseDTO> uploadImage(@NotEmptyFile @RequestParam("image") MultipartFile multipartImage) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(imageService.addImage(multipartImage));
    }

    /**
     * This function is a GET request that takes in an id and returns an image object
     *
     * @param id The id of the image you want to retrieve.
     * @return A ResponseEntity object is being returned.
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<ImageResponseDTO> getImageById(@PathVariable long id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    /**
     * The function deletes an image by id
     *
     * @param id The id of the image to be deleted.
     * @return ResponseEntity.noContent().build();
     */
    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}