package kh.farrukh.progee_api.exception.custom_exceptions;

import kh.farrukh.progee_api.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.utils.constant.ExceptionMessages.EXCEPTION_BAD_REQUEST;

/**
 * `EndpointAccessDeniedException` is a subclass of `ApiException` that is thrown
 * when a user doesn't have enough permission/role to access the endpoint
 *
 * HttpStatus of the response will be FORBIDDEN
 */
@Getter
public class EndpointAccessDeniedException extends ApiException {

    private final String endpoint;

    public EndpointAccessDeniedException(String endpoint) {
        super(
                String.format("You don't have access to %s", endpoint),
                HttpStatus.FORBIDDEN,
                EXCEPTION_BAD_REQUEST,
                new Object[]{endpoint}
        );
        this.endpoint = endpoint;
    }
}