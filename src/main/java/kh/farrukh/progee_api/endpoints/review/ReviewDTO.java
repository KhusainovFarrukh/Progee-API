package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    @NotNull
    private String body;
    @NotNull
    private ReviewValue value;
    @NotNull
    @JsonProperty("author_id")
    private long authorId;
//    @JsonProperty("language_id")
//    private long languageId;
}