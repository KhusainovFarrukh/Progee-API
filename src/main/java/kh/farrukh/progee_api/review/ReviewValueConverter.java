package kh.farrukh.progee_api.review;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * It converts a ReviewValue enum to an integer value when saving to the database, and converts an integer value to a
 * ReviewValue enum when reading from the database (using its score)
 */
@Converter(autoApply = true)
public class ReviewValueConverter implements AttributeConverter<ReviewValue, Integer> {

    public static final ReviewValue DEFAULT_REVIEW_VALUE = ReviewValue.DONT_HAVE_PRACTICE;
    public static final Integer DEFAULT_SCORE = DEFAULT_REVIEW_VALUE.getScore();

    @Override
    public Integer convertToDatabaseColumn(ReviewValue reviewValue) {
        if (reviewValue == null) {
            return DEFAULT_SCORE;
        }
        return reviewValue.getScore();
    }

    @Override
    public ReviewValue convertToEntityAttribute(Integer intValue) {
        if (intValue == null) {
            return DEFAULT_REVIEW_VALUE;
        }
        return ReviewValue.fromIntValue(intValue);
    }
}
