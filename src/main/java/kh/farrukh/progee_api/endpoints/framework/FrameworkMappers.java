package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.endpoints.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.endpoints.framework.payloads.FrameworkResponseDTO;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.LanguageMappers;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUserMappers;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;

public class FrameworkMappers {

    public static FrameworkResponseDTO toFrameworkResponseDTO(Framework framework) {
        if (framework == null) return null;
        FrameworkResponseDTO frameworkResponseDTO = new FrameworkResponseDTO();
        BeanUtils.copyProperties(framework, frameworkResponseDTO);
        frameworkResponseDTO.setAuthor(AppUserMappers.toAppUserResponseDTO(framework.getAuthor()));
        frameworkResponseDTO.setLanguage(LanguageMappers.toLanguageResponseDTO(framework.getLanguage()));
        return frameworkResponseDTO;
    }

    public static Framework toFramework(
            FrameworkRequestDTO frameworkRequestDTO,
            LanguageRepository languageRepository,
            ImageRepository imageRepository
    ) {
        if (frameworkRequestDTO == null) return null;
        Framework framework = new Framework();
        BeanUtils.copyProperties(frameworkRequestDTO, framework);
        framework.setImage(imageRepository.findById(frameworkRequestDTO.getImageId()).orElseThrow(
                () -> new ResourceNotFoundException("Image", "id", frameworkRequestDTO.getImageId())
        ));
        framework.setLanguage(languageRepository.findById(frameworkRequestDTO.getLanguageId()).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", frameworkRequestDTO.getLanguageId())
        ));
        return framework;
    }
}
