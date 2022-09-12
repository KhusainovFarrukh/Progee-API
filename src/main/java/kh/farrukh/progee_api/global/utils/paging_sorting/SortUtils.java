package kh.farrukh.progee_api.global.utils.paging_sorting;

import org.springframework.data.domain.Sort;

import java.util.Locale;

/**
 * It's a utility class with methods for sorting helper logic
 */
public class SortUtils {

    /**
     * If the value is not a valid direction, then return the default direction,
     * else parse direction from string value
     *
     * @param value The value to parse.
     * @return The Sort.Direction.valueOf(value.toUpperCase(Locale.US)) is being returned.
     */
    public static Sort.Direction parseDirection(String value) {
        try {
            return Sort.Direction.valueOf(value.toUpperCase(Locale.US));
        } catch (Exception e) {
            return Sort.Direction.ASC;
        }
    }
}
