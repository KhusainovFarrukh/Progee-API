package kh.farrukh.progee_api.test_utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import kh.farrukh.progee_api.endpoints.review.ReviewValue;
import kh.farrukh.progee_api.endpoints.review.ReviewValueConverter;

import java.io.IOException;

public class ReviewValueDeserializer extends StdDeserializer<ReviewValue> {
    public ReviewValueDeserializer() {
        super(ReviewValue.class);
    }

    @Override
    public ReviewValue deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
        switch (jsonParser.getIntValue()) {
            case -1:
                return ReviewValue.DISLIKE;
            case 0:
                return ReviewValue.DONT_HAVE_PRACTICE;
            case 1:
                return ReviewValue.WANT_TO_LEARN;
            case 2:
                return ReviewValue.LIKE;
            default:
                return ReviewValueConverter.DEFAULT_REVIEW_VALUE;
        }
    }
}
