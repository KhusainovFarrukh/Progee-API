package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.exception.custom_exceptions.ReviewDuplicateVoteException;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SecurityTestExecutionListeners
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ReviewServiceImpl underTest;

    @Test
    void canGetAllReviews() {
        // given
        SecurityContextHolder.clearContext();
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        underTest.getReviewsByLanguage(1, null, 1, 10, "id", "ASC");

        // then
        verify(reviewRepository).findByLanguage_Id(
                1,
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    void canGetReviewsByReviewValue() {
        // given
        SecurityContextHolder.clearContext();
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        underTest.getReviewsByLanguage(1, ReviewValue.LIKE, 1, 10, "id", "ASC");

        // then
        verify(reviewRepository).findByLanguage_IdAndReviewValue(
                1,
                ReviewValue.LIKE,
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    void throwsExceptionIfLanguageOfReviewsDoesNotExistWithId() {
        // given
        SecurityContextHolder.clearContext();
        long languageId = 1;

        // when
        // then
        assertThatThrownBy(() ->
                underTest.getReviewsByLanguage(
                        languageId, null, 1, 10, "id", "ASC"
                ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    void canGetReviewById() {
        // given
        long reviewId = 1;
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(new Review()));

        // when
        underTest.getReviewById(1, reviewId);

        // then
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void throwsExceptionIfReviewDoesNotExistWithId() {
        // given
        long reviewId = 1;
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.getReviewById(1, reviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining(String.valueOf(reviewId));
    }

    @Test
    void throwsExceptionIfLanguageOfReviewDoesNotExistWithId() {
        // given
        long languageId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.getReviewById(languageId, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canCreateReview() {
        // given
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        ReviewDTO reviewDto = new ReviewDTO(body, reviewValue);
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language(1)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("user@mail.com")));

        // when
        underTest.addReview(1, reviewDto);

        // then
        ArgumentCaptor<Review> languageArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(languageArgCaptor.capture());

        Review capturedReview = languageArgCaptor.getValue();
        assertThat(capturedReview.getLanguage().getId()).isEqualTo(1);
        assertThat(capturedReview.getAuthor().getUsername()).isEqualTo("user@mail.com");
        assertThat(capturedReview.getBody()).isEqualTo(body);
        assertThat(capturedReview.getReviewValue()).isEqualTo(reviewValue);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfLanguageOfFrameworkToCreateDoesNotExistWithId() {
        // given
        long languageId = 1;
        ReviewDTO reviewDto = new ReviewDTO("", ReviewValue.LIKE);
        when(languageRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.addReview(languageId, reviewDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void authorWithUpdateOwnPermissionCanUpdateReview() {
        // given
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        AppUser author = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_UPDATE_OWN_REVIEW)));
        ReviewDTO reviewDto = new ReviewDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(author);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));

        // when
        Review actual = underTest.updateReview(1, 1, reviewDto);

        // then
        assertThat(actual.getBody()).isEqualTo(body);
        assertThat(actual.getReviewValue()).isEqualTo(reviewValue);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfAuthorWithoutUpdateOwnPermissionUpdatesReview() {
        // given
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        AppUser author = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        ReviewDTO reviewDto = new ReviewDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(author);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(1, 1, reviewDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithUpdateOthersPermissionCanUpdateReview() {
        // given
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHERS_REVIEW)));
        ReviewDTO reviewDto = new ReviewDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        Review actual = underTest.updateReview(1, 1, reviewDto);

        // then
        assertThat(actual.getBody()).isEqualTo(body);
        assertThat(actual.getReviewValue()).isEqualTo(reviewValue);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUserWithoutUpdateOthersPermissionUpdatesReview() {
        // given
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        ReviewDTO reviewDto = new ReviewDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(1, 1, reviewDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void throwsExceptionIfReviewToUpdateDoesNotExistWithId() {
        // given
        long frameworkId = 1;
        ReviewDTO reviewDto = new ReviewDTO("", ReviewValue.LIKE);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(1, frameworkId, reviewDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining(String.valueOf(frameworkId));
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void throwsExceptionIfLanguageOfReviewToUpdateDoesNotExistWithId() {
        // given
        long languageId = 1;
        ReviewDTO reviewDTto = new ReviewDTO("", ReviewValue.LIKE);

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(languageId, 1, reviewDTto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithDeleteOthersPermissionCanDeleteReviewById() {
        // given
        long reviewId = 1;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_DELETE_OTHERS_REVIEW)));
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        underTest.deleteReview(1, reviewId);

        // then
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfUserWithoutDeleteOthersPermissionDeletesReviewById() {
        // given
        long reviewId = 1;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(1, reviewId))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void authorWithDeleteOwnPermissionCanDeleteReviewById() {
        // given
        long reviewId = 1;
        AppUser author = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_DELETE_OWN_REVIEW)));
        Review existingReview = new Review();
        existingReview.setAuthor(author);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        underTest.deleteReview(1, reviewId);

        // then
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void throwsExceptionIfAuthorWithoutDeleteOwnPermissionDeletesReviewById() {
        // given
        long reviewId = 1;
        AppUser author = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        Review existingReview = new Review();
        existingReview.setAuthor(author);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(1, reviewId))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void throwsExceptionIfReviewToDeleteDoesNotExistWithId() {
        // given
        long reviewId = 1;
        when(languageRepository.existsById(any())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(1, reviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining(String.valueOf(reviewId));
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void throwsExceptionIfLanguageOfReviewToDeleteDoesNotExistWithId() {
        // given
        long languageId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(languageId, 1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canUpvoteReview() {
        // given
        long reviewId = 1;
        ReviewVoteDTO voteDto = new ReviewVoteDTO(true);
        AppUser user = new AppUser(1);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(new Review()));

        // when
        Review review = underTest.voteReview(1, reviewId, voteDto);

        // then
        assertThat(user.getId()).isIn(review.getUpVotes());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canDownvoteReview() {
        // given
        long reviewId = 1;
        ReviewVoteDTO voteDto = new ReviewVoteDTO(false);
        AppUser user = new AppUser(1);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(new Review()));

        // when
        Review review = underTest.voteReview(1, reviewId, voteDto);

        // then
        assertThat(user.getId()).isIn(review.getDownVotes());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canChangeDownvoteToUpvoteReview() {
        // given
        long reviewId = 1;
        ReviewVoteDTO voteDto = new ReviewVoteDTO(true);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getDownVotes().add(user.getId());
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        Review review = underTest.voteReview(1, reviewId, voteDto);

        // then
        assertThat(user.getId()).isIn(review.getUpVotes());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canChangeUpvoteToDownvoteReview() {
        // given
        long reviewId = 1;
        ReviewVoteDTO voteDto = new ReviewVoteDTO(false);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getUpVotes().add(user.getId());
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        Review review = underTest.voteReview(1, reviewId, voteDto);

        // then
        assertThat(user.getId()).isIn(review.getDownVotes());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfUpvotesAlreadyUpvotedReview() {
        // given
        long reviewId = 1;
        ReviewVoteDTO voteDto = new ReviewVoteDTO(true);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getUpVotes().add(user.getId());
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(1, reviewId, voteDto))
                .isInstanceOf(ReviewDuplicateVoteException.class)
                .hasMessageContaining("up-vote");
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfDownvotesAlreadyDownvotedReview() {
        // given
        long reviewId = 1;
        ReviewVoteDTO voteDto = new ReviewVoteDTO(false);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getDownVotes().add(user.getId());
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(1, reviewId, voteDto))
                .isInstanceOf(ReviewDuplicateVoteException.class)
                .hasMessageContaining("down-vote");
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void throwsExceptionIfReviewToVoteDoesNotExistWithId() {
        // given
        long reviewId = 1;
        ReviewVoteDTO stateDto = new ReviewVoteDTO(true);
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(1, reviewId, stateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining(String.valueOf(reviewId));
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void throwsExceptionIfLanguageOfReviewToVOteDoesNotExistWithId() {
        // given
        long languageId = 1;
        ReviewVoteDTO stateDto = new ReviewVoteDTO(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(languageId, 1, stateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining(String.valueOf(languageId));
    }
}