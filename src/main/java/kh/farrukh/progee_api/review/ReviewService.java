package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.utils.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LanguageRepository languageRepository;

    public ReviewService(ReviewRepository reviewRepository, LanguageRepository languageRepository) {
        this.reviewRepository = reviewRepository;
        this.languageRepository = languageRepository;
    }

    public List<Review> getReviewsByLanguage(long languageId) {
        checkLanguageId(languageId);
        return reviewRepository.findByLanguage_Id(languageId);
    }

    public Review getReviewById(long languageId, long id) {
        checkLanguageId(languageId);
        return reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );
    }

    public Review addReview(long languageId, Review review) {
        checkLanguageId(languageId);
        review.setLanguageId(languageId);
        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(long languageId, long id, Review review) {
        checkLanguageId(languageId);
        review.setLanguageId(languageId);
        Review existingReview = reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );

        existingReview.setAuthor(review.getAuthor());
        existingReview.setBody(review.getBody());
        existingReview.setDownVotes(review.getDownVotes());
        existingReview.setUpVotes(review.getUpVotes());
        existingReview.setValue(review.getValue());
        existingReview.setCreatedAt(review.getCreatedAt());
        existingReview.setLanguageId(review.getLanguage().getId());

        return existingReview;
    }

    public void deleteReview(long languageId, long id) {
        checkLanguageId(languageId);
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", "id", id);
        }
        reviewRepository.deleteById(id);
    }

    private void checkLanguageId(long languageId) {
        if (!languageRepository.existsById(languageId)) {
            throw new ResourceNotFoundException("Language", "id", languageId);
        }
    }
}