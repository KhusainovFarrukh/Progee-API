package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exception.custom_exceptions.PermissionException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.checkers.Checkers;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.utils.user.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static kh.farrukh.progee_api.utils.checkers.Checkers.checkLanguageId;
import static kh.farrukh.progee_api.utils.checkers.Checkers.checkPageNumber;

/**
 * It implements the LanguageService interface and uses the LanguageRepository and ImageRepository
 * to perform CRUD operations on the Language entity
 */
@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    /**
     * This function returns a list of languages
     *
     * @param state    The state of the language.
     * @param page     The page number.
     * @param pageSize The number of items to return per page.
     * @param sortBy   The field to sort by.
     * @param orderBy  The order of the results. Can be either "asc" or "desc".
     * @return A list of frameworks
     */
    @Override
    public PagingResponse<Language> getLanguages(
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        // If there isn't state param in request, return only approved languages.
        // Else if the user is admin then return the list of languages with the given state.
        // Else if the user is not admin, throw an exception.
        if (state == null) {
            return new PagingResponse<>(languageRepository.findByState(
                    ResourceState.APPROVED,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
            ));
        } else if (UserUtils.isAdmin()) {
            return new PagingResponse<>(languageRepository.findByState(
                    state,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
            ));
        } else {
            throw new RuntimeException("You don't have enough permissions");
        }
    }

    /**
     * If the id is valid, return the language with the given id, or throw a ResourceNotFoundException if the
     * language doesn't exist.
     *
     * @param id The id of the language to be retrieved
     * @return Language
     */
    @Override
    public Language getLanguageById(long id) {
        return languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );
    }

    /**
     * This function adds a language to the database
     *
     * @param languageDto The DTO object that contains the language information.
     * @return Language
     */
    @Override
    public Language addLanguage(LanguageDTO languageDto) {
        if (languageRepository.existsByName(languageDto.getName())) {
            throw new DuplicateResourceException("Language", "name", languageDto.getName());
        }
        Checkers.checkImageId(imageRepository, languageDto.getImageId());
        Language language = new Language(languageDto);
        AppUser currentUser = UserUtils.getCurrentUser(userRepository);
        language.setAuthorId(currentUser.getId());
        if (currentUser.isAdmin()) {
            language.setState(ResourceState.APPROVED);
        } else {
            language.setState(ResourceState.WAITING);
        }
        return languageRepository.save(language);
    }

    /**
     * This function updates a language in the database
     *
     * @param id          The id of the framework to update
     * @param languageDto The DTO object that contains the new values for the language.
     * @return The updated language.
     */
    @Override
    @Transactional
    public Language updateLanguage(long id, LanguageDTO languageDto) {
        Language existingLanguage = languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );

        if (UserUtils.isAdminOrAuthor(existingLanguage.getAuthor().getId(), userRepository)) {

            // It checks if the name of the language is changed and if the new name is already taken.
            if (!languageDto.getName().equals(existingLanguage.getName()) &&
                    languageRepository.existsByName(languageDto.getName())) {
                throw new DuplicateResourceException("Language", "name", languageDto.getName());
            }
            Checkers.checkImageId(imageRepository, languageDto.getImageId());

            existingLanguage.setName(languageDto.getName());
            existingLanguage.setDescription(languageDto.getDescription());
            existingLanguage.setImageId(languageDto.getImageId());
            existingLanguage.setStateAccordingToRole(UserUtils.isAdmin());

            return existingLanguage;
        } else {
            throw new PermissionException();
        }
    }

    /**
     * This function deletes a language by id
     *
     * @param id The id of the language to delete
     */
    @Override
    public void deleteLanguage(long id) {
        checkLanguageId(languageRepository, id);
        languageRepository.deleteById(id);
    }

    /**
     * This function sets the state of a language
     *
     * @param id               The id of the language to update
     * @param resourceStateDto This is the object that contains the state that we want to set.
     * @return Language
     */
    @Override
    @Transactional
    public Language setLanguageState(long id, ResourceStateDTO resourceStateDto) {
        Language language = languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );
        language.setState(resourceStateDto.getState());
        return language;
    }
}