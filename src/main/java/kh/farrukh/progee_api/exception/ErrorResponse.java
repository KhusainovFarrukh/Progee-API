package kh.farrukh.progee_api.exception;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("http_status")
    private HttpStatus httpStatus;
    @JsonProperty("http_code")
    private int httpCode;
    private ZonedDateTime timestamp;
}
