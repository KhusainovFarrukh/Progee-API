package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.language.payloads.LanguageRequestDTO;
import kh.farrukh.progee_api.language.payloads.LanguageResponseDTO;
import kh.farrukh.progee_api.global.resource_state.ResourceStateDTO;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;

/**
 * A base interface for service of Language endpoints
 *
 * Methods implemented in LanguageServiceImpl
 */
public interface LanguageService {

    PagingResponse<LanguageResponseDTO> getLanguages(
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    LanguageResponseDTO getLanguageById(long id);

    LanguageResponseDTO addLanguage(LanguageRequestDTO languageRequestDto);

    LanguageResponseDTO updateLanguage(long id, LanguageRequestDTO languageRequestDto);

    void deleteLanguage(long id);

    LanguageResponseDTO setLanguageState(long id, ResourceStateDTO resourceStateDto);
}
