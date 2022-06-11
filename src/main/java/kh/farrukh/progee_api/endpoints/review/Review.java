package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.base.entity.EntityWithAuthorAndCreatedAt;
import kh.farrukh.progee_api.endpoints.language.Language;
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
public class Review extends EntityWithAuthorAndCreatedAt {

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

    @ManyToOne
    private Language language;

    public Review(ReviewDTO reviewDto) {
        setAuthorId(reviewDto.getAuthorId());
        this.body = reviewDto.getBody();
        this.value = reviewDto.getValue();
        this.upVotes = reviewDto.getUpVotes();
        this.downVotes = reviewDto.getDownVotes();
        super.setCreatedAt(ZonedDateTime.now());
    }

    public int getScore() {
        return this.upVotes - this.downVotes;
    }

    public void setLanguageId(long languageId) {
        setLanguage(new Language(languageId));
    }
}
