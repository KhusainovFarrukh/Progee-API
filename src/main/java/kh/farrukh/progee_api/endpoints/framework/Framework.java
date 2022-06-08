package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.language.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_FRAMEWORK;

@Entity
@Table(name = TABLE_NAME_FRAMEWORK)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Framework {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String name;
    private String description;
    @ManyToOne
    private Image image;

    @ManyToOne
    private Language language;

    public Framework(long id, String name, String description, long languageId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.language = new Language(languageId, "", "");
    }

    public Framework(String name, String description, long languageId) {
        this.name = name;
        this.description = description;
        this.language = new Language(languageId, "", "");
    }

    public Framework(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Framework(FrameworkDTO frameworkDto) {
        this.name = frameworkDto.getName();
        this.description = frameworkDto.getDescription();
        setImageId(frameworkDto.getImageId());
    }

    public void setLanguageId(long languageId) {
        setLanguage(new Language(languageId, "", ""));
    }

    public void setImageId(long imageId) {
        this.image = new Image(imageId, "");
    }
}
