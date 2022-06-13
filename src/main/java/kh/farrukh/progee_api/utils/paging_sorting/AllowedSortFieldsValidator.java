package kh.farrukh.progee_api.utils.paging_sorting;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collection;

import static java.util.stream.Collectors.joining;

/**
 * Validates a list of sort fields within a Pageable against an allowed list.
 * Allowed list comes from AllowedSortFields annotation values
 */
public class AllowedSortFieldsValidator implements ConstraintValidator<AllowedSortFields, Pageable> {

    private Collection<String> allowedSortFields;

    // TODO: 6/12/22 add i18n
    static final String ERROR_MESSAGE = "The following sort fields [%s] are not within the allowed fields. "
            + "Allowed sort fields are: [%s]";

    @Override
    public void initialize(AllowedSortFields constraintAnnotation) {
        allowedSortFields = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(Pageable value, ConstraintValidatorContext context) {
        // if there is no Pageable or there is no sort params, then it is valid request
        if (value == null || CollectionUtils.isEmpty(allowedSortFields)) {
            return true;
        }

        // if there Pageable is unsorted, then it is valid request
        Sort sort = value.getSort();
        if (sort.isUnsorted()) {
            return true;
        }

        String notAllowedFields = getNotAllowedFieldsFromSort(sort);

        // if all found fields are allowed, then it is valid request
        if (notAllowedFields == null || notAllowedFields.length() == 0) {
            return true;
        }

        // else it is not valid request
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                String.format(
                        ERROR_MESSAGE,
                        notAllowedFields,
                        String.join(",", allowedSortFields)
                )).addConstraintViolation();
        return false;
    }

    /**
     * It takes a Sort object, and returns all not allowed fields used in it
     *
     * @param sort The sort object that was passed in by the user.
     * @return A string of all the fields that are not allowed to be sorted on.
     */
    private String getNotAllowedFieldsFromSort(Sort sort) {
        return sort.stream()
                .map(Sort.Order::getProperty)
                .filter(property -> !allowedSortFields.contains(property))
                .collect(joining(","));
    }
}