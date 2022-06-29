package kh.farrukh.progee_api.exception.custom_exceptions;

import kh.farrukh.progee_api.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.utils.constant.ExceptionMessages.EXCEPTION_BAD_REQUEST;

/**
 * `BadRequestException` is a subclass of `ApiException` that is thrown
 * when a required value is not valid
 *
 * HttpStatus of the response will be BAD_REQUEST
 */
@Getter
public class BadRequestException extends ApiException {

    private final String invalidValue;

    public BadRequestException(String invalidValue) {
        super(
                String.format("%s is not valid", invalidValue),
                HttpStatus.BAD_REQUEST,
                EXCEPTION_BAD_REQUEST,
                new Object[]{invalidValue}
        );
        this.invalidValue = invalidValue;
    }
}
