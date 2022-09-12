package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.review.payloads.ReviewRequestDTO;
import kh.farrukh.progee_api.review.payloads.ReviewResponseDTO;
import kh.farrukh.progee_api.review.payloads.ReviewVoteRequestDTO;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.progee_api.review.ReviewController.ENDPOINT_REVIEW;

/**
 * Controller that exposes endpoints for managing reviews
 */
@RestController
@RequestMapping(ENDPOINT_REVIEW)
@RequiredArgsConstructor
public class ReviewController {

    public static final String ENDPOINT_REVIEW = "/api/v1/reviews";

    private final ReviewService reviewService;

    /**
     * It returns a list (with pagination) of reviews for a given language
     *
     * @param languageId The id of the language to get reviews for.
     * @param value      ReviewValue to filter by (optional).
     * @param page       The page number to return.
     * @param pageSize   The number of items to be returned in a single page.
     * @param sortBy     The field to sort by.
     * @param orderBy    asc or desc
     * @return A list of reviews for a given language.
     */
    @GetMapping
    public ResponseEntity<PagingResponse<ReviewResponseDTO>> getReviews(
            @RequestParam(name = "language_id", required = false) Long languageId,
            @RequestParam(name = "value", required = false) ReviewValue value,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort_by", defaultValue = "id") String sortBy,
            @RequestParam(name = "order_by", defaultValue = "asc") String orderBy
    ) {
        return ResponseEntity.ok(reviewService.getReviews(languageId, value, page, pageSize, sortBy, orderBy));
    }

    /**
     * This function returns a review with the given id, if it exists
     *
     * @param id The id of the review you want to get.
     * @return A review object
     */
    @GetMapping("{id}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    /**
     * This function takes creates review if it does not exist.
     *
     * @param reviewRequestDto Values for the review to be created.
     * @return A ResponseEntity containing created Review object and HttpStatus.
     */
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> addReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reviewService.addReview(reviewRequestDto));
    }

    /**
     * This function updates a review.
     *
     * @param id               The id of the review to update
     * @param reviewRequestDto The review values that we want to update.
     * @return A ResponseEntity with the updated Framework object and HttpStatus.
     */
    @PutMapping("{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable long id,
            @Valid @RequestBody ReviewRequestDTO reviewRequestDto
    ) {
        return ResponseEntity.ok(reviewService.updateReview(id, reviewRequestDto));
    }

    /**
     * This function deletes a review from a language
     *
     * @param id The id of the review to delete
     * @return A ResponseEntity with HttpStatus.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Function for voting on a review. Up-vote or down-vote.
     *
     * @param id                   the id of the review
     * @param reviewVoteRequestDto This is the object that contains the vote value.
     * @return Review
     */
    @PostMapping("{id}/vote")
    public ResponseEntity<ReviewResponseDTO> voteReview(
            @PathVariable long id,
            @Valid @RequestBody ReviewVoteRequestDTO reviewVoteRequestDto
    ) {
        return ResponseEntity.ok(reviewService.voteReview(id, reviewVoteRequestDto));
    }
}