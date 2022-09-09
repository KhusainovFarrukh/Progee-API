package kh.farrukh.progee_api.exceptions.custom_exceptions;

import kh.farrukh.progee_api.exceptions.ApiException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static kh.farrukh.progee_api.exceptions.ExceptionMessages.EXCEPTION_SORT_PARAM;

@Getter
@Setter
public class SortParamException extends ApiException {

    private final List<String> invalidValues;
    private final List<String> allowedValues;

    public SortParamException(List<String> invalidValues, List<String> allowedValues) {
        super(
                String.format("%s is not valid", invalidValues),
                HttpStatus.BAD_REQUEST,
                EXCEPTION_SORT_PARAM,
                new Object[]{invalidValues, allowedValues}
        );
        this.allowedValues = allowedValues;
        this.invalidValues = invalidValues;
    }
}