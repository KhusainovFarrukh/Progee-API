package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_REVIEW;

@Entity
@Table(name = TABLE_NAME_REVIEW)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String body;
    private ReviewValue value;
    @JsonProperty("up_votes")
    @Column(name = "up_votes")
    private int upVotes;
    @JsonProperty("down_votes")
    @Column(name = "down_votes")
    private int downVotes;
    @Transient
    private int score;
    @JsonProperty("created_at")
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @ManyToOne
    private AppUser author;

    @ManyToOne
    private Language language;

    public Review(ReviewDTO reviewDto) {
        setAuthorId(reviewDto.getAuthorId());
        this.body = reviewDto.getBody();
        this.value = reviewDto.getValue();
        this.upVotes = reviewDto.getUpVotes();
        this.downVotes = reviewDto.getDownVotes();
        this.createdAt = ZonedDateTime.now();
    }

    public Review(long authorId, String body, ReviewValue value) {
        setAuthorId(authorId);
        this.body = body;
        this.value = value;
        this.upVotes = 0;
        this.downVotes = 0;
        this.createdAt = ZonedDateTime.now();
    }

    public Review(long authorId, String body, ReviewValue value, int upVotes, int downVotes) {
        setAuthorId(authorId);
        this.body = body;
        this.value = value;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.createdAt = ZonedDateTime.now();
    }

    public int getScore() {
        return this.upVotes - this.downVotes;
    }

    public void setLanguageId(long languageId) {
        setLanguage(new Language(languageId));
    }

    public void setAuthorId(long authorId) {
        setAuthor(new AppUser(authorId));
    }
}
