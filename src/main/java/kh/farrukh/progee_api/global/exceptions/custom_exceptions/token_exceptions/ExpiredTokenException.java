package kh.farrukh.progee_api.global.exceptions.custom_exceptions.token_exceptions;

import kh.farrukh.progee_api.global.exceptions.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.global.exceptions.ExceptionMessages.EXCEPTION_TOKEN_EXPIRED;

/**
 * `ExpiredTokenException` is a subclass of `ApiException` that is thrown
 * when expired token is used
 * <p>
 * HttpStatus of the response will be FORBIDDEN
 */
@Getter
public class ExpiredTokenException extends ApiException {

    public ExpiredTokenException() {
        super(
                "Token is expired",
                HttpStatus.FORBIDDEN,
                EXCEPTION_TOKEN_EXPIRED,
                new Object[]{}
        );
    }
}
