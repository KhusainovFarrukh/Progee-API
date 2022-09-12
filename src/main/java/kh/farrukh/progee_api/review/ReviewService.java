package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.review.payloads.ReviewRequestDTO;
import kh.farrukh.progee_api.review.payloads.ReviewResponseDTO;
import kh.farrukh.progee_api.review.payloads.ReviewVoteRequestDTO;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;

/**
 * A base interface for service of Review endpoints
 * <p>
 * Methods implemented in ReviewServiceImpl
 */
public interface ReviewService {

    PagingResponse<ReviewResponseDTO> getReviews(
            Long languageId,
            ReviewValue value,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    ReviewResponseDTO getReviewById(long id);

    ReviewResponseDTO addReview(ReviewRequestDTO reviewRequestDto);

    ReviewResponseDTO updateReview(long id, ReviewRequestDTO reviewRequestDto);

    void deleteReview(long id);

    ReviewResponseDTO voteReview(long id, ReviewVoteRequestDTO reviewVoteRequestDto);
}
