package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.exception.DuplicateResourceException;
import kh.farrukh.progee_api.exception.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.image.ImageCheckUtils;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
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
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        return new PagingResponse<>(languageRepository.findAll(
                PageRequest.of(page, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
        ));
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
        if (!languageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Language", "id", id);
        }
        languageRepository.deleteById(id);
    }
}