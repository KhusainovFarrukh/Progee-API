package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.exception.DuplicateResourceException;
import kh.farrukh.progee_api.exception.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.image.ImageCheckUtils;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.utils.user.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final ImageRepository imageRepository;

    @Override
    public PagingResponse<Language> getLanguages(
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        if (state == null) {
            return new PagingResponse<>(languageRepository.findAll(
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
            ));
        } else if (!UserUtils.isAdmin()) {
            throw new RuntimeException("You don't have enough permissions");
        } else {
            return new PagingResponse<>(languageRepository.findByState(
                    state,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
            ));
        }
    }

    @Override
    public Language getLanguageById(long id) {
        return languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );
    }

    @Override
    public Language addLanguage(LanguageDTO languageDto) {
        if (languageRepository.existsByName(languageDto.getName())) {
            throw new DuplicateResourceException("Language", "name", languageDto.getName());
        }
        ImageCheckUtils.checkImageId(imageRepository, languageDto.getImageId());
        Language language = new Language(languageDto);
        if (UserUtils.isAdmin()) {
            language.setState(ResourceState.APPROVED);
        } else {
            language.setState(ResourceState.WAITING);
        }
        return languageRepository.save(language);
    }

    @Override
    @Transactional
    public Language updateLanguage(long id, LanguageDTO languageDto) {
        Language existingLanguage = languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );

        if (!languageDto.getName().equals(existingLanguage.getName()) &&
                languageRepository.existsByName(languageDto.getName())) {
            throw new DuplicateResourceException("Language", "name", languageDto.getName());
        }
        ImageCheckUtils.checkImageId(imageRepository, languageDto.getImageId());

        existingLanguage.setName(languageDto.getName());
        existingLanguage.setDescription(languageDto.getDescription());
        existingLanguage.setImageId(languageDto.getImageId());
        existingLanguage.setAuthorId(languageDto.getAuthorId());

        return existingLanguage;
    }

    @Override
    public void deleteLanguage(long id) {
        checkLanguageId(id);
        languageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Language setLanguageState(long id, ResourceStateDTO resourceStateDto) {
        Language language = languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );
        language.setState(resourceStateDto.getState());
        return language;
    }

    private void checkLanguageId(long id) {
        if (!languageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Language", "id", id);
        }
    }

    private void checkPageNumber(int page) {
        if (page < 1) {
            // TODO: 6/12/22 custom exception with exception handler
            throw new RuntimeException("Page must be bigger than zero");
        }
    }
}