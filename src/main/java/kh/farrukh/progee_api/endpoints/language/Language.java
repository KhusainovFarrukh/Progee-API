package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.endpoints.image.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @ManyToOne
    private Image image;

    public Language(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Language(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Language(String name, String description, long imageId) {
        this.name = name;
        this.description = description;
        this.image = new Image(imageId, "");
    }

    public Language(LanguageDTO languageDTO) {
        this.name = languageDTO.getName();
        this.description = languageDTO.getDescription();
        this.image = new Image(languageDTO.getImageId(), "");
    }

    public void setImageId(long imageId) {
        this.image = new Image(imageId, "");
    }
}
