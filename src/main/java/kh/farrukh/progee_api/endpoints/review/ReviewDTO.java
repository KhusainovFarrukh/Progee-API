package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    @NotNull
    private String body;
    @NotNull
    private ReviewValue value;
    @JsonProperty("up_votes")
    private int upVotes = 0;
    @JsonProperty("down_votes")
    private int downVotes = 0;
    @NotNull
    @JsonProperty("author_id")
    private long authorId;
//    @JsonProperty("language_id")
//    private long languageId;

    public ReviewDTO(String body, ReviewValue value, long authorId) {
        this.body = body;
        this.value = value;
        this.authorId = authorId;
//        this.languageId = languageId;
    }
}