package kh.farrukh.progee_api.test_utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import kh.farrukh.progee_api.review.ReviewValue;
import kh.farrukh.progee_api.review.ReviewValueConverter;

import java.io.IOException;

public class ReviewValueDeserializer extends StdDeserializer<ReviewValue> {
    public ReviewValueDeserializer() {
        super(ReviewValue.class);
    }

    @Override
    public ReviewValue deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        return switch (jsonParser.getIntValue()) {
            case -1 -> ReviewValue.DISLIKE;
            case 0 -> ReviewValue.DONT_HAVE_PRACTICE;
            case 1 -> ReviewValue.WANT_TO_LEARN;
            case 2 -> ReviewValue.LIKE;
            default -> ReviewValueConverter.DEFAULT_REVIEW_VALUE;
        };
    }
}
