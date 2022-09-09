package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

/**
 * A base interface for service of Review endpoints
 * <p>
 * Methods implemented in ReviewServiceImpl
 */
public interface ReviewService {

    PagingResponse<Review> getReviews(
            Long languageId,
            ReviewValue value,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    Review getReviewById(long id);

    Review addReview(ReviewDTO reviewDto);

    Review updateReview(long id, ReviewDTO reviewDto);

    void deleteReview(long id);

    Review voteReview(long id, ReviewVoteDTO reviewVoteDto);
}
