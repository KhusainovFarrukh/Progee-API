package kh.farrukh.progee_api.language;

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
                () -> new IllegalStateException("Language with id " + id + " does not exist")
        );
    }

    public Language addLanguage(Language language) {
        if (languageRepository.existsByName(language.getName())) {
            throw new IllegalStateException("Language with name " + language.getName() + " already exists");
        }
        return languageRepository.save(language);
    }

    @Transactional
    public Language updateLanguage(long id, Language language) {
        Language languageToUpdate = languageRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("Language with id " + id + " does not exist")
        );

        languageToUpdate.setName(language.getName());
        languageToUpdate.setDescription(language.getDescription());
        languageToUpdate.setHasSamples(language.getHasSamples());

        return languageToUpdate;
    }

    public void deleteLanguage(long id) {
        if (!languageRepository.existsById(id)) {
            throw new IllegalStateException("Language with id " + id + " does not exist");
        }
        languageRepository.deleteById(id);
    }
}