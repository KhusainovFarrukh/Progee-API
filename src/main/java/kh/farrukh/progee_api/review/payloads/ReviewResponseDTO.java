package kh.farrukh.progee_api.review.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.language.payloads.LanguageResponseDTO;
import kh.farrukh.progee_api.review.ReviewValue;
import kh.farrukh.progee_api.app_user.payloads.AppUserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "body", "value", "score", "language", "up_votes", "down_votes"})
public class ReviewResponseDTO {

    private long id;

    private String body;

    @JsonProperty("value")
    private ReviewValue reviewValue;

    @JsonProperty("up_votes")
    private Set<Long> upVotes = new HashSet<>();

    @JsonProperty("down_votes")
    private Set<Long> downVotes = new HashSet<>();

    private int score;

    private LanguageResponseDTO language;

    private AppUserResponseDTO author;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

}
