package kh.farrukh.progee_api.exception;

import kh.farrukh.progee_api.utils.constant.ExceptionMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;
import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException exception, Locale locale) {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception exception, Locale locale) {
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
