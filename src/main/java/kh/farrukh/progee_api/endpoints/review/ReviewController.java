package kh.farrukh.progee_api.endpoints.review;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kh.farrukh.progee_api.utils.constant.ApiEndpoints.ENDPOINT_REVIEW;

@RestController
@RequestMapping(ENDPOINT_REVIEW)
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getReviewsByLanguage(@PathVariable long languageId) {
        return new ResponseEntity<>(reviewService.getReviewsByLanguage(languageId), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable long languageId, @PathVariable long id) {
        return new ResponseEntity<>(reviewService.getReviewById(languageId, id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Review> addReview(@PathVariable long languageId, @RequestBody Review review) {
        return new ResponseEntity<>(reviewService.addReview(languageId, review), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Review> updateLanguage(
            @PathVariable long languageId,
            @PathVariable long id,
            @RequestBody Review review
    ) {
        return new ResponseEntity<>(reviewService.updateReview(languageId, id, review), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable long languageId, @PathVariable long id) {
        reviewService.deleteReview(languageId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}