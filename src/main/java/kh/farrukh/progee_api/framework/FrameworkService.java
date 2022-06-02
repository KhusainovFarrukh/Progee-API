package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.utils.exception.ResourceDuplicateNameException;
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
                () -> new ResourceNotFoundException("Framework", id)
        );
    }

    public void addFramework(long languageId, Framework framework) {
        checkLanguageId(languageId);
        if (frameworkRepository.existsByName(framework.getName())) {
            throw new ResourceDuplicateNameException("Framework", framework.getName());
        }
        framework.setLanguageId(languageId);
        frameworkRepository.save(framework);
    }

    @Transactional
    public void updateFramework(long languageId, long id, Framework framework) {
        checkLanguageId(languageId);
        framework.setLanguageId(languageId);
        Framework frameworkToUpdate = frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", id)
        );

        frameworkToUpdate.setName(framework.getName());
        frameworkToUpdate.setDescription(framework.getDescription());
        frameworkToUpdate.setLanguageId(framework.getLanguage().getId());
    }

    public void deleteFramework(long languageId, long id) {
        checkLanguageId(languageId);
        if (!frameworkRepository.existsById(id)) {
            throw new ResourceNotFoundException("Framework", id);
        }
        frameworkRepository.deleteById(id);
    }

    private void checkLanguageId(long languageId) {
        if (!languageRepository.existsById(languageId)) {
            throw new ResourceNotFoundException("Language", languageId);
        }
    }
}