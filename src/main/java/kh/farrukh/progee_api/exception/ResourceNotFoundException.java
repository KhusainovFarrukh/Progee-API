package kh.farrukh.progee_api.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import kh.farrukh.progee_api.utils.constant.ExceptionMessages;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonIgnoreProperties
public class ResourceNotFoundException extends ApiException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND,
                ExceptionMessages.EXCEPTION_RESOURCE_NOT_FOUND,
                new Object[]{resourceName, fieldName, fieldValue}
        );
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}