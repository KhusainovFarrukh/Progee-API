package kh.farrukh.progee_api.endpoints.review.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.review.ReviewValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * It's a DTO that represents a review
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDTO {

    @NotBlank(message = "Body must not be blank")
    @Size(min = 8, message = "Body must not be shorter than 8 characters")
    private String body;
    @NotNull(message = "ReviewValue must not be null")
    @JsonProperty("value")
    private ReviewValue reviewValue;
    @JsonProperty("language_id")
    private Long languageId;

    public ReviewRequestDTO(String body, ReviewValue reviewValue) {
        this.body = body;
        this.reviewValue = reviewValue;
    }
}