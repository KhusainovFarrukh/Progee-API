package kh.farrukh.progee_api.review;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.global.base_entity.EntityWithId;
import kh.farrukh.progee_api.language.Language;
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
import static kh.farrukh.progee_api.review.ReviewConstants.SEQUENCE_NAME_REVIEW_ID;
import static kh.farrukh.progee_api.review.ReviewConstants.TABLE_NAME_REVIEW;

/**
 * It's a review of a language
 */
@Entity
@Table(name = TABLE_NAME_REVIEW)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_REVIEW_ID)
public class Review extends EntityWithId {

    private String body;

    private ReviewValue reviewValue;

    @Column(name = "up_voter_id")
    @ElementCollection
    @CollectionTable(
            name = "review_up_votes",
            joinColumns = @JoinColumn(name = "review_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "fk_review_id_of_up_votes")
    )
    private Set<Long> upVotes = new HashSet<>();

    @Column(name = "down_voter_id")
    @ElementCollection
    @CollectionTable(
            name = "review_down_votes",
            joinColumns = @JoinColumn(name = "review_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "fk_review_id_of_down_votes")
    )
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
    private ZonedDateTime createdAt;

    public Review(
            long id,
            String body,
            ReviewValue reviewValue,
            Set<Long> upVotes,
            Set<Long> downVotes,
            int score,
            Language language,
            AppUser author,
            ZonedDateTime createdAt
    ) {
        super.setId(id);
        this.body = body;
        this.reviewValue = reviewValue;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.score = score;
        this.language = language;
        this.author = author;
        this.createdAt = createdAt;
    }

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
