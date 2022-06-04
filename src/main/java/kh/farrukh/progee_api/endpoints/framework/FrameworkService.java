package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.utils.exception.DuplicateResourceException;
import kh.farrukh.progee_api.utils.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class FrameworkService {

    private final FrameworkRepository frameworkRepository;
    private final LanguageRepository languageRepository;

    public FrameworkService(FrameworkRepository frameworkRepository, LanguageRepository languageRepository) {
        this.frameworkRepository = frameworkRepository;
        this.languageRepository = languageRepository;
    }

    public List<Framework> getFrameworksByLanguage(long languageId) {
        checkLanguageId(languageId);
        return frameworkRepository.findByLanguage_Id(languageId);
    }

    public Framework getFrameworkById(long languageId, long id) {
        checkLanguageId(languageId);
        return frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", "id", id)
        );
    }

    public Framework addFramework(long languageId, Framework framework) {
        checkLanguageId(languageId);
        if (frameworkRepository.existsByName(framework.getName())) {
            throw new DuplicateResourceException("Framework", "name", framework.getName());
        }
        framework.setLanguageId(languageId);
        return frameworkRepository.save(framework);
    }

    @Transactional
    public Framework updateFramework(long languageId, long id, Framework framework) {
        checkLanguageId(languageId);
        framework.setLanguageId(languageId);
        Framework existingFramework = frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", "id", id)
        );

        existingFramework.setName(framework.getName());
        existingFramework.setDescription(framework.getDescription());
        existingFramework.setLanguageId(framework.getLanguage().getId());

        return existingFramework;
    }

    public void deleteFramework(long languageId, long id) {
        checkLanguageId(languageId);
        if (!frameworkRepository.existsById(id)) {
            throw new ResourceNotFoundException("Framework", "id", id);
        }
        frameworkRepository.deleteById(id);
    }

    private void checkLanguageId(long languageId) {
        if (!languageRepository.existsById(languageId)) {
            throw new ResourceNotFoundException("Language", "id", languageId);
        }
    }
}