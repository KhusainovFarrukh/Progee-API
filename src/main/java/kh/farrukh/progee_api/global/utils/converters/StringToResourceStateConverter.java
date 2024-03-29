package kh.farrukh.progee_api.global.utils.converters;

import kh.farrukh.progee_api.global.resource_state.ResourceState;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * It converts a String to a ResourceState enum
 */
@ControllerAdvice
public class StringToResourceStateConverter implements Converter<String, ResourceState> {

    @Override
    public ResourceState convert(String source) {
        try {
            return ResourceState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
