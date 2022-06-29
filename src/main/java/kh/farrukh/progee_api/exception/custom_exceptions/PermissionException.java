package kh.farrukh.progee_api.exception.custom_exceptions;

import kh.farrukh.progee_api.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.utils.constant.ExceptionMessages.EXCEPTION_PERMISSION;

@Getter
public class PermissionException extends ApiException {

    public PermissionException() {
        super(
                "You don't have enough permission",
                HttpStatus.FORBIDDEN,
                EXCEPTION_PERMISSION,
                new Object[]{}
        );
    }
}