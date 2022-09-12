package kh.farrukh.progee_api.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.language.Language;
import kh.farrukh.progee_api.user.AppUser;
import kh.farrukh.progee_api.global.base_entity.EntityWithId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import static kh.farrukh.progee_api.global.base_entity.EntityWithId.GENERATOR_NAME;
import static kh.farrukh.progee_api.global.utils.constants.DatabaseConstants.SEQUENCE_NAME_REVIEW_ID;
import static kh.farrukh.progee_api.global.utils.constants.DatabaseConstants.TABLE_NAME_REVIEW;

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
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_REVIEW_ID)
public class Review extends EntityWithId {

    private String body;

    @JsonProperty("value")
    private ReviewValue reviewValue;

    @Column(name = "up_voter_id")
    @ElementCollection
    @CollectionTable(
            name = "review_up_votes",
            joinColumns = @JoinColumn(name = "review_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "fk_review_id_of_up_votes")
    )
    @JsonProperty("up_votes")
    private Set<Long> upVotes = new HashSet<>();

    @Column(name = "down_voter_id")
    @ElementCollection
    @CollectionTable(
            name = "review_down_votes",
            joinColumns = @JoinColumn(name = "review_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "fk_review_id_of_down_votes")
    )
    @JsonProperty("down_votes")
    private Set<Long> downVotes = new HashSet<>();

    @Transient
    private int score;

    @ManyToOne
    @JoinColumn(
            name = "language_id",
            foreignKey = @ForeignKey(name = "fk_language_id_of_review")
    )
    private Language language;

    @ManyToOne
    @JoinColumn(
            name = "author_id",
            foreignKey = @ForeignKey(name = "fk_author_id_of_review")
    )
    private AppUser author;

    @CreationTimestamp
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    public Review(Language language) {
        this.language = language;
    }

    public Review(ReviewValue reviewValue, Language language) {
        this.reviewValue = reviewValue;
        this.language = language;
    }

    public Review(String body, ReviewValue reviewValue, Language language) {
        this.body = body;
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
