package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.endpoints.image.ImageMappers;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.payloads.LanguageRequestDTO;
import kh.farrukh.progee_api.endpoints.language.payloads.LanguageResponseDTO;
import kh.farrukh.progee_api.endpoints.user.AppUserMappers;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;

public class LanguageMappers {

    public static LanguageResponseDTO toLanguageResponseDTO(Language language) {
        if (language == null) return null;
        LanguageResponseDTO languageResponseDTO = new LanguageResponseDTO();
        BeanUtils.copyProperties(language, languageResponseDTO);
        languageResponseDTO.setAuthor(AppUserMappers.toAppUserResponseDTO(language.getAuthor()));
        languageResponseDTO.setImage(ImageMappers.toImageResponseDto(language.getImage()));
        return languageResponseDTO;
    }

    public static Language toLanguage(LanguageResponseDTO languageResponseDTO) {
        if (languageResponseDTO == null) return null;
        Language language = new Language();
        BeanUtils.copyProperties(languageResponseDTO, language);
        language.setAuthor(AppUserMappers.toAppUser(languageResponseDTO.getAuthor()));
        language.setImage(ImageMappers.toImage(languageResponseDTO.getImage()));
        return language;
    }

    public static Language toLanguage(LanguageRequestDTO languageRequestDTO, ImageRepository imageRepository) {
        if (languageRequestDTO == null) return null;
        Language language = new Language();
        BeanUtils.copyProperties(languageRequestDTO, language);
        language.setImage(imageRepository.findById(languageRequestDTO.getImageId())
                .orElseThrow(() -> new ResourceNotFoundException("Image", "id", languageRequestDTO.getImageId())));
        return language;
    }
}
