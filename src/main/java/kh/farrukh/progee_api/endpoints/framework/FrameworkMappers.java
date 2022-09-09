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

    public static Framework toFramework(FrameworkResponseDTO frameworkResponseDTO) {
        if (frameworkResponseDTO == null) return null;
        Framework framework = new Framework();
        BeanUtils.copyProperties(frameworkResponseDTO, framework);
        framework.setAuthor(AppUserMappers.toAppUser(frameworkResponseDTO.getAuthor()));
        return framework;
    }
}
