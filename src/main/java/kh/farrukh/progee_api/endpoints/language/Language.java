package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.entity.EntityWithResourceState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.ZonedDateTime;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_LANGUAGE;

/**
 * Language is a simple entity
 */
@Entity
@Table(name = TABLE_NAME_LANGUAGE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Language extends EntityWithResourceState {

    @Column(unique = true)
    private String name;
    private String description;

    public Language(long id) {
        super.setId(id);
    }

    // This is a constructor that takes a LanguageDTO object and
    // sets the values of the current object to the values of
    // the given object.
    public Language(LanguageDTO languageDto) {
        this.name = languageDto.getName();
        this.description = languageDto.getDescription();
        super.setImageId(languageDto.getImageId());
        super.setCreatedAt(ZonedDateTime.now());
        super.setAuthorId(languageDto.getAuthorId());
    }
}
