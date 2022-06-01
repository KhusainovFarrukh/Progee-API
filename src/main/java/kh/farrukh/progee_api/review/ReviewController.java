package kh.farrukh.progee_api.review;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/languages/{languageId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Review> getReviewsByLanguage(@PathVariable long languageId) {
        return reviewService.getReviewsByLanguage(languageId);
    }

    @GetMapping(path = "{id}")
    public Review getReviewById(@PathVariable long languageId, @PathVariable long id) {
        return reviewService.getReviewById(languageId, id);
    }

    @PostMapping
    public void addReview(@PathVariable long languageId, @RequestBody Review review) {
        reviewService.addReview(languageId, review);
    }

    @PutMapping(path = "{id}")
    public void updateLanguage(
            @PathVariable long languageId,
            @PathVariable long id,
            @RequestBody Review review
    ) {
        reviewService.updateReview(languageId, id, review);
    }

    @DeleteMapping(path = "{id}")
    public void deleteLanguage(@PathVariable long languageId, @PathVariable long id) {
        reviewService.deleteReview(languageId, id);
    }
}