package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.utils.exception.DuplicateResourceException;
import kh.farrukh.progee_api.utils.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    public List<Language> getLanguages() {
        return languageRepository.findAll();
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
        existingLanguage.setHasSamples(language.getHasSamples());

        return existingLanguage;
    }

    public void deleteLanguage(long id) {
        if (!languageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Language", "id", id);
        }
        languageRepository.deleteById(id);
    }
}