package kh.farrukh.progee_api.global.utils.checkers;

import kh.farrukh.progee_api.image.ImageRepository;
import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.review.ReviewRepository;
import kh.farrukh.progee_api.role.RoleRepository;
import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.user.AppUserRepository;
import kh.farrukh.progee_api.user.payloads.AppUserRequestDTO;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.SortParamException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * It contains static methods that check if a resource exists in the database,
 * is unique, request parameter is valid and etc.
 */
public class Checkers {

    /**
     * If the image with the given ID doesn't exist, throw a ResourceNotFoundException
     *
     * @param imageRepository The repository that will be used to check if the image exists.
     * @param imageId         The id of the image to be checked.
     */
    public static void checkImageId(ImageRepository imageRepository, long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("Image", "id", imageId);
        }
    }

    /**
     * If the page number is less than 1, throw a BadRequestException.
     *
     * @param page The page number to return.
     */
    public static void checkPageNumber(int page) {
        if (page < 1) throw new BadRequestException("Page index");
    }

    public static void checkSortParams(Pageable pageable, List<String> allowedSortFields) {
        Sort sort = pageable.getSort();
        if (sort.isUnsorted()) {
            return;
        }

        List<String> notAllowedFields = sort.stream()
                .map(Sort.Order::getProperty)
                .filter(property -> !allowedSortFields.contains(property))
                .toList();

        // if all found fields are allowed, then it is valid request
        if (notAllowedFields.size() == 0) {
            return;
        }

        // else it is not valid request
        throw new SortParamException(notAllowedFields, allowedSortFields);
    }

    /**
     * If the languageId does not exist in the database, throw a ResourceNotFoundException
     *
     * @param languageRepository The repository that will be used to check if the language exists.
     * @param languageId         The id of the language to be checked
     */
    public static void checkLanguageId(LanguageRepository languageRepository, long languageId) {
        if (!languageRepository.existsById(languageId)) {
            throw new ResourceNotFoundException("Language", "id", languageId);
        }
    }

    /**
     * If the review with the given id doesn't exist, throw a ResourceNotFoundException
     *
     * @param reviewRepository The repository that we are using to check if the review exists.
     * @param id               The id of the review to be deleted.
     */
    public static void checkReviewId(ReviewRepository reviewRepository, long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", "id", id);
        }
    }

    /**
     * If the user doesn't exist, throw a ResourceNotFoundException.
     *
     * @param appUserRepository The repository that we are using to check if the user exists.
     * @param id                The id of the user to be checked
     */
    public static void checkUserId(AppUserRepository appUserRepository, long id) {
        if (!appUserRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }

    /**
     * If the user with given username or email already exists, throw an exception.
     *
     * @param appUserRepository The repository that will be used to check if the user exists.
     * @param appUserRequestDto The DTO that is being validated.
     */
    public static void checkUserIsUnique(AppUserRepository appUserRepository, AppUserRequestDTO appUserRequestDto) {
        if (appUserRepository.existsByUniqueUsername(appUserRequestDto.getUsername())) {
            throw new DuplicateResourceException("User", "username", appUserRequestDto.getUsername());
        }
        if (appUserRepository.existsByEmail(appUserRequestDto.getEmail())) {
            throw new DuplicateResourceException("User", "email", appUserRequestDto.getEmail());
        }
    }

    public static void checkRoleId(RoleRepository roleRepository, long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
    }

    public static void checkRoleIsUnique(RoleRepository roleRepository, RoleRequestDTO roleRequestDTO) {
        if (roleRepository.existsByTitle(roleRequestDTO.getTitle())) {
            throw new DuplicateResourceException("Role", "title", roleRequestDTO.getTitle());
        }
    }
}
