package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.review.payloads.ReviewRequestDTO;
import kh.farrukh.progee_api.endpoints.review.payloads.ReviewVoteRequestDTO;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.role.Role;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.AppUserRepository;
import kh.farrukh.progee_api.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ReviewDuplicateVoteException;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private AppUserRepository appUserRepository;
    @InjectMocks
    private ReviewServiceImpl underTest;

    @Test
    void canGetAllReviews() {
        // given
        SecurityContextHolder.clearContext();
        when(languageRepository.existsById(any())).thenReturn(true);
        when(reviewRepository.findAll(any(ReviewSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getReviews(1L, null, 1, 10, "id", "ASC");

        // then
        verify(reviewRepository).findAll(
                new ReviewSpecification(1L, null),
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
        when(reviewRepository.findAll(any(ReviewSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getReviews(1L, ReviewValue.LIKE, 1, 10, "id", "ASC");

        // then
        verify(reviewRepository).findAll(
                new ReviewSpecification(1L, ReviewValue.LIKE),
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
                underTest.getReviews(
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
        when(reviewRepository.findById(any())).thenReturn(Optional.of(new Review()));

        // when
        underTest.getReviewById(reviewId);

        // then
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void throwsExceptionIfReviewDoesNotExistWithId() {
        // given
        long reviewId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.getReviewById(reviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining(String.valueOf(reviewId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void canCreateReview() {
        // given
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue, 1L);
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language(1)));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("user@mail.com")));

        // when
        underTest.addReview(reviewRequestDto);

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
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO("", ReviewValue.LIKE, languageId);
        when(languageRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.addReview(reviewRequestDto))
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
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(author);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));

        // when
        underTest.updateReview(1, reviewRequestDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());
        Review actual = reviewArgCaptor.getValue();
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
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(author);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(1, reviewRequestDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithUpdateOthersPermissionCanUpdateReview() {
        // given
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHERS_REVIEW)));
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        underTest.updateReview(1, reviewRequestDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());
        Review actual = reviewArgCaptor.getValue();
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
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(1, reviewRequestDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void throwsExceptionIfReviewToUpdateDoesNotExistWithId() {
        // given
        long frameworkId = 1;
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO("", ReviewValue.LIKE);
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(frameworkId, reviewRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining(String.valueOf(frameworkId));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void userWithDeleteOthersPermissionCanDeleteReviewById() {
        // given
        long reviewId = 1;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_DELETE_OTHERS_REVIEW)));
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        underTest.deleteReview(reviewId);

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
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(reviewId))
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
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        underTest.deleteReview(reviewId);

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
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(reviewId))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void throwsExceptionIfReviewToDeleteDoesNotExistWithId() {
        // given
        long reviewId = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(reviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining(String.valueOf(reviewId));
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canUpvoteReview() {
        // given
        long reviewId = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(true);
        AppUser user = new AppUser(1);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(new Review()));

        // when
        underTest.voteReview(reviewId, voteDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());
        Review actual = reviewArgCaptor.getValue();
        assertThat(user.getId()).isIn(actual.getUpVotes());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canDownvoteReview() {
        // given
        long reviewId = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(false);
        AppUser user = new AppUser(1);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(new Review()));

        // when
        underTest.voteReview(reviewId, voteDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());
        Review actual = reviewArgCaptor.getValue();
        assertThat(user.getId()).isIn(actual.getDownVotes());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canChangeDownvoteToUpvoteReview() {
        // given
        long reviewId = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(true);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getDownVotes().add(user.getId());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        underTest.voteReview(reviewId, voteDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());
        Review actual = reviewArgCaptor.getValue();
        assertThat(user.getId()).isIn(actual.getUpVotes());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void canChangeUpvoteToDownvoteReview() {
        // given
        long reviewId = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(false);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getUpVotes().add(user.getId());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        underTest.voteReview(reviewId, voteDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());
        Review actual = reviewArgCaptor.getValue();
        assertThat(user.getId()).isIn(actual.getDownVotes());
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfUpvotesAlreadyUpvotedReview() {
        // given
        long reviewId = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(true);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getUpVotes().add(user.getId());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(reviewId, voteDto))
                .isInstanceOf(ReviewDuplicateVoteException.class)
                .hasMessageContaining("up-vote");
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "USER")
    void throwsExceptionIfDownvotesAlreadyDownvotedReview() {
        // given
        long reviewId = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(false);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getDownVotes().add(user.getId());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(reviewId, voteDto))
                .isInstanceOf(ReviewDuplicateVoteException.class)
                .hasMessageContaining("down-vote");
    }

    @Test
    @WithMockUser(username = "user@mail.com", authorities = "user")
    void throwsExceptionIfReviewToVoteDoesNotExistWithId() {
        // given
        long reviewId = 1;
        ReviewVoteRequestDTO stateDto = new ReviewVoteRequestDTO(true);
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(reviewId, stateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining(String.valueOf(reviewId));
    }
}