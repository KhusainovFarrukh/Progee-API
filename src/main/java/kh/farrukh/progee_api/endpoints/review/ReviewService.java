package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.exception.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LanguageRepository languageRepository;

    public PagingResponse<Review> getReviewsByLanguage(
            long languageId,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkLanguageId(languageId);
        return new PagingResponse<>(reviewRepository.findByLanguage_Id(
                languageId,
                PageRequest.of(page, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
        ));
    }

    public Review getReviewById(long languageId, long id) {
        checkLanguageId(languageId);
        return reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );
    }

    public Review addReview(long languageId, ReviewDTO reviewDto) {
        Review review = new Review(reviewDto);
        checkLanguageId(languageId);
        review.setLanguageId(languageId);
        return reviewRepository.save(review);
    }

    @Transactional
    public Review updateReview(long languageId, long id, ReviewDTO reviewDto) {
        checkLanguageId(languageId);
        Review existingReview = reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );

        existingReview.setAuthorId(reviewDto.getAuthorId());
        existingReview.setBody(reviewDto.getBody());
        existingReview.setDownVotes(reviewDto.getDownVotes());
        existingReview.setUpVotes(reviewDto.getUpVotes());
        existingReview.setValue(reviewDto.getValue());
//        existingReview.setLanguageId(reviewDto.getLanguageId());

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