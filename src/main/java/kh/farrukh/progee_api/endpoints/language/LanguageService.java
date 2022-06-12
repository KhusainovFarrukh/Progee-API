package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;

public interface LanguageService {
    PagingResponse<Language> getLanguages(
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    );

    Language getLanguageById(long id);

    Language addLanguage(LanguageDTO languageDto);

    Language updateLanguage(long id, LanguageDTO languageDto);

    void deleteLanguage(long id);

    Language setLanguageState(long id, ResourceStateDTO resourceStateDto);
}
