package kh.farrukh.progee_api.test_utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import kh.farrukh.progee_api.review.ReviewValue;

import java.io.IOException;

public class ReviewValueSerializer extends StdSerializer<ReviewValue> {

    public ReviewValueSerializer() {
        super(ReviewValue.class);
    }

    @Override
    public void serialize(ReviewValue value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeNumber(value.getScore());
    }
}
