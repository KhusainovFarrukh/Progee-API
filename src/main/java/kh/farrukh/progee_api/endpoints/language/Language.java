package kh.farrukh.progee_api.endpoints.language;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.ZonedDateTime;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_LANGUAGE;

@Entity
@Table(name = TABLE_NAME_LANGUAGE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String name;
    private String description;
    @JsonProperty("created_at")
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @ManyToOne
    private Image image;

    @ManyToOne
    private AppUser author;

    public Language(long id) {
        this.id = id;
    }

    public Language(LanguageDTO languageDto) {
        setAuthorId(languageDto.getAuthorId());
        setImageId(languageDto.getImageId());
        this.name = languageDto.getName();
        this.description = languageDto.getDescription();
        this.createdAt = ZonedDateTime.now();
    }

    public void setImageId(long imageId) {
        this.image = new Image(imageId, "");
    }

    public void setAuthorId(long authorId) {
        setAuthor(new AppUser(authorId));
    }
}
