package kh.farrukh.progee_api.exceptions.custom_exceptions;

import kh.farrukh.progee_api.exceptions.ApiException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.exceptions.ExceptionMessages.EXCEPTION_EMAIL_PASSWORD_WRONG;

/**
 * `EmailPasswordInvalidException` is a subclass of `ApiException` that is thrown
 * when email or password provided in login request is invalid
 * <p>
 * HttpStatus of the response will be UNAUTHORIZED
 */
@Getter
public class EmailPasswordWrongException extends ApiException {

    private final Type errorType;

    public EmailPasswordWrongException(Type errorType) {
        super(
                String.format("%s is wrong", errorType.errorName),
                HttpStatus.UNAUTHORIZED,
                EXCEPTION_EMAIL_PASSWORD_WRONG,
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