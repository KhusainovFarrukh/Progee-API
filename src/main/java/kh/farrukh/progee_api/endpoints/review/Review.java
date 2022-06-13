package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Review extends EntityWithAuthorAndCreatedAt {

    private String body;
    private ReviewValue value;
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
        setAuthorId(reviewDto.getAuthorId());
        this.body = reviewDto.getBody();
        this.value = reviewDto.getValue();
        super.setCreatedAt(ZonedDateTime.now());
    }

    /**
     * The score of a review is the number of up-votes minus the number of down-votes.
     *
     * @return The difference between the number of up-votes and down-votes.
     */
    public int getScore() {
        return this.upVotes.size() - this.downVotes.size();
    }

    /**
     * Sets the language of the current review to the language with the given ID.
     *
     * @param languageId The ID of the language to be set.
     */
    public void setLanguageId(long languageId) {
        setLanguage(new Language(languageId));
    }
}
