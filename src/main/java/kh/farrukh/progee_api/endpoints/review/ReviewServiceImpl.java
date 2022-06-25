package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.exception.custom_exceptions.ReviewVoteException;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.utils.user.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static kh.farrukh.progee_api.utils.checkers.Checkers.*;

/**
 * It implements the ReviewService interface and uses the ReviewRepository
 * to perform CRUD operations on the Review entity
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;

    /**
     * "Get all reviews for a given language, sorted by a given field, in a given order, and return a page of them."
     *
     * @param languageId The id of the language to get reviews for.
     * @param value      ReviewValue to filter by (optional).
     * @param page       The page number to return.
     * @param pageSize   The number of items to return per page.
     * @param sortBy     The field to sort by.
     * @param orderBy    The direction of the sorting. Can be either "asc" or "desc".
     * @return A PagingResponse object is being returned.
     */
    @Override
    public PagingResponse<Review> getReviewsByLanguage(
            long languageId,
            ReviewValue value,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        checkLanguageId(languageRepository, languageId);
        if (value == null) {
            return new PagingResponse<>(reviewRepository.findByLanguage_Id(
                    languageId,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
            ));
        } else {
            return new PagingResponse<>(reviewRepository.findByLanguage_IdAndValue(
                    languageId,
                    value,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
            ));
        }
    }

    /**
     * If the languageId is valid, return the review with the given id, otherwise throw a ResourceNotFoundException.
     *
     * @param languageId The id of the language that the review is in.
     * @param id         The id of the review to be retrieved.
     * @return Review
     */
    @Override
    public Review getReviewById(long languageId, long id) {
        checkLanguageId(languageRepository, languageId);
        return reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );
    }

    /**
     * It adds a review to a language.
     *
     * @param languageId The id of the language that the review is for.
     * @param reviewDto  This is the object that will be used to create the new Review object.
     * @return A Review object
     */
    @Override
    public Review addReview(long languageId, ReviewDTO reviewDto) {
        Review review = new Review(reviewDto);
        checkLanguageId(languageRepository, languageId);
        review.setLanguageId(languageId);
        return reviewRepository.save(review);
    }

    /**
     * This function updates a review in the database
     *
     * @param languageId The id of the language that the review is associated with.
     * @param id         The id of the review to update.
     * @param reviewDto  The ReviewDTO object that contains the new values for the review.
     * @return The updated review.
     */
    @Override
    @Transactional
    public Review updateReview(long languageId, long id, ReviewDTO reviewDto) {
        checkLanguageId(languageRepository, languageId);
        Review existingReview = reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );

        existingReview.setAuthorId(reviewDto.getAuthorId());
        existingReview.setBody(reviewDto.getBody());
        existingReview.setValue(reviewDto.getValue());
//        existingReview.setLanguageId(reviewDto.getLanguageId());

        return existingReview;
    }

    /**
     * This function deletes a review by its id
     *
     * @param languageId The id of the language that the review is for.
     * @param id         The id of the review to delete.
     */
    @Override
    public void deleteReview(long languageId, long id) {
        checkLanguageId(languageRepository, languageId);
        checkReviewId(reviewRepository, id);
        reviewRepository.deleteById(id);
    }

    /**
     * If the user has not voted on the review, then add the user's id to the upVotes or downVotes list
     *
     * @param languageId    The id of the language that the review is for.
     * @param id            The id of the review to vote on.
     * @param reviewVoteDto This is the DTO that contains the vote.
     * @return Review
     */
    @Override
    @Transactional
    public Review voteReview(long languageId, long id, ReviewVoteDTO reviewVoteDto) {
        checkLanguageId(languageRepository, languageId);
        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );

        // Get the user who is currently logged in.
        AppUser user = UserUtils.getCurrentUser(userRepository);

        // Checking if the user has already voted on the review. If the user has already voted on the review, then
        // throw a ReviewVoteException.
        if (reviewVoteDto.isVote()) {
            if (review.getUpVotes().contains(user.getId())) {
                throw new ReviewVoteException("up-vote");
            }

            review.getUpVotes().add(user.getId());
            review.getDownVotes().remove(user.getId());
        } else {
            if (review.getDownVotes().contains(user.getId())) {
                throw new ReviewVoteException("down-vote");
            }

            review.getDownVotes().add(user.getId());
            review.getUpVotes().remove(user.getId());
        }

        return review;
    }
}