package kh.farrukh.progee_api.endpoints.review;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ReviewValueConverter implements AttributeConverter<ReviewValue, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReviewValue reviewValue) {
        return reviewValue.getValue();
    }

    @Override
    public ReviewValue convertToEntityAttribute(Integer intValue) {
        return ReviewValue.fromIntValue(intValue);
    }
}
