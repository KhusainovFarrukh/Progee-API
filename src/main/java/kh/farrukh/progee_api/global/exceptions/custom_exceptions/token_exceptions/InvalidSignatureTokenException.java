package kh.farrukh.progee_api.global.exceptions.custom_exceptions.token_exceptions;

import kh.farrukh.progee_api.global.exceptions.ApiException;
import kh.farrukh.progee_api.global.exceptions.ExceptionMessages;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * `InvalidSignatureTokenException` is a subclass of `ApiException` that is thrown
 * when signature of token is not valid
 * <p>
 * HttpStatus of the response will be FORBIDDEN
 */
@Getter
public class InvalidSignatureTokenException extends ApiException {

    public InvalidSignatureTokenException() {
        super(
                "Signature of token is not valid",
                HttpStatus.FORBIDDEN,
                ExceptionMessages.EXCEPTION_TOKEN_INVALID_SIGNATURE,
                new Object[]{}
        );
    }
}
