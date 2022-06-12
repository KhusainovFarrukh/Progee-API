package kh.farrukh.progee_api.utils.sorting;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = {AllowedSortFieldsValidator.class})
@Target({ANNOTATION_TYPE, TYPE, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface AllowedSortFields {

    String message() default "Sort field values provided are not within the allowed fields that are sortable.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Specify an array of fields that are allowed to use when sorting via Pageable.
     *
     * @return the allowed sort fields
     */
    String[] value() default {};

}
