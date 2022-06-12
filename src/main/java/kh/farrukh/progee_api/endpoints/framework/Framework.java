package kh.farrukh.progee_api.endpoints.framework;

import kh.farrukh.progee_api.base.entity.EntityWithResourceState;
import kh.farrukh.progee_api.endpoints.language.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

import static kh.farrukh.progee_api.utils.constant.Tables.TABLE_NAME_FRAMEWORK;

@Entity
@Table(name = TABLE_NAME_FRAMEWORK)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Framework extends EntityWithResourceState {

    @Column(unique = true)
    private String name;
    private String description;

    @ManyToOne
    private Language language;

    public Framework(FrameworkDTO frameworkDto) {
        this.name = frameworkDto.getName();
        this.description = frameworkDto.getDescription();
        super.setImageId(frameworkDto.getImageId());
        super.setAuthorId(frameworkDto.getAuthorId());
        super.setCreatedAt(ZonedDateTime.now());
    }

    public void setLanguageId(long languageId) {
        setLanguage(new Language(languageId));
    }
}
