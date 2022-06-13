package kh.farrukh.progee_api.exception.custom_exceptions;

import kh.farrukh.progee_api.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.progee_api.utils.constant.ExceptionMessages.EXCEPTION_BAD_REQUEST;

@Getter
public class BadRequestException extends ApiException {

    private final String requiredValue;

    public BadRequestException(String requiredValue) {
        super(
                String.format("%s is not valid", requiredValue),
                HttpStatus.BAD_REQUEST,
                EXCEPTION_BAD_REQUEST,
                new Object[]{requiredValue}
        );
        this.requiredValue = requiredValue;
    }
}
