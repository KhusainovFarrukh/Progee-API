package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.payloads.LanguageRequestDTO;
import kh.farrukh.progee_api.language.payloads.LanguageResponseDTO;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.resource_state.SetResourceStateRequestDTO;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.global.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.global.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.global.utils.user.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static kh.farrukh.progee_api.global.utils.checkers.Checkers.*;

/**
 * It implements the LanguageService interface and uses the LanguageRepository
 * to perform CRUD operations on the Language entity
 */
@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final ImageRepository imageRepository;
    private final AppUserRepository appUserRepository;

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
    public PagingResponse<LanguageResponseDTO> getLanguages(
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy));
        checkSortParams(pageable, List.of("id", "name", "description", "state", "createdAt"));

        // If the user doesn't have the permission to view languages by state, then if the state is not null, then throw an
        // exception, otherwise set the APPROVED state.
        if (!CurrentUserUtils.hasPermission(Permission.CAN_VIEW_LANGUAGES_BY_STATE, appUserRepository)) {
            if (state != null) throw new NotEnoughPermissionException();
            state = ResourceState.APPROVED;
        }

        return new PagingResponse<>(languageRepository.findAll(
                new LanguageSpecification(state),
                pageable
        ).map(LanguageMappers::toLanguageResponseDTO));
    }

    /**
     * If the id is valid, return the language with the given id, or throw a ResourceNotFoundException if the
     * language doesn't exist.
     *
     * @param id The id of the language to be retrieved
     * @return Language
     */
    @Override
    public LanguageResponseDTO getLanguageById(long id) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", id));

        // It checks if the user has the permission to view languages by state. If the user doesn't have the permission,
        // then if the state is not null, then throw an
        // exception, otherwise set the APPROVED state.
        if (language.getState() != ResourceState.APPROVED &&
                !CurrentUserUtils.hasPermission(Permission.CAN_VIEW_LANGUAGES_BY_STATE, appUserRepository)) {
            throw new NotEnoughPermissionException();
        }

        return LanguageMappers.toLanguageResponseDTO(language);
    }

    /**
     * This function adds a language to the database
     *
     * @param languageRequestDto The DTO object that contains the language information.
     * @return Language
     */
    @Override
    public LanguageResponseDTO addLanguage(LanguageRequestDTO languageRequestDto) {
        if (languageRepository.existsByName(languageRequestDto.getName())) {
            throw new DuplicateResourceException("Language", "name", languageRequestDto.getName());
        }

        Language language = LanguageMappers.toLanguage(languageRequestDto, imageRepository);
        language.setAuthor(CurrentUserUtils.getCurrentUser(appUserRepository));

        // It checks if the user has the permission to set the state of the language. If the user has the permission, then
        // the state of the language is set to APPROVED, otherwise it is set to WAITING.
        if (CurrentUserUtils.hasPermission(Permission.CAN_SET_LANGUAGE_STATE, appUserRepository)) {
            language.setState(ResourceState.APPROVED);
        } else {
            language.setState(ResourceState.WAITING);
        }

        return LanguageMappers.toLanguageResponseDTO(languageRepository.save(language));
    }

    /**
     * This function updates a language in the database
     *
     * @param id                 The id of the framework to update
     * @param languageRequestDto The DTO object that contains the new values for the language.
     * @return The updated language.
     */
    @Override
    public LanguageResponseDTO updateLanguage(long id, LanguageRequestDTO languageRequestDto) {
        Language existingLanguage = languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", id));

        // Checking if the user has the permission to update the language. If the user has the permission, then it updates
        // the language, otherwise it throws an exception.
        if (CurrentUserUtils.hasPermissionOrIsAuthor(
                Permission.CAN_UPDATE_OTHERS_LANGUAGE,
                Permission.CAN_UPDATE_OWN_LANGUAGE,
                existingLanguage.getAuthor().getId(),
                appUserRepository
        )) {

            // It checks if the name of the language is changed and if the new name is already taken.
            if (!languageRequestDto.getName().equals(existingLanguage.getName()) &&
                    languageRepository.existsByName(languageRequestDto.getName())) {
                throw new DuplicateResourceException("Language", "name", languageRequestDto.getName());
            }

            existingLanguage.setName(languageRequestDto.getName());
            existingLanguage.setDescription(languageRequestDto.getDescription());
            if (CurrentUserUtils.hasPermission(Permission.CAN_SET_LANGUAGE_STATE, appUserRepository)) {
                existingLanguage.setState(ResourceState.APPROVED);
            } else {
                existingLanguage.setState(ResourceState.WAITING);
            }
            existingLanguage.setImage(imageRepository.findById(languageRequestDto.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image", "id", languageRequestDto.getImageId())));

            return LanguageMappers.toLanguageResponseDTO(languageRepository.save(existingLanguage));
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
     * @param setResourceStateRequestDto This is the object that contains the state that we want to set.
     * @return Language
     */
    @Override
    public LanguageResponseDTO setLanguageState(long id, SetResourceStateRequestDTO setResourceStateRequestDto) {
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", id));
        language.setState(setResourceStateRequestDto.getState());
        return LanguageMappers.toLanguageResponseDTO(languageRepository.save(language));
    }
}