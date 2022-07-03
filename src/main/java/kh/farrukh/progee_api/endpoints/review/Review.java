package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.base.entity.EntityWithAuthorAndCreatedAt;
import kh.farrukh.progee_api.endpoints.language.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_REVIEW;

/**
 * Review is a simple entity
 */
@Entity
@Table(name = TABLE_NAME_REVIEW)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "body", "value", "score", "language", "up_votes", "down_votes"})
public class Review extends EntityWithAuthorAndCreatedAt {

    private String body;
    @JsonProperty("value")
    private ReviewValue reviewValue;
    @ElementCollection
    @JsonProperty("up_votes")
    private Set<Long> upVotes = Collections.emptySet();
    @ElementCollection
    @JsonProperty("down_votes")
    private Set<Long> downVotes = Collections.emptySet();
    @Transient
    private int score;

    @ManyToOne
    private Language language;

    // This is a constructor that takes a ReviewDTO object and sets the values of the current object to the values of
    // the given object.
    public Review(ReviewDTO reviewDto) {
        this.body = reviewDto.getBody();
        this.reviewValue = reviewDto.getValue();
        super.setCreatedAt(ZonedDateTime.now());
    }

    public Review(Language language) {
        this.language = language;
    }

    public Review(ReviewValue reviewValue, Language language) {
        this.reviewValue = reviewValue;
        this.language = language;
    }

    /**
     * The score of a review is the number of up-votes minus the number of down-votes.
     *
     * @return The difference between the number of up-votes and down-votes.
     */
    public int getScore() {
        return this.upVotes.size() - this.downVotes.size();
    }
}
