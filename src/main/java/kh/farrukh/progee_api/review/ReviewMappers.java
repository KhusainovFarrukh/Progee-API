package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.language.LanguageMappers;
import kh.farrukh.progee_api.language.LanguageRepository;
import kh.farrukh.progee_api.review.payloads.ReviewRequestDTO;
import kh.farrukh.progee_api.review.payloads.ReviewResponseDTO;
import kh.farrukh.progee_api.user.AppUserMappers;
import kh.farrukh.progee_api.global.exceptions.custom_exceptions.ResourceNotFoundException;
import org.springframework.beans.BeanUtils;

public class ReviewMappers {

    public static ReviewResponseDTO toReviewResponseDTO(Review review) {
        if (review == null) return null;
        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();
        BeanUtils.copyProperties(review, reviewResponseDTO);
        reviewResponseDTO.setAuthor(AppUserMappers.toAppUserResponseDTO(review.getAuthor()));
        reviewResponseDTO.setLanguage(LanguageMappers.toLanguageResponseDTO(review.getLanguage()));
        return reviewResponseDTO;
    }

    public static Review toReview(ReviewRequestDTO reviewRequestDTO, LanguageRepository languageRepository) {
        if (reviewRequestDTO == null) return null;
        Review review = new Review();
        BeanUtils.copyProperties(reviewRequestDTO, review);
        review.setLanguage(languageRepository.findById(reviewRequestDTO.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", reviewRequestDTO.getLanguageId())));
        return review;
    }
}
