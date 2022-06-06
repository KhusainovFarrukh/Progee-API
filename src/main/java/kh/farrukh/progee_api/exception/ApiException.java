package kh.farrukh.progee_api.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiException extends RuntimeException {

    private HttpStatus httpStatus;
    private String messageId;
    private Object[] messageArgs;

    public ApiException(String message, Throwable cause, HttpStatus httpStatus, String messageId, Object[] messageArgs) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.messageId = messageId;
        this.messageArgs = messageArgs;
    }

    public ApiException(String message, HttpStatus httpStatus, String messageId, Object[] messageArgs) {
        super(message);
        this.httpStatus = httpStatus;
        this.messageId = messageId;
        this.messageArgs = messageArgs;
    }
}
