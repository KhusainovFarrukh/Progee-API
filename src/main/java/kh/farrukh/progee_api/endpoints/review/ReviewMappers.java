package kh.farrukh.progee_api.endpoints.review;

import kh.farrukh.progee_api.endpoints.language.LanguageMappers;
import kh.farrukh.progee_api.endpoints.review.payloads.ReviewResponseDTO;
import kh.farrukh.progee_api.endpoints.user.AppUserMappers;
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
}
