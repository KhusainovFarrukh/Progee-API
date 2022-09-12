package kh.farrukh.progee_api.global.utils.file;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates a file.
 */
public class NotEmptyFileValidator implements ConstraintValidator<NotEmptyFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        return value != null && !value.isEmpty();
    }
}
