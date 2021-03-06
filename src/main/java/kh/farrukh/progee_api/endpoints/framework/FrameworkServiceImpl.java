package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.base.dto.ResourceStateDTO;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exception.custom_exceptions.NotEnoughPermissionException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.utils.paging_sorting.PagingResponse;
import kh.farrukh.progee_api.utils.paging_sorting.SortUtils;
import kh.farrukh.progee_api.utils.user.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static kh.farrukh.progee_api.utils.checkers.Checkers.checkLanguageId;
import static kh.farrukh.progee_api.utils.checkers.Checkers.checkPageNumber;

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
    private final UserRepository userRepository;

    /**
     * This function returns a list of frameworks that are associated with a specific language
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
        // If there isn't state param in request, return only approved frameworks.
        // Else if the user is admin then return the list of frameworks with the given state.
        // Else if the user is not admin, throw an exception.
        if (state == null) {
            return new PagingResponse<>(frameworkRepository.findByStateAndLanguage_Id(
                    ResourceState.APPROVED,
                    languageId,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))));
        } else if (CurrentUserUtils.isAdmin()) {
            return new PagingResponse<>(frameworkRepository.findByStateAndLanguage_Id(
                    state,
                    languageId,
                    PageRequest.of(page - 1, pageSize, Sort.by(SortUtils.parseDirection(orderBy), sortBy))
            ));
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    /**
     * If the languageId is valid, return the framework with the given id, or throw a ResourceNotFoundException if the
     * framework doesn't exist.
     *
     * @param languageId The id of the language that the framework is associated with.
     * @param id         The id of the framework to be retrieved
     * @return Framework
     */
    @Override
    public Framework getFrameworkById(long languageId, long id) {
        checkLanguageId(languageRepository, languageId);
        return frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", "id", id)
        );
    }

    /**
     * This function adds a framework to the database
     *
     * @param languageId   The id of the language that the framework belongs to.
     * @param frameworkDto The DTO object that contains the framework information.
     * @return Framework
     */
    @Override
    public Framework addFramework(long languageId, FrameworkDTO frameworkDto) {
        if (frameworkRepository.existsByName(frameworkDto.getName())) {
            throw new DuplicateResourceException("Framework", "name", frameworkDto.getName());
        }

        Framework framework = new Framework(frameworkDto, imageRepository);
        AppUser currentUser = CurrentUserUtils.getCurrentUser(userRepository);
        framework.setAuthor(currentUser);
        framework.setStateAccordingToRole(currentUser.isAdmin());
        framework.setLanguage(languageRepository.findById(languageId).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", languageId)
        ));
        return frameworkRepository.save(framework);
    }

    /**
     * This function updates a framework in the database
     *
     * @param languageId   The id of the language that the framework belongs to.
     * @param id           The id of the framework to update
     * @param frameworkDto The DTO object that contains the new values for the framework.
     * @return The updated framework.
     */
    @Override
    @Transactional
    public Framework updateFramework(long languageId, long id, FrameworkDTO frameworkDto) {
        checkLanguageId(languageRepository, languageId);
        Framework existingFramework = frameworkRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Framework", "id", id)
        );

        if (CurrentUserUtils.isAdminOrAuthor(existingFramework.getAuthor().getId(), userRepository)) {

            // It checks if the name of the framework is changed and if the new name is already taken.
            if (!frameworkDto.getName().equals(existingFramework.getName()) &&
                    languageRepository.existsByName(frameworkDto.getName())) {
                throw new DuplicateResourceException("Framework", "name", frameworkDto.getName());
            }

            existingFramework.setName(frameworkDto.getName());
            existingFramework.setDescription(frameworkDto.getDescription());
            existingFramework.setStateAccordingToRole(CurrentUserUtils.isAdmin());
            existingFramework.setImage(imageRepository.findById(frameworkDto.getImageId()).orElseThrow(
                    () -> new ResourceNotFoundException("Image", "id", frameworkDto.getImageId())
            ));

            return existingFramework;
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    /**
     * This function deletes a framework by id
     *
     * @param languageId The id of the language that the framework belongs to.
     * @param id         The id of the framework to delete
     */
    @Override
    public void deleteFramework(long languageId, long id) {
        checkLanguageId(languageRepository, languageId);
        if (!frameworkRepository.existsById(id)) {
            throw new ResourceNotFoundException("Framework", "id", id);
        }
        frameworkRepository.deleteById(id);
    }

    /**
     * This function sets the state of a framework
     *
     * @param languageId       The id of the language that the framework is associated with.
     * @param id               The id of the framework to update
     * @param resourceStateDto This is the object that contains the state that we want to set.
     * @return Framework
     */
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