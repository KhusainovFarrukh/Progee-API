package kh.farrukh.progee_api.global.exceptions.custom_exceptions.token_exceptions;

import kh.farrukh.progee_api.global.exceptions.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.global.exceptions.ExceptionMessages.EXCEPTION_TOKEN_UNKNOWN;

/**
 * `UnknownTokenException` is a subclass of `ApiException` that is thrown
 * when there is unknown problem with token
 * <p>
 * HttpStatus of the response will be FORBIDDEN
 */
@Getter
public class UnknownTokenException extends ApiException {

    public UnknownTokenException() {
        super(
                "Token is invalid",
                HttpStatus.FORBIDDEN,
                EXCEPTION_TOKEN_UNKNOWN,
                new Object[]{}
        );
    }
}