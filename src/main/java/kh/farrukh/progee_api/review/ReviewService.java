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
                () -> new ResourceNotFoundException("Review", id)
        );
    }

    public void addReview(long languageId, Review review) {
        checkLanguageId(languageId);
        review.setLanguageId(languageId);
        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(long languageId, long id, Review review) {
        checkLanguageId(languageId);
        review.setLanguageId(languageId);
        Review reviewToUpdate = reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", id)
        );

        reviewToUpdate.setAuthor(review.getAuthor());
        reviewToUpdate.setBody(review.getBody());
        reviewToUpdate.setDownVotes(review.getDownVotes());
        reviewToUpdate.setUpVotes(review.getUpVotes());
        reviewToUpdate.setValue(review.getValue());
        reviewToUpdate.setCreatedAt(review.getCreatedAt());
        reviewToUpdate.setLanguageId(review.getLanguage().getId());
    }

    public void deleteReview(long languageId, long id) {
        checkLanguageId(languageId);
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", id);
        }
        reviewRepository.deleteById(id);
    }

    private void checkLanguageId(long languageId) {
        if (!languageRepository.existsById(languageId)) {
            throw new ResourceNotFoundException("Language", languageId);
        }
    }
}