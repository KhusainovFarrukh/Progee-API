package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.review.payloads.ReviewRequestDTO;
import kh.farrukh.progee_api.review.payloads.ReviewResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewMappersTest {

    @Mock
    private LanguageRepository languageRepository;

    @Test
    void toReviewResponseDTO_returnsNull_whenReviewIsNull() {
        // given
        Review review = null;

        // when
        ReviewResponseDTO actual = ReviewMappers.toReviewResponseDTO(review);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toReviewResponseDTO_canMap_whenReviewIsValid() {
        // given
        Review review = new Review(
                1,
                "Test",
                ReviewValue.LIKE,
                Set.of(1L, 2L, 3L),
                Set.of(4L),
                2,
                new Language(1),
                new AppUser(1),
                ZonedDateTime.now()
        );

        // when
        ReviewResponseDTO actual = ReviewMappers.toReviewResponseDTO(review);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(review.getId());
        assertThat(actual.getBody()).isEqualTo(review.getBody());
        assertThat(actual.getReviewValue()).isEqualTo(review.getReviewValue());
        assertThat(actual.getUpVotes()).isEqualTo(review.getUpVotes());
        assertThat(actual.getDownVotes()).isEqualTo(review.getDownVotes());
        assertThat(actual.getScore()).isEqualTo(review.getScore());
        assertThat(actual.getLanguage().getId()).isEqualTo(review.getLanguage().getId());
        assertThat(actual.getAuthor().getId()).isEqualTo(review.getAuthor().getId());
        assertThat(actual.getCreatedAt()).isEqualTo(review.getCreatedAt());
    }

    @Test
    void toReview_returnsNull_whenReviewRequestDTOIsNull() {
        // given
        ReviewRequestDTO reviewRequestDTO = null;

        // when
        Review actual = ReviewMappers.toReview(reviewRequestDTO, languageRepository);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void toReview_canMap_whenReviewRequestDTOIsValid() {
        // given
        when(languageRepository.findById(any())).thenReturn(Optional.of(new Language(1)));
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO(
                "Test",
                ReviewValue.LIKE,
                1L
        );

        // when
        Review actual = ReviewMappers.toReview(reviewRequestDTO, languageRepository);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getBody()).isEqualTo(reviewRequestDTO.getBody());
        assertThat(actual.getReviewValue()).isEqualTo(reviewRequestDTO.getReviewValue());
        assertThat(actual.getLanguage().getId()).isEqualTo(reviewRequestDTO.getLanguageId());
    }

    @Test
    void toReview_throwsException_whenLanguageDoesNotExist() {
        // given
        when(languageRepository.findById(any())).thenReturn(Optional.empty());
        ReviewRequestDTO reviewRequestDTO = new ReviewRequestDTO(
                "Test",
                ReviewValue.LIKE,
                1L
        );

        // when
        // then
        assertThatThrownBy(() -> ReviewMappers.toReview(reviewRequestDTO, languageRepository))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Language")
                .hasMessageContaining("id")
                .hasMessageContaining(String.valueOf(reviewRequestDTO.getLanguageId()));
    }
}