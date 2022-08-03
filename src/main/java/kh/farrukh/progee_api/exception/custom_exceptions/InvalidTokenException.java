package kh.farrukh.progee_api.exception.custom_exceptions;

import kh.farrukh.progee_api.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.utils.constant.ExceptionMessages.EXCEPTION_INVALID_TOKEN;

/**
 * `InvalidTokenException` is a subclass of `ApiException` that is thrown
 * when token is invalid
 * <p>
 * HttpStatus of the response will be UNAUTHORIZED
 */
@Getter
public class InvalidTokenException extends ApiException {

    public InvalidTokenException() {
        super(
                "Token is invalid",
                HttpStatus.FORBIDDEN,
                EXCEPTION_INVALID_TOKEN,
                new Object[]{}
        );
    }
}
