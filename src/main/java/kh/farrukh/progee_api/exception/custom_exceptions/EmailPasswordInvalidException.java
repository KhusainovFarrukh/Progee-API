package kh.farrukh.progee_api.exception.custom_exceptions;

import kh.farrukh.progee_api.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.utils.constants.ExceptionMessages.EXCEPTION_EMAIL_PASSWORD_INVALID;

/**
 * `EmailPasswordInvalidException` is a subclass of `ApiException` that is thrown
 * when email or password provided in login request is invalid
 * <p>
 * HttpStatus of the response will be UNAUTHORIZED
 */
@Getter
public class EmailPasswordInvalidException extends ApiException {

    private final Type errorType;

    public EmailPasswordInvalidException(Type errorType) {
        super(
                String.format("%s is invalid", errorType.errorName),
                HttpStatus.UNAUTHORIZED,
                EXCEPTION_EMAIL_PASSWORD_INVALID,
                new Object[]{errorType.errorName}
        );
        this.errorType = errorType;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum Type {
        EMAIL("Email"),
        PASSWORD("Password");

        private String errorName;
    }
}