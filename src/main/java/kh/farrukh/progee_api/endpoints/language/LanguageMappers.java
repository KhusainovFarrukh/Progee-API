package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.endpoints.image.ImageMappers;
import kh.farrukh.progee_api.endpoints.language.payloads.LanguageResponseDTO;
import kh.farrukh.progee_api.endpoints.user.AppUserMappers;
import org.springframework.beans.BeanUtils;

public class LanguageMappers {

    public static LanguageResponseDTO toLanguageResponseDTO(Language language) {
        if (language == null) return null;
        LanguageResponseDTO languageResponseDTO = new LanguageResponseDTO();
        BeanUtils.copyProperties(language, languageResponseDTO);
        languageResponseDTO.setAuthor(AppUserMappers.toAppUserResponseDTO(language.getAuthor()));
        languageResponseDTO.setImage(ImageMappers.toImageDto(language.getImage()));
        return languageResponseDTO;
    }
}
