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

    public Language addLanguage(LanguageDTO languageDto) {
        Language language = new Language(languageDto);
        if (languageRepository.existsByName(language.getName())) {
            throw new DuplicateResourceException("Language", "name", language.getName());
        }
        return languageRepository.save(language);
    }

    @Transactional
    public Language updateLanguage(long id, LanguageDTO languageDto) {
        Language existingLanguage = languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );

        if (!languageDto.getName().equals(existingLanguage.getName()) &&
                languageRepository.existsByName(languageDto.getName())) {
            throw new DuplicateResourceException("Language", "name", languageDto.getName());
        }

        existingLanguage.setName(languageDto.getName());
        existingLanguage.setDescription(languageDto.getDescription());
        existingLanguage.setImageId(languageDto.getImageId());
        existingLanguage.setAuthorId(languageDto.getAuthorId());

        return existingLanguage;
    }

    public void deleteLanguage(long id) {
        if (!languageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Language", "id", id);
        }
        languageRepository.deleteById(id);
    }
}