package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.endpoints.language.payloads.LanguageRequestDTO;
import kh.farrukh.progee_api.global.dto.ResourceStateDTO;
import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

/**
 * A base interface for service of Language endpoints
 *
 * Methods implemented in LanguageServiceImpl
 */
public interface LanguageService {

    PagingResponse<Language> getLanguages(
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    Language getLanguageById(long id);

    Language addLanguage(LanguageRequestDTO languageRequestDto);

    Language updateLanguage(long id, LanguageRequestDTO languageRequestDto);

    void deleteLanguage(long id);

    Language setLanguageState(long id, ResourceStateDTO resourceStateDto);
}
