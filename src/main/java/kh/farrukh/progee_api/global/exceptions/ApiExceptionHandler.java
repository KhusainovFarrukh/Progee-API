package kh.farrukh.progee_api.global.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static kh.farrukh.progee_api.global.exceptions.ExceptionMessages.EXCEPTION_METHOD_ARGUMENT_NOT_VALID;

/**
 * It handles exceptions thrown by the application
 * and returns a response with a message and a status code
 */
@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final MessageSource messageSource;

    /**
     * It handles all custom exceptions
     *
     * @param exception The exception object that was thrown.
     * @param locale    The locale of the user.
     * @return A ResponseEntity<Object>
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException exception, Locale locale) {
        exception.printStackTrace();
        return new ResponseEntity<>(
                new ErrorResponse(
                        messageSource.getMessage(
                                exception.getMessageId(),
                                exception.getMessageArgs(),
                                locale
                        ),
                        exception.getHttpStatus(),
                        exception.getHttpStatus().value(),
                        ZonedDateTime.now()
                ),
                exception.getHttpStatus()
        );
    }

    /**
     * It handles MethodArgumentNotValidException exceptions from Validation
     *
     * @param exception The exception object that was thrown.
     * @return A ResponseEntity<Object>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgNotValidException(MethodArgumentNotValidException exception, Locale locale) {
        exception.printStackTrace();
        Map<String, Object> errorsMap = new HashMap<>();
        List<FieldError> errors = exception.getFieldErrors();
        for (FieldError error : errors) {
            errorsMap.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(
                new ErrorResponse(
                        messageSource.getMessage(EXCEPTION_METHOD_ARGUMENT_NOT_VALID, null, locale),
                        HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.value(),
                        ZonedDateTime.now(),
                        errorsMap
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * It handles all other unknown exceptions (not handled exceptions)
     *
     * @param exception The exception object that was thrown.
     * @return A ResponseEntity<Object>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception exception, Locale locale) {
        exception.printStackTrace();
        return new ResponseEntity<>(
                new ErrorResponse(
                        messageSource.getMessage(
                                ExceptionMessages.EXCEPTION_UNKNOWN,
                                null,
                                locale
                        ) + ": " + exception.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ZonedDateTime.now()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
