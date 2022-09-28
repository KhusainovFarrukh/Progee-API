package kh.farrukh.progee_api.global.exceptions.custom_exceptions;

import kh.farrukh.progee_api.global.exceptions.ApiException;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.global.exceptions.ExceptionMessages.EXCEPTION_DEFAULT_ROLE_DELETION;

/**
 * `DefaultRoleDeletionException` is a custom exception class that extends `ApiException` and is thrown when a user tries
 * to delete the single default role
 */
public class DefaultRoleDeletionException extends ApiException {

    public DefaultRoleDeletionException() {
        super(
                "Default role cannot be deleted",
                HttpStatus.BAD_REQUEST,
                EXCEPTION_DEFAULT_ROLE_DELETION,
                new Object[]{}
        );
    }
}
