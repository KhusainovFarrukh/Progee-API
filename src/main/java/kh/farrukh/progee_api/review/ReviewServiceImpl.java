package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.review.payloads.ReviewRequestDTO;
import kh.farrukh.progee_api.review.payloads.ReviewResponseDTO;
import kh.farrukh.progee_api.review.payloads.ReviewVoteRequestDTO;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ReviewDuplicateVoteException;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.global.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.global.utils.user.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static kh.farrukh.progee_api.global.utils.checkers.Checkers.*;

/**
 * It implements the ReviewService interface and uses the ReviewRepository
 * to perform CRUD operations on the Review entity
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final LanguageRepository languageRepository;
    private final AppUserRepository appUserRepository;

    /**
     * "Get all sorted by a given field, in a given order, and return a page of them."
     *
     * @param languageId The id of the language to get reviews for (optional).
     * @param value      ReviewValue to filter by (optional).
     * @param page       The page number to return.
     * @param pageSize   The number of items to return per page.
     * @param sortBy     The field to sort by.
     * @param orderBy    The direction of the sorting. Can be either "asc" or "desc".
     * @return A PagingResponse object is being returned.
     */
    @Override
    public PagingResponse<ReviewResponseDTO> getReviews(
            Long languageId,
            ReviewValue value,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        if (languageId != null) checkLanguageId(languageRepository, languageId);
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy));
        // TODO: 6/12/22 add custom (transient) field: score
        checkSortParams(pageable, List.of("id", "body", "reviewValue", "upVotes", "downVotes", "createdAt"));

        return new PagingResponse<>(
                reviewRepository.findAll(new ReviewSpecification(languageId, value), pageable)
                        .map(ReviewMappers::toReviewResponseDTO)
        );
    }

    /**
     * Return the review with the given id.
     *
     * @param id The id of the review to be retrieved.
     * @return Review
     */
    @Override
    public ReviewResponseDTO getReviewById(long id) {
        return reviewRepository.findById(id)
                .map(ReviewMappers::toReviewResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
    }

    /**
     * It adds a review to a language.
     *
     * @param reviewRequestDto This is the object that will be used to create the new Review object.
     * @return A Review object
     */
    @Override
    public ReviewResponseDTO addReview(ReviewRequestDTO reviewRequestDto) {
        if (reviewRequestDto.getLanguageId() == null) {
            throw new BadRequestException("Language id");
        }
        Review review = ReviewMappers.toReview(reviewRequestDto, languageRepository);
        review.setAuthor(CurrentUserUtils.getCurrentUser(appUserRepository));
        return ReviewMappers.toReviewResponseDTO(reviewRepository.save(review));
    }

    /**
     * This function updates a review in the database
     *
     * @param id               The id of the review to update.
     * @param reviewRequestDto The ReviewDTO object that contains the new values for the review.
     * @return The updated review.
     */
    @Override
    public ReviewResponseDTO updateReview(long id, ReviewRequestDTO reviewRequestDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        // Checking if the current user has the permission to update the review.
        if (CurrentUserUtils.hasPermissionOrIsAuthor(
                Permission.CAN_UPDATE_OTHERS_REVIEW,
                Permission.CAN_UPDATE_OWN_REVIEW,
                review.getAuthor().getId(),
                appUserRepository
        )) {
            review.setBody(reviewRequestDto.getBody());
            review.setReviewValue(reviewRequestDto.getReviewValue());
        } else {
            throw new NotEnoughPermissionException();
        }

        return ReviewMappers.toReviewResponseDTO(reviewRepository.save(review));
    }

    /**
     * This function deletes a review by its id
     *
     * @param id The id of the review to delete.
     */
    @Override
    public void deleteReview(long id) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        // Checking if the current user has the permission to delete the review.
        if (CurrentUserUtils.hasPermissionOrIsAuthor(
                Permission.CAN_DELETE_OTHERS_REVIEW,
                Permission.CAN_DELETE_OWN_REVIEW,
                existingReview.getAuthor().getId(),
                appUserRepository
        )) {
            reviewRepository.deleteById(id);
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    /**
     * If the user has not voted on the review, then add the user's id to the upVotes or downVotes list
     *
     * @param id                   The id of the review to vote on.
     * @param reviewVoteRequestDto This is the DTO that contains the vote.
     * @return Review
     */
    @Override
    public ReviewResponseDTO voteReview(long id, ReviewVoteRequestDTO reviewVoteRequestDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        // Get the user who is currently logged in.
        AppUser currentUser = CurrentUserUtils.getCurrentUser(appUserRepository);

        // Checking if the currentUser has already voted on the review. If the current user has already voted on the review, then
        // throw a ReviewDuplicateVoteException.
        if (reviewVoteRequestDto.isVote()) {
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

        return ReviewMappers.toReviewResponseDTO(reviewRepository.save(review));
    }
}