package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.base.entity.EntityWithResourceState;
import kh.farrukh.progee_api.endpoints.language.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_FRAMEWORK;

/**
 * Framework is a simple entity
 */
@Entity
@Table(name = TABLE_NAME_FRAMEWORK)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Framework extends EntityWithResourceState {

    @Column(unique = true)
    private String name;
    private String description;

    @ManyToOne
    private Language language;

    // This is a constructor that takes a FrameworkDTO object and
    // sets the values of the current object to the values of
    // the given object.
    public Framework(FrameworkDTO frameworkDto) {
        this.name = frameworkDto.getName();
        this.description = frameworkDto.getDescription();
        super.setImageId(frameworkDto.getImageId());
        super.setCreatedAt(ZonedDateTime.now());
    }

    /**
     * Sets the language of the current framework to the language with the given ID.
     *
     * @param languageId The ID of the language to be set.
     */
    public void setLanguageId(long languageId) {
        setLanguage(new Language(languageId));
    }
}
