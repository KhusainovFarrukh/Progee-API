package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.utils.exception.ResourceDuplicateNameException;
import kh.farrukh.progee_api.utils.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getLanguages() {
        return languageRepository.findAll();
    }

    public Language getLanguageById(long id) {
        return languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", id)
        );
    }

    public Language addLanguage(Language language) {
        if (languageRepository.existsByName(language.getName())) {
            throw new ResourceDuplicateNameException("Language", language.getName());
        }
        return languageRepository.save(language);
    }

    @Transactional
    public Language updateLanguage(long id, Language language) {
        Language languageToUpdate = languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", id)
        );

        languageToUpdate.setName(language.getName());
        languageToUpdate.setDescription(language.getDescription());
        languageToUpdate.setHasSamples(language.getHasSamples());

        return languageToUpdate;
    }

    public void deleteLanguage(long id) {
        if (!languageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Language", id);
        }
        languageRepository.deleteById(id);
    }
}