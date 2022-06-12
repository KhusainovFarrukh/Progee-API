package kh.farrukh.progee_api.endpoints.language;

import kh.farrukh.progee_api.base.entity.EntityWithImage;
import kh.farrukh.progee_api.base.entity.EntityWithResourceState;
import kh.farrukh.progee_api.base.entity.ResourceState;
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
public class Language extends EntityWithResourceState {

    @Column(unique = true)
    private String name;
    private String description;

    public Language(long id) {
        super.setId(id);
    }

    public Language(LanguageDTO languageDto) {
        this.name = languageDto.getName();
        this.description = languageDto.getDescription();
        super.setImageId(languageDto.getImageId());
        super.setCreatedAt(ZonedDateTime.now());
        super.setAuthorId(languageDto.getAuthorId());
    }
}
