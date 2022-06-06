package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewValue {
    DISLIKE(-1),
    DONT_HAVE_PRACTICE(0),
    WANT_TO_LEARN(1),
    LIKE(2);

    private final int value;

    @JsonCreator
    public static ReviewValue fromIntValue(int intValue) {
        switch (intValue) {
            case -1:
                return ReviewValue.DISLIKE;
            case 0:
                return ReviewValue.DONT_HAVE_PRACTICE;
            case 1:
                return ReviewValue.WANT_TO_LEARN;
            case 2:
                return ReviewValue.LIKE;
            default:
                throw new IllegalStateException("Unexpected value: " + intValue);
        }
    }
}