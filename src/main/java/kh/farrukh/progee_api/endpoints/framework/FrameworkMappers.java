package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.endpoints.framework.payloads.FrameworkResponseDTO;
import kh.farrukh.progee_api.endpoints.user.AppUserMappers;
import org.springframework.beans.BeanUtils;

public class FrameworkMappers {

    public static FrameworkResponseDTO toFrameworkResponseDTO(Framework framework) {
        if (framework == null) return null;
        FrameworkResponseDTO frameworkResponseDTO = new FrameworkResponseDTO();
        BeanUtils.copyProperties(framework, frameworkResponseDTO);
        frameworkResponseDTO.setAuthor(AppUserMappers.toAppUserResponseDTO(framework.getAuthor()));
        return frameworkResponseDTO;
    }
}
