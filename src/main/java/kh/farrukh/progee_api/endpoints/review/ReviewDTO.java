package kh.farrukh.progee_api.endpoints.review;

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
public class ReviewDTO {

    @NotBlank(message = "Body must not be blank")
    @Size(min = 8, message = "Body must not be shorter than 8 characters")
    private String body;
    @NotNull(message = "ReviewValue must not be null")
    private ReviewValue value;
//    @JsonProperty("language_id")
//    private long languageId;
}