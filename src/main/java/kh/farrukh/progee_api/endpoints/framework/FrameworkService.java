package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
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
public class FrameworkService {

    private final FrameworkRepository frameworkRepository;
    private final LanguageRepository languageRepository;

    public PagingResponse<Framework> getFrameworksByLanguage(
            long languageId,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkLanguageId(languageId);
        return new PagingResponse<>(frameworkRepository.findByLanguage_Id(
                languageId,
                PageRequest.of(page, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))));
    }

    public Framework getFrameworkById(long languageId, long id) {
        checkLanguageId(languageId);
        return frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", "id", id)
        );
    }

    public Framework addFramework(long languageId, FrameworkDTO frameworkDto) {
        Framework framework = new Framework(frameworkDto);
        checkLanguageId(languageId);
        if (frameworkRepository.existsByName(framework.getName())) {
            throw new DuplicateResourceException("Framework", "name", framework.getName());
        }
        framework.setLanguageId(languageId);
        return frameworkRepository.save(framework);
    }

    @Transactional
    public Framework updateFramework(long languageId, long id, FrameworkDTO frameworkDto) {
        checkLanguageId(languageId);
        Framework existingFramework = frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", "id", id)
        );
        if (!frameworkDto.getName().equals(existingFramework.getName()) &&
                languageRepository.existsByName(frameworkDto.getName())) {
            throw new DuplicateResourceException("Framework", "name", frameworkDto.getName());
        }

        existingFramework.setLanguageId(languageId);
        existingFramework.setName(frameworkDto.getName());
        existingFramework.setDescription(frameworkDto.getDescription());
        existingFramework.setImageId(frameworkDto.getImageId());

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