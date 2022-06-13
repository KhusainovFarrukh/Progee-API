package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

/**
 * A base interface for service of Review endpoints
 *
 * Methods implemented in ReviewServiceImpl
 */
public interface ReviewService {

    PagingResponse<Review> getReviewsByLanguage(
            long languageId,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    Review getReviewById(long languageId, long id);

    Review addReview(long languageId, ReviewDTO reviewDto);

    Review updateReview(long languageId, long id, ReviewDTO reviewDto);

    void deleteReview(long languageId, long id);

    Review voteReview(long languageId, long id, ReviewVoteDTO reviewVoteDto);
}
