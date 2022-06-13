package kh.farrukh.progee_api.endpoints.review;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * It converts a ReviewValue enum to an integer value when saving to the database, and converts an integer value to a
 * ReviewValue enum when reading from the database (using its score)
 */
@Converter(autoApply = true)
public class ReviewValueConverter implements AttributeConverter<ReviewValue, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReviewValue reviewValue) {
        return reviewValue.getScore();
    }

    @Override
    public ReviewValue convertToEntityAttribute(Integer intValue) {
        return ReviewValue.fromIntValue(intValue);
    }
}
