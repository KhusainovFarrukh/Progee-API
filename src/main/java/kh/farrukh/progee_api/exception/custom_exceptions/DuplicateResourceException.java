package kh.farrukh.progee_api.exception.custom_exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import kh.farrukh.progee_api.exception.ApiException;
import kh.farrukh.progee_api.utils.constants.ExceptionMessages;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * It's a custom exception class that extends the ApiException class,
 * and thrown when there is already another resource with some unique field
 *
 * HttpStatus of the response will be BAD_REQUEST
 */
@Getter
@JsonIgnoreProperties
public class DuplicateResourceException extends ApiException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s is already exists with %s : '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.BAD_REQUEST,
                ExceptionMessages.EXCEPTION_DUPLICATE_RESOURCE,
                new Object[]{resourceName, fieldName, fieldValue}
        );
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}