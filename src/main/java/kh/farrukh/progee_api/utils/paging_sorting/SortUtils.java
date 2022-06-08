package kh.farrukh.progee_api.utils.paging_sorting;

import org.springframework.data.domain.Sort;

import java.util.Locale;

public class SortUtils {

    public static Sort.Direction parseDirection(String value) {

        try {
            return Sort.Direction.valueOf(value.toUpperCase(Locale.US));
        } catch (Exception e) {
            return Sort.Direction.ASC;
        }
    }
}
