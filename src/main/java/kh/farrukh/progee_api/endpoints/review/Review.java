package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.language.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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
    private String author;
    private String body;
    private ReviewValue value;
    @JsonProperty("up_votes")
    private int upVotes;
    @JsonProperty("down_votes")
    private int downVotes;
    @Transient
    private int score;
    @JsonProperty("created_at")
    private Date createdAt;

    @ManyToOne
    private Language language;

    public Review(long id, String author, String body, ReviewValue value, int upVotes, int downVotes, Date createdAt, long languageId) {
        this.id = id;
        this.author = author;
        this.body = body;
        this.value = value;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.createdAt = createdAt;
        this.language = new Language(languageId, "", "", false);
    }

    public void setLanguageId(long languageId) {
        setLanguage(new Language(languageId, "", "", false));
    }
}
