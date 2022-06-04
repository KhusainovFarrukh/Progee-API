package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ReviewValue {
    DISLIKE(-1),
    DONT_HAVE_PRACTICE(0),
    WANT_TO_LEARN(1),
    LIKE(2);

    private int value;

    ReviewValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

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