package kh.farrukh.progee_api.global.exceptions.custom_exceptions;

import kh.farrukh.progee_api.global.exceptions.ApiException;
import kh.farrukh.progee_api.global.exceptions.ExceptionMessages;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * It's a custom exception class that extends the ApiException class that is thrown when there are
 * invalid sorting parameters in a request
 */
@Getter
@Setter
public class SortParamException extends ApiException {

    private final List<String> invalidValues;
    private final List<String> allowedValues;

    public SortParamException(List<String> invalidValues, List<String> allowedValues) {
        super(
                String.format("%s is not valid", invalidValues),
                HttpStatus.BAD_REQUEST,
                ExceptionMessages.EXCEPTION_SORT_PARAM,
                new Object[]{invalidValues, allowedValues}
        );
        this.allowedValues = allowedValues;
        this.invalidValues = invalidValues;
    }
}
