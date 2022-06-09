package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private String body;
    private ReviewValue value;
    @JsonProperty("up_votes")
    private int upVotes = 0;
    @JsonProperty("down_votes")
    private int downVotes = 0;
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