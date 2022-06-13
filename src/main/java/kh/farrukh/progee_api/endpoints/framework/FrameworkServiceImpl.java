package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.image.Checkers;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.utils.user.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static kh.farrukh.progee_api.utils.image.Checkers.checkLanguageId;
import static kh.farrukh.progee_api.utils.image.Checkers.checkPageNumber;

@Service
@RequiredArgsConstructor
public class FrameworkServiceImpl implements FrameworkService {

    private final FrameworkRepository frameworkRepository;
    private final LanguageRepository languageRepository;
    private final ImageRepository imageRepository;

    @Override
    public PagingResponse<Framework> getFrameworksByLanguage(
            long languageId,
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        checkLanguageId(languageRepository, languageId);
        if (state == null) {
            return new PagingResponse<>(frameworkRepository.findByLanguage_Id(
                    languageId,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))));
        } else if (!UserUtils.isAdmin()) {
            throw new RuntimeException("You don't have enough permissions");
        } else {
            return new PagingResponse<>(frameworkRepository.findByStateAndLanguage_Id(
                    state,
                    languageId,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
            ));
        }
    }

    @Override
    public Framework getFrameworkById(long languageId, long id) {
        checkLanguageId(languageRepository, languageId);
        return frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", "id", id)
        );
    }

    @Override
    public Framework addFramework(long languageId, FrameworkDTO frameworkDto) {
        checkLanguageId(languageRepository, languageId);
        if (frameworkRepository.existsByName(frameworkDto.getName())) {
            throw new DuplicateResourceException("Framework", "name", frameworkDto.getName());
        }
        Checkers.checkImageId(imageRepository, frameworkDto.getImageId());
        Framework framework = new Framework(frameworkDto);
        if (UserUtils.isAdmin()) {
            framework.setState(ResourceState.APPROVED);
        } else {
            framework.setState(ResourceState.WAITING);
        }
        framework.setLanguageId(languageId);
        return frameworkRepository.save(framework);
    }

    @Override
    @Transactional
    public Framework updateFramework(long languageId, long id, FrameworkDTO frameworkDto) {
        checkLanguageId(languageRepository, languageId);
        Framework existingFramework = frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", "id", id)
        );
        if (!frameworkDto.getName().equals(existingFramework.getName()) &&
                languageRepository.existsByName(frameworkDto.getName())) {
            throw new DuplicateResourceException("Framework", "name", frameworkDto.getName());
        }
        Checkers.checkImageId(imageRepository, frameworkDto.getImageId());

        existingFramework.setLanguageId(languageId);
        existingFramework.setName(frameworkDto.getName());
        existingFramework.setDescription(frameworkDto.getDescription());
        existingFramework.setImageId(frameworkDto.getImageId());
        existingFramework.setAuthorId(frameworkDto.getAuthorId());

        return existingFramework;
    }

    @Override
    public void deleteFramework(long languageId, long id) {
        checkLanguageId(languageRepository, languageId);
        if (!frameworkRepository.existsById(id)) {
            throw new ResourceNotFoundException("Framework", "id", id);
        }
        frameworkRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Framework setFrameworkState(long languageId, long id, ResourceStateDTO resourceStateDto) {
        checkLanguageId(languageRepository, languageId);
        Framework framework = frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", "id", id)
        );
        framework.setState(resourceStateDto.getState());
        return framework;
    }
}