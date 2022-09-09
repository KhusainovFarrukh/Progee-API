package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ReviewDuplicateVoteException;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.utils.user.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static kh.farrukh.progee_api.utils.checkers.Checkers.checkLanguageId;
import static kh.farrukh.progee_api.utils.checkers.Checkers.checkPageNumber;

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
    public PagingResponse<Review> getReviews(
            Long languageId,
            ReviewValue value,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        if (languageId != null) checkLanguageId(languageRepository, languageId);
        return new PagingResponse<>(reviewRepository.findAll(
                new ReviewSpecification(languageId, value),
                PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
        ));
    }

    /**
     * If the languageId is valid, return the review with the given id, otherwise throw a ResourceNotFoundException.
     *
     * @param id The id of the review to be retrieved.
     * @return Review
     */
    @Override
    public Review getReviewById(long id) {
        return reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );
    }

    /**
     * It adds a review to a language.
     *
     * @param reviewDto This is the object that will be used to create the new Review object.
     * @return A Review object
     */
    @Override
    public Review addReview(ReviewDTO reviewDto) {
        if (reviewDto.getLanguageId() == null) {
            throw new BadRequestException("Language id");
        }
        Review review = new Review(reviewDto, languageRepository);
        review.setAuthor(CurrentUserUtils.getCurrentUser(userRepository));
        return reviewRepository.save(review);
    }

    /**
     * This function updates a review in the database
     *
     * @param id        The id of the review to update.
     * @param reviewDto The ReviewDTO object that contains the new values for the review.
     * @return The updated review.
     */
    @Override
    public Review updateReview(long id, ReviewDTO reviewDto) {
        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );

        if (
                CurrentUserUtils.hasPermissionOrIsAuthor(
                        Permission.CAN_UPDATE_OTHERS_REVIEW,
                        Permission.CAN_UPDATE_OWN_REVIEW,
                        review.getAuthor().getId(),
                        userRepository
                )
        ) {

            review.setBody(reviewDto.getBody());
            review.setReviewValue(reviewDto.getValue());
        } else {
            throw new NotEnoughPermissionException();
        }

        return reviewRepository.save(review);
    }

    /**
     * This function deletes a review by its id
     *
     * @param id The id of the review to delete.
     */
    @Override
    public void deleteReview(long id) {
        Review existingReview = reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );

        if (
                CurrentUserUtils.hasPermissionOrIsAuthor(
                        Permission.CAN_DELETE_OTHERS_REVIEW,
                        Permission.CAN_DELETE_OWN_REVIEW,
                        existingReview.getAuthor().getId(),
                        userRepository
                )
        ) {

            reviewRepository.deleteById(id);
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    /**
     * If the user has not voted on the review, then add the user's id to the upVotes or downVotes list
     *
     * @param id            The id of the review to vote on.
     * @param reviewVoteDto This is the DTO that contains the vote.
     * @return Review
     */
    @Override
    public Review voteReview(long id, ReviewVoteDTO reviewVoteDto) {
        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Review", "id", id)
        );

        // Get the user who is currently logged in.
        AppUser currentUser = CurrentUserUtils.getCurrentUser(userRepository);

        // Checking if the currentUser has already voted on the review. If the current user has already voted on the review, then
        // throw a ReviewDuplicateVoteException.
        if (reviewVoteDto.isVote()) {
            if (review.getUpVotes().contains(currentUser.getId())) {
                throw new ReviewDuplicateVoteException("up-vote");
            }

            review.getUpVotes().add(currentUser.getId());
            review.getDownVotes().remove(currentUser.getId());
        } else {
            if (review.getDownVotes().contains(currentUser.getId())) {
                throw new ReviewDuplicateVoteException("down-vote");
            }

            review.getDownVotes().add(currentUser.getId());
            review.getUpVotes().remove(currentUser.getId());
        }

        return reviewRepository.save(review);
    }
}