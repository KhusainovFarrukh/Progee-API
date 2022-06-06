package kh.farrukh.progee_api.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private String message;
//    private Throwable throwable;
    private HttpStatus httpStatus;
    private int httpCode;
    private ZonedDateTime timestamp;
}
