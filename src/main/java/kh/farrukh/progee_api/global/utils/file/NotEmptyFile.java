package kh.farrukh.progee_api.global.utils.file;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Custom annotation to validate file
 */
@Documented
@Constraint(validatedBy = {NotEmptyFileValidator.class})
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface NotEmptyFile {

    String message() default "File must not be empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}