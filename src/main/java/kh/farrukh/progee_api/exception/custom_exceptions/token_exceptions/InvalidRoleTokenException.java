package kh.farrukh.progee_api.exception.custom_exceptions.token_exceptions;

import kh.farrukh.progee_api.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.utils.constants.ExceptionMessages.EXCEPTION_TOKEN_INVALID_ROLE;

/**
 * `InvalidRoleTokenException` is a subclass of `ApiException` that is thrown
 * when there is no valid role in token claims
 * <p>
 * HttpStatus of the response will be FORBIDDEN
 */
@Getter
public class InvalidRoleTokenException extends ApiException {

    public InvalidRoleTokenException() {
        super(
                "Token does not contain valid role claim",
                HttpStatus.FORBIDDEN,
                EXCEPTION_TOKEN_INVALID_ROLE,
                new Object[]{}
        );
    }
}