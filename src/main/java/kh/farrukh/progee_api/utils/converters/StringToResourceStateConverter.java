package kh.farrukh.progee_api.utils.converters;

import kh.farrukh.progee_api.global.entity.ResourceState;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * It converts a String to a ResourceState enum
 * User when ResourceStateDTO comes in RequestBody
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
