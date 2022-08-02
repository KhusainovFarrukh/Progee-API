package kh.farrukh.progee_api.exception.custom_exceptions;

import kh.farrukh.progee_api.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.utils.constant.ExceptionMessages.EXCEPTION_INVALID_CREDENTIALS;

/**
 * `InvalidCredentialsException` is a subclass of `ApiException` that is thrown
 * when token or email/password is invalid
 * <p>
 * HttpStatus of the response will be UNAUTHORIZED
 * <p>
 * todo project needs separate exceptions for invalid token and invalid email/password (EmailPasswordInvalidException: ready)
 *      see todos in EmailPasswordAuthenticationEntryPoint
 */
@Getter
public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException() {
        super(
                "Auth credentials are invalid",
                HttpStatus.UNAUTHORIZED,
                EXCEPTION_INVALID_CREDENTIALS,
                new Object[]{}
        );
    }
}
