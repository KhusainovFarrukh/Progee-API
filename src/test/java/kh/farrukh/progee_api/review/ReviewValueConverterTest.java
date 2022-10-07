package kh.farrukh.progee_api.review;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReviewValueConverterTest {

    private static ReviewValueConverter underTest;

    @BeforeAll
    static void setUp() {
        underTest = new ReviewValueConverter();
    }

    @Test
    void convertToDatabaseColumn_returnsScore_whenReviewValueIsValid() {
        // given
        ReviewValue reviewValue = ReviewValue.LIKE;

        // when
        Integer actual = underTest.convertToDatabaseColumn(reviewValue);

        // then
        assertThat(actual).isEqualTo(reviewValue.getScore());
    }

    @Test
    void convertToDatabaseColumn_returnsDefaultScore_whenReviewValueIsNull() {
        // given
        ReviewValue reviewValue = null;

        // when
        Integer actual = underTest.convertToDatabaseColumn(reviewValue);

        // then
        assertThat(actual).isEqualTo(ReviewValueConverter.DEFAULT_SCORE);
    }

    @Test
    void convertToEntityAttribute_returnsReviewValue_whenScoreIsValid() {
        // given
        ReviewValue reviewValue = ReviewValue.LIKE;
        Integer score = reviewValue.getScore();

        // when
        ReviewValue actual = underTest.convertToEntityAttribute(score);

        // then
        assertThat(actual).isEqualTo(reviewValue);
    }

    @Test
    void convertToEntityAttribute_returnsDefaultReviewValue_whenScoreIsInvalid() {
        // given
        Integer score = 99;

        // when
        ReviewValue actual = underTest.convertToEntityAttribute(score);

        // then
        assertThat(actual).isEqualTo(ReviewValueConverter.DEFAULT_REVIEW_VALUE);
    }

    @Test
    void convertToEntityAttribute_returnsDefaultReviewValue_whenScoreIsNull() {
        // given
        Integer score = null;

        // when
        ReviewValue actual = underTest.convertToEntityAttribute(score);

        // then
        assertThat(actual).isEqualTo(ReviewValueConverter.DEFAULT_REVIEW_VALUE);
    }
}