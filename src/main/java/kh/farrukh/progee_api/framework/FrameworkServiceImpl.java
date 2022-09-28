package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.framework.payloads.FrameworkResponseDTO;
import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.role.Permission;
import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.app_user.AppUserRepository;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.BadRequestException;
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
 * It implements the `FrameworkService` interface and uses the `FrameworkRepository`
 * to perform CRUD operations on the `Framework` entity
 */
@Service
@RequiredArgsConstructor
public class FrameworkServiceImpl implements FrameworkService {

    private final FrameworkRepository frameworkRepository;
    private final LanguageRepository languageRepository;
    private final ImageRepository imageRepository;
    private final AppUserRepository appUserRepository;

    /**
     * This function returns a list of frameworks
     *
     * @param languageId The id of the language to filter by.
     * @param state      The state of the framework.
     * @param page       The page number.
     * @param pageSize   The number of items to return per page.
     * @param sortBy     The field to sort by.
     * @param orderBy    The order of the results. Can be either "asc" or "desc".
     * @return A list of frameworks
     */
    @Override
    public PagingResponse<FrameworkResponseDTO> getFrameworks(
            Long languageId,
            ResourceState state,
            int page,
            int pageSize,
            String sortBy,
            String orderBy
    ) {
        checkPageNumber(page);
        if (languageId != null) checkLanguageId(languageRepository, languageId);
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy));
        checkSortParams(pageable, List.of("id", "name", "description", "state", "createdAt"));

        // Checking if the user has permission to view frameworks by state. If not, it will throw an exception.
        if (!CurrentUserUtils.hasPermission(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE, appUserRepository)) {
            if (state != null) throw new NotEnoughPermissionException();
            state = ResourceState.APPROVED;
        }

        return new PagingResponse<>(frameworkRepository.findAll(
                new FrameworkSpecification(languageId, state),
                pageable
        ).map(FrameworkMappers::toFrameworkResponseDTO));
    }

    /**
     * If the languageId is valid, return the framework with the given id, or throw a ResourceNotFoundException if the
     * framework doesn't exist.
     *
     * @param id The id of the framework to be retrieved
     * @return FrameworkResponseDTO
     */
    @Override
    public FrameworkResponseDTO getFrameworkById(long id) {
        Framework framework = frameworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Framework", "id", id));

        // It checks if the current user has permission to view frameworks by state. If not, it will throw an exception.
        if (framework.getState() != ResourceState.APPROVED &&
                !CurrentUserUtils.hasPermission(Permission.CAN_VIEW_FRAMEWORKS_BY_STATE, appUserRepository)) {
            throw new NotEnoughPermissionException();
        }

        return FrameworkMappers.toFrameworkResponseDTO(framework);
    }

    /**
     * This function adds a framework to the database
     *
     * @param frameworkRequestDto The DTO object that contains the framework information.
     * @return FrameworkResponseDTO
     */
    @Override
    public FrameworkResponseDTO addFramework(FrameworkRequestDTO frameworkRequestDto) {
        if (frameworkRequestDto.getLanguageId() == null) {
            throw new BadRequestException("Language id");
        }
        if (frameworkRepository.existsByName(frameworkRequestDto.getName())) {
            throw new DuplicateResourceException("Framework", "name", frameworkRequestDto.getName());
        }

        Framework framework = FrameworkMappers.toFramework(frameworkRequestDto, languageRepository, imageRepository);
        AppUser currentUser = CurrentUserUtils.getCurrentUser(appUserRepository);
        framework.setAuthor(currentUser);
        // It checks if the current user has permission to set the state of the framework. If not, it sets the state to
        // `WAITING`.
        if (CurrentUserUtils.hasPermission(Permission.CAN_SET_FRAMEWORK_STATE, appUserRepository)) {
            framework.setState(ResourceState.APPROVED);
        } else {
            framework.setState(ResourceState.WAITING);
        }

        return FrameworkMappers.toFrameworkResponseDTO(frameworkRepository.save(framework));
    }

    /**
     * This function updates a framework in the database
     *
     * @param id                  The id of the framework to update
     * @param frameworkRequestDto The DTO object that contains the new values for the framework.
     * @return The updated framework.
     */
    @Override
    public FrameworkResponseDTO updateFramework(long id, FrameworkRequestDTO frameworkRequestDto) {
        Framework framework = frameworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Framework", "id", id));

        // It checks if the current user has permission to update the framework. If not, it throws an exception.
        if (CurrentUserUtils.hasPermissionOrIsAuthor(
                Permission.CAN_UPDATE_OTHERS_FRAMEWORK,
                Permission.CAN_UPDATE_OWN_FRAMEWORK,
                framework.getAuthor().getId(),
                appUserRepository
        )) {

            // It checks if the name of the framework is changed and if the new name is already taken.
            if (!frameworkRequestDto.getName().equals(framework.getName()) &&
                    languageRepository.existsByName(frameworkRequestDto.getName())) {
                throw new DuplicateResourceException("Framework", "name", frameworkRequestDto.getName());
            }

            framework.setName(frameworkRequestDto.getName());
            framework.setDescription(frameworkRequestDto.getDescription());
            framework.setImage(imageRepository.findById(frameworkRequestDto.getImageId()).orElseThrow(
                    () -> new ResourceNotFoundException("Image", "id", frameworkRequestDto.getImageId())
            ));
            if (CurrentUserUtils.hasPermission(Permission.CAN_SET_FRAMEWORK_STATE, appUserRepository)) {
                framework.setState(ResourceState.APPROVED);
            } else {
                framework.setState(ResourceState.WAITING);
            }

            return FrameworkMappers.toFrameworkResponseDTO(frameworkRepository.save(framework));
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    /**
     * This function deletes a framework by id
     *
     * @param id The id of the framework to delete
     */
    @Override
    public void deleteFramework(long id) {
        if (!frameworkRepository.existsById(id)) {
            throw new ResourceNotFoundException("Framework", "id", id);
        }
        frameworkRepository.deleteById(id);
    }

    /**
     * This function sets the state of a framework
     *
     * @param id               The id of the framework to update
     * @param setResourceStateRequestDto This is the object that contains the state that we want to set.
     * @return Framework
     */
    @Override
    public FrameworkResponseDTO setFrameworkState(long id, SetResourceStateRequestDTO setResourceStateRequestDto) {
        Framework framework = frameworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Framework", "id", id));
        framework.setState(setResourceStateRequestDto.getState());
        return FrameworkMappers.toFrameworkResponseDTO(frameworkRepository.save(framework));
    }
}