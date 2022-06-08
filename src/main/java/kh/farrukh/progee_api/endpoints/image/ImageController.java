package kh.farrukh.progee_api.endpoints.image;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_IMAGE;

@RestController
@RequestMapping(ENDPOINT_IMAGE)
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<Image> uploadImage(@RequestParam() MultipartFile multipartImage) {
        return new ResponseEntity<>(imageService.addImage(multipartImage), HttpStatus.CREATED);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<Image> getImageById(@PathVariable long id) {
        return new ResponseEntity<>(imageService.getImageById(id), HttpStatus.OK);
    }

    @GetMapping(value = "{id}/download", produces = MediaType.IMAGE_JPEG_VALUE)
    public Resource downloadImageById(@PathVariable long id) {
        return imageService.downloadImage(id);
    }
}