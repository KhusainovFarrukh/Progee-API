package kh.farrukh.progee_api.review;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum class for using as review type.
 * Integer value is score of review to this language/framework
 * <p>
 * DISLIKE - don't like this language/framework
 * DONT_HAVE_PRACTICE - not used yet
 * WANT_TO_LEARN - not used yet, but want to learn
 * LIKE - like this language/framework
 */
@Getter
@AllArgsConstructor
public enum ReviewValue {
    DISLIKE(-1),
    DONT_HAVE_PRACTICE(0),
    WANT_TO_LEARN(1),
    LIKE(2);

    private final int score;

    /**
     * Json converter for converting int value in request to ReviewValue by its score
     *
     * @param intValue The integer value of the enum.
     * @return The ReviewValue enum is being returned.
     */
    @JsonCreator
    public static ReviewValue fromIntValue(int intValue) {
        return switch (intValue) {
            case -1 -> ReviewValue.DISLIKE;
            case 0 -> ReviewValue.DONT_HAVE_PRACTICE;
            case 1 -> ReviewValue.WANT_TO_LEARN;
            case 2 -> ReviewValue.LIKE;
            default -> ReviewValueConverter.DEFAULT_REVIEW_VALUE;
        };
    }
}