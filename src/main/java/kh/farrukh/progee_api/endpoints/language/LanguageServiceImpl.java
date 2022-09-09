package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.role.Permission;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.dto.ResourceStateDTO;
import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.utils.user.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
        } else if (CurrentUserUtils.hasPermission(Permission.CAN_VIEW_LANGUAGES_BY_STATE, userRepository)) {
            return new PagingResponse<>(languageRepository.findByState(
                    state,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
            ));
        } else {
            throw new NotEnoughPermissionException();
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
        Language language = new Language(languageDto, imageRepository);
        AppUser currentUser = CurrentUserUtils.getCurrentUser(userRepository);
        language.setAuthor(currentUser);
        if (CurrentUserUtils.hasPermission(Permission.CAN_SET_LANGUAGE_STATE, userRepository)) {
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
    public Language updateLanguage(long id, LanguageDTO languageDto) {
        Language existingLanguage = languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );

        if (
                CurrentUserUtils.hasPermissionOrIsAuthor(
                        Permission.CAN_UPDATE_OTHERS_LANGUAGE,
                        Permission.CAN_UPDATE_OWN_LANGUAGE,
                        existingLanguage.getAuthor().getId(),
                        userRepository
                )
        ) {


            // It checks if the name of the language is changed and if the new name is already taken.
            if (!languageDto.getName().equals(existingLanguage.getName()) &&
                    languageRepository.existsByName(languageDto.getName())) {
                throw new DuplicateResourceException("Language", "name", languageDto.getName());
            }

            existingLanguage.setName(languageDto.getName());
            existingLanguage.setDescription(languageDto.getDescription());
            if (CurrentUserUtils.hasPermission(Permission.CAN_SET_LANGUAGE_STATE, userRepository)) {
                existingLanguage.setState(ResourceState.APPROVED);
            } else {
                existingLanguage.setState(ResourceState.WAITING);
            }
            existingLanguage.setImage(imageRepository.findById(languageDto.getImageId()).orElseThrow(
                    () -> new ResourceNotFoundException("Image", "id", languageDto.getImageId())
            ));

            return languageRepository.save(existingLanguage);
        } else {
            throw new NotEnoughPermissionException();
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
    public Language setLanguageState(long id, ResourceStateDTO resourceStateDto) {
        Language language = languageRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", id)
        );
        language.setState(resourceStateDto.getState());
        return languageRepository.save(language);
    }
}