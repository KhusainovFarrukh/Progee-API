package kh.farrukh.progee_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException exception) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        exception.getMessage(),
//                        exception,
                        exception.getHttpStatus(),
                        exception.getHttpStatus().value(),
                        ZonedDateTime.now()
                ),
                exception.getHttpStatus()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception exception) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        exception.getMessage(),
//                        exception,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ZonedDateTime.now()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
