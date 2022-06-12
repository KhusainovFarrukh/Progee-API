package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_REVIEW;

@RestController
@RequestMapping(ENDPOINT_REVIEW)
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<PagingResponse<Review>> getReviewsByLanguage(
            @PathVariable long languageId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return new ResponseEntity<>(reviewService.getReviewsByLanguage(
                languageId, page, pageSize, sortBy, orderBy
        ), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable long languageId, @PathVariable long id) {
        return new ResponseEntity<>(reviewService.getReviewById(languageId, id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Review> addReview(@PathVariable long languageId, @RequestBody ReviewDTO reviewDto) {
        return new ResponseEntity<>(reviewService.addReview(languageId, reviewDto), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Review> updateLanguage(
            @PathVariable long languageId,
            @PathVariable long id,
            @RequestBody ReviewDTO reviewDto
    ) {
        return new ResponseEntity<>(reviewService.updateReview(languageId, id, reviewDto), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable long languageId, @PathVariable long id) {
        reviewService.deleteReview(languageId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}