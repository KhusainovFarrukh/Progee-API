package kh.farrukh.progee_api.endpoints.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.language.Language;

import javax.persistence.*;
import java.util.Date;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_REVIEW;

@Entity
@Table(name = TABLE_NAME_REVIEW)
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

    public Review() {
    }

    public Review(long id, String author, String body, ReviewValue value, int upVotes, int downVotes, Date createdAt, Language language) {
        this.id = id;
        this.author = author;
        this.body = body;
        this.value = value;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.createdAt = createdAt;
        this.language = language;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ReviewValue getValue() {
        return value;
    }

    public void setValue(ReviewValue value) {
        this.value = value;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getScore() {
        return this.upVotes - this.downVotes;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setLanguageId(long languageId) {
        setLanguage(new Language(languageId, "", "", false));
    }
}
