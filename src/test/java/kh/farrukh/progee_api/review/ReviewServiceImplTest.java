package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ReviewDuplicateVoteException;
import kh.farrukh.progee_api.global.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.review.payloads.ReviewRequestDTO;
import kh.farrukh.progee_api.review.payloads.ReviewVoteRequestDTO;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.role.Role;
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
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SecurityTestExecutionListeners
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
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
    @WithAnonymousUser
    void getReviews_canGetReviews_whenWithoutLanguageIdAndReviewValueFilter() {
        // given
        when(reviewRepository.findAll(any(ReviewSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getReviews(null, null, 1, 10, "id", "ASC");

        // then
        verify(reviewRepository).findAll(
                new ReviewSpecification(null, null),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithAnonymousUser
    void getReviews_canGetReviews_whenWithoutLanguageIdAndWithReviewValueFilter() {
        // given
        when(reviewRepository.findAll(any(ReviewSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getReviews(null, ReviewValue.LIKE, 1, 10, "id", "ASC");

        // then
        verify(reviewRepository).findAll(
                new ReviewSpecification(null, ReviewValue.LIKE),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithAnonymousUser
    void getReviews_canGetReviews_whenWithLanguageIdAndWithoutReviewValueFilter() {
        // given
        long languageId = 1;
        when(languageRepository.existsById(languageId)).thenReturn(true);
        when(reviewRepository.findAll(any(ReviewSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getReviews(languageId, null, 1, 10, "id", "ASC");

        // then
        verify(reviewRepository).findAll(
                new ReviewSpecification(languageId, null),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithAnonymousUser
    void getReviews_canGetReviews_whenWithLanguageIdAndReviewValueFilter() {
        // given
        long languageId = 1;
        when(languageRepository.existsById(languageId)).thenReturn(true);
        when(reviewRepository.findAll(any(ReviewSpecification.class), any(Pageable.class)))
                .thenReturn(Page.empty(Pageable.ofSize(10)));

        // when
        underTest.getReviews(languageId, ReviewValue.LIKE, 1, 10, "id", "ASC");

        // then
        verify(reviewRepository).findAll(
                new ReviewSpecification(languageId, ReviewValue.LIKE),
                PageRequest.of(
                        0,
                        10,
                        Sort.by(SortUtils.parseDirection("ASC"), "id")
                )
        );
    }

    @Test
    @WithAnonymousUser
    void getReviews_throwsException_whenLanguageOfReviewsDoesNotExistWithId() {
        // given
        long languageId = 1;

        // when
        // then
        assertThatThrownBy(() ->
                underTest.getReviews(
                        languageId, null, 1, 10, "id", "ASC"
                ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithAnonymousUser
    void getReviewById_canGetReviewById_whenIdIsValid() {
        // given
        long id = 1;
        when(reviewRepository.findById(id)).thenReturn(Optional.of(new Review()));

        // when
        underTest.getReviewById(id);

        // then
        verify(reviewRepository).findById(id);
    }

    @Test
    @WithAnonymousUser
    void getReviewById_throwsException_whenReviewDoesNotExistWithId() {
        // given
        long id = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.getReviewById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    @WithMockUser
    void addReview_canCreateReview_whenReviewRequestDTOIsValid() {
        // given
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue, 1L);
        when(languageRepository.findById(reviewRequestDto.getLanguageId()))
                .thenReturn(Optional.of(new Language(reviewRequestDto.getLanguageId())));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(new AppUser("user@mail.com")));

        // when
        underTest.addReview(reviewRequestDto);

        // then
        ArgumentCaptor<Review> languageArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(languageArgCaptor.capture());

        Review actual = languageArgCaptor.getValue();
        assertThat(actual.getLanguage().getId()).isEqualTo(reviewRequestDto.getLanguageId());
        assertThat(actual.getAuthor().getUsername()).isEqualTo("user@mail.com");
        assertThat(actual.getBody()).isEqualTo(body);
        assertThat(actual.getReviewValue()).isEqualTo(reviewValue);
    }

    @Test
    @WithMockUser
    void addReview_throwsException_whenLanguageIdIsNull() {
        // given
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue, null);

        // when
        // then
        assertThatThrownBy(() -> underTest.addReview(reviewRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Language id");
    }

    @Test
    @WithMockUser
    void addReview_throwsException_whenLanguageOfReviewToCreateDoesNotExistWithId() {
        // given
        long languageId = 1;
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO("test", ReviewValue.LIKE, languageId);
        when(languageRepository.findById(languageId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.addReview(reviewRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(languageId));
    }

    @Test
    @WithMockUser
    void updateReview_canUpdateReview_whenAuthorWithUpdateOwnPermission() {
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
    @WithMockUser
    void updateReview_throwsException_whenAuthorWithoutUpdateOwnPermissionUpdatesReview() {
        // given
        long id = 1;
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        AppUser author = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(author);
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(id, reviewRequestDto))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void updateReview_canUpdateReview_whenUserWithUpdateOthersPermission() {
        // given
        long id = 1;
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_UPDATE_OTHERS_REVIEW)));
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        underTest.updateReview(id, reviewRequestDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());

        Review actual = reviewArgCaptor.getValue();
        assertThat(actual.getBody()).isEqualTo(body);
        assertThat(actual.getReviewValue()).isEqualTo(reviewValue);
    }

    @Test
    @WithMockUser
    void updateReview_throwsException_whenUserWithoutUpdateOthersPermissionUpdatesReview() {
        // given
        long id = 1;
        String body = "test review";
        ReviewValue reviewValue = ReviewValue.LIKE;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO(body, reviewValue);
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(id, reviewRequestDto))
                .isInstanceOf(NotEnoughPermissionException.class);
    }

    @Test
    @WithMockUser
    void anUpdateReview_throwsException_whenReviewToUpdateDoesNotExistWithId() {
        // given
        long id = 1;
        ReviewRequestDTO reviewRequestDto = new ReviewRequestDTO("test", ReviewValue.LIKE);
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateReview(id, reviewRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    @WithMockUser
    void deleteReview_canDeleteReviewById_whenUserWithDeleteOthersPermission() {
        // given
        long id = 1;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_DELETE_OTHERS_REVIEW)));
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));

        // when
        underTest.deleteReview(id);

        // then
        verify(reviewRepository).deleteById(id);
    }

    @Test
    @WithMockUser
    void deleteReview_throwsException_whenUserWithoutDeleteOthersPermission() {
        // given
        long id = 1;
        AppUser user = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        Review existingReview = new Review();
        existingReview.setAuthor(new AppUser(1));
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(id))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser
    void deleteReview_canDeleteReviewById_whenAuthorWithDeleteOwnPermission() {
        // given
        long id = 1;
        AppUser author = new AppUser("test@mail.com", new Role(Collections.singletonList(Permission.CAN_DELETE_OWN_REVIEW)));
        Review existingReview = new Review();
        existingReview.setAuthor(author);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));

        // when
        underTest.deleteReview(id);

        // then
        verify(reviewRepository).deleteById(id);
    }

    @Test
    @WithMockUser
    void deleteReview_throwsException_whenAuthorWithoutDeleteOwnPermission() {
        // given
        long id = 1;
        AppUser author = new AppUser("test@mail.com", new Role(Collections.emptyList()));
        Review existingReview = new Review();
        existingReview.setAuthor(author);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(author));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(id))
                .isInstanceOf(NotEnoughPermissionException.class);
        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    @WithMockUser
    void deleteReview_throwsException_whenReviewToDeleteDoesNotExistWithId() {
        // given
        long id = 1;

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteReview(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
    }

    @Test
    @WithMockUser
    void voteReview_canUpvoteReview_whenDidNotVoteEarlier() {
        // given
        long id = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(true);
        AppUser user = new AppUser(1);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(new Review()));

        // when
        underTest.voteReview(id, voteDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());

        Review actual = reviewArgCaptor.getValue();
        assertThat(user.getId()).isIn(actual.getUpVotes());
    }

    @Test
    @WithMockUser
    void voteReview_canDownvoteReview_whenDidNotVoteEarlier() {
        // given
        long id = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(false);
        AppUser user = new AppUser(1);
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(new Review()));

        // when
        underTest.voteReview(id, voteDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());

        Review actual = reviewArgCaptor.getValue();
        assertThat(user.getId()).isIn(actual.getDownVotes());
    }

    @Test
    @WithMockUser
    void voteReview_canChangeDownvoteToUpvoteReview_whenDownvotedEarlier() {
        // given
        long id = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(true);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getDownVotes().add(user.getId());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));

        // when
        underTest.voteReview(id, voteDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());

        Review actual = reviewArgCaptor.getValue();
        assertThat(user.getId()).isIn(actual.getUpVotes());
    }

    @Test
    @WithMockUser
    void voteReview_canChangeUpvoteToDownvoteReview_whenUpvotedEarlier() {
        // given
        long id = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(false);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getUpVotes().add(user.getId());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));

        // when
        underTest.voteReview(id, voteDto);

        // then
        ArgumentCaptor<Review> reviewArgCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository).save(reviewArgCaptor.capture());

        Review actual = reviewArgCaptor.getValue();
        assertThat(user.getId()).isIn(actual.getDownVotes());
    }

    @Test
    @WithMockUser
    void voteReview_throwsException_whenUpvotesAlreadyUpvotedReview() {
        // given
        long id = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(true);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getUpVotes().add(user.getId());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(id, voteDto))
                .isInstanceOf(ReviewDuplicateVoteException.class)
                .hasMessageContaining("up-vote");
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void voteReview_throwsException_whenDownvotesAlreadyDownvotedReview() {
        // given
        long id = 1;
        ReviewVoteRequestDTO voteDto = new ReviewVoteRequestDTO(false);
        AppUser user = new AppUser(1);
        Review existingReview = new Review();
        existingReview.getDownVotes().add(user.getId());
        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(reviewRepository.findById(id)).thenReturn(Optional.of(existingReview));

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(id, voteDto))
                .isInstanceOf(ReviewDuplicateVoteException.class)
                .hasMessageContaining("down-vote");
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void voteReview_throwsException_whenReviewToVoteDoesNotExistWithId() {
        // given
        long id = 1;
        ReviewVoteRequestDTO stateDto = new ReviewVoteRequestDTO(true);
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.voteReview(id, stateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(id));
    }
}