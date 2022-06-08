package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.exception.DuplicateResourceException;
import kh.farrukh.progee_api.exception.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    public PagingResponse<Language> getLanguages(
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        return new PagingResponse<>(languageRepository.findAll(
                PageRequest.of(page, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
        ));
    }

    public Language getLanguageById(long id) {
        return languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );
    }

    public Language addLanguage(Language language) {
        if (languageRepository.existsByName(language.getName())) {
            throw new DuplicateResourceException("Language", "name", language.getName());
        }
        return languageRepository.save(language);
    }

    @Transactional
    public Language updateLanguage(long id, Language language) {
        Language existingLanguage = languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );

        existingLanguage.setName(language.getName());
        existingLanguage.setDescription(language.getDescription());

        return existingLanguage;
    }

    public void deleteLanguage(long id) {
        if (!languageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Language", "id", id);
        }
        languageRepository.deleteById(id);
    }
}