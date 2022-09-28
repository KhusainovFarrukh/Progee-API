package kh.farrukh.progee_api.global.exceptions.custom_exceptions;

import kh.farrukh.progee_api.global.exceptions.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.global.exceptions.ExceptionMessages.EXCEPTION_NOT_ENOUGH_PERMISSION;

/**
 * `NotEnoughPermissionException` is a subclass of `ApiException` that is thrown when the user doesn't have enough
 * permission to perform the requested action
 */
@Getter
public class NotEnoughPermissionException extends ApiException {

    public NotEnoughPermissionException() {
        super(
                "You don't have enough permission",
                HttpStatus.FORBIDDEN,
                EXCEPTION_NOT_ENOUGH_PERMISSION,
                new Object[]{}
        );
    }
}