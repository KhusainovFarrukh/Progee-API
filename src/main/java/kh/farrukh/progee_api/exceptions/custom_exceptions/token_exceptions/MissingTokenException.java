package kh.farrukh.progee_api.exceptions.custom_exceptions.token_exceptions;

import kh.farrukh.progee_api.exceptions.ApiException;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.exceptions.ExceptionMessages.EXCEPTION_TOKEN_MISSING;

public class MissingTokenException extends ApiException {

    public MissingTokenException() {
        super(
                "Token is missing",
                HttpStatus.FORBIDDEN,
                EXCEPTION_TOKEN_MISSING,
                new Object[]{}
        );
    }
}
