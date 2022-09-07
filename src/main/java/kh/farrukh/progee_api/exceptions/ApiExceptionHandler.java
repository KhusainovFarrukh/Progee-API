package kh.farrukh.progee_api.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.*;

import static kh.farrukh.progee_api.exceptions.ExceptionMessages.EXCEPTION_METHOD_ARGUMENT_NOT_VALID;

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
//                        exception,
                        exception.getHttpStatus(),
                        exception.getHttpStatus().value(),
                        ZonedDateTime.now()
                ),
                exception.getHttpStatus()
        );
    }

    /**
     * It handles exceptions from AllowedSortFields annotation
     *
     * @param exception The exception object that was thrown.
     * @return A ResponseEntity<Object>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleSortParamValidationException(ConstraintViolationException exception) {
        exception.printStackTrace();
        String errorMessage = new ArrayList<>(exception.getConstraintViolations()).get(0).getMessage();
        return new ResponseEntity<>(
                new ErrorResponse(
                        errorMessage,
                        HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.value(),
                        ZonedDateTime.now()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * It handles exceptions from Valid annotation on the RequestBody
     *
     * @param exception The exception object that was thrown.
     * @return A ResponseEntity<Object>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleRequestBodyValidationException(MethodArgumentNotValidException exception, Locale locale) {
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
     * It handles all other exceptions (not handled exceptions)
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
//                        exception,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ZonedDateTime.now()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
