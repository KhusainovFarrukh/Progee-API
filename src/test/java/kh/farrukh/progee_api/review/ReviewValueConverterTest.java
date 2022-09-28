package kh.farrukh.progee_api.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReviewValueConverterTest {

    private ReviewValueConverter underTest;

    @BeforeEach
    void setUp() {
        underTest = new ReviewValueConverter();
    }

    @Test
    void returnsScoreIfReviewValueIsValid() {
        // given
        ReviewValue reviewValue = ReviewValue.LIKE;

        // when
        Integer actual = underTest.convertToDatabaseColumn(reviewValue);

        // then
        assertThat(actual).isEqualTo(reviewValue.getScore());
    }

    @Test
    void returnsDefaultScoreIfReviewValueIsNull() {
        // given
        ReviewValue reviewValue = null;

        // when
        Integer actual = underTest.convertToDatabaseColumn(reviewValue);

        // then
        assertThat(actual).isEqualTo(ReviewValueConverter.DEFAULT_SCORE);
    }

    @Test
    void returnsReviewValueIfScoreIsValid() {
        // given
        ReviewValue reviewValue = ReviewValue.LIKE;
        Integer score = reviewValue.getScore();

        // when
        ReviewValue actual = underTest.convertToEntityAttribute(score);

        // then
        assertThat(actual).isEqualTo(reviewValue);
    }

    @Test
    void returnsDefaultReviewValueIfScoreIsInvalid() {
        // given
        Integer score = 99;

        // when
        ReviewValue actual = underTest.convertToEntityAttribute(score);

        // then
        assertThat(actual).isEqualTo(ReviewValueConverter.DEFAULT_REVIEW_VALUE);
    }

    @Test
    void returnsDefaultReviewValueIfScoreIsNull() {
        // given
        Integer score = null;

        // when
        ReviewValue actual = underTest.convertToEntityAttribute(score);

        // then
        assertThat(actual).isEqualTo(ReviewValueConverter.DEFAULT_REVIEW_VALUE);
    }
}