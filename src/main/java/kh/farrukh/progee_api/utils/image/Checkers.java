package kh.farrukh.progee_api.utils.image;

import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.endpoints.review.ReviewRepository;
import kh.farrukh.progee_api.endpoints.user.AppUserDTO;
import kh.farrukh.progee_api.endpoints.user.UserRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.BadRequestException;
import kh.farrukh.progee_api.exception.custom_exceptions.DuplicateResourceException;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;

public class Checkers {

    public static void checkImageId(ImageRepository imageRepository, long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("Image", "id", imageId);
        }
    }

    public static void checkPageNumber(int page) {
        if (page < 1) {
            throw new BadRequestException("Page index");
        }
    }


    public static void checkLanguageId(LanguageRepository languageRepository, long languageId) {
        if (!languageRepository.existsById(languageId)) {
            throw new ResourceNotFoundException("Language", "id", languageId);
        }
    }

    public static void checkReviewId(ReviewRepository reviewRepository, long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", "id", id);
        }
    }

    public static void checkUserId(UserRepository userRepository, long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }

    public static void checkUserIsUnique(UserRepository userRepository, AppUserDTO appUserDto) {
        if (userRepository.existsByUniqueUsername(appUserDto.getUsername())) {
            throw new DuplicateResourceException("User", "username", appUserDto.getUsername());
        }
        if (userRepository.existsByEmail(appUserDto.getEmail())) {
            throw new DuplicateResourceException("User", "email", appUserDto.getUsername());
        }
    }
}
