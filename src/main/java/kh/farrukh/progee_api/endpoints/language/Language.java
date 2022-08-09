package kh.farrukh.progee_api.endpoints.language;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.base.entity.EntityWithResourceState;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.ZonedDateTime;

import static kh.farrukh.progee_api.base.entity.EntityWithId.GENERATOR_NAME;
import static kh.farrukh.progee_api.utils.constants.DatabaseConstants.SEQUENCE_NAME_LANGUAGE_ID;
import static kh.farrukh.progee_api.utils.constants.DatabaseConstants.TABLE_NAME_LANGUAGE;

/**
 * Language is a simple entity
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "description", "state", "image"})
@Entity
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_LANGUAGE_ID)
@Table(
        name = TABLE_NAME_LANGUAGE,
        uniqueConstraints = @UniqueConstraint(name = "uk_language_name", columnNames = "name")
)
public class Language extends EntityWithResourceState {

    private String name;
    private String description;

    public Language(long id) {
        super.setId(id);
    }

    public Language(String name) {
        this.name = name;
    }

    public Language(String name, ResourceState state) {
        this.name = name;
        super.setState(state);
    }

    // This is a constructor that takes a LanguageDTO object and
    // sets the values of the current object to the values of
    // the given object.
    public Language(LanguageDTO languageDto, ImageRepository imageRepository) {
        this.name = languageDto.getName();
        this.description = languageDto.getDescription();
        super.setImage(imageRepository.findById(languageDto.getImageId()).orElseThrow(
                () -> new ResourceNotFoundException("Image", "id", languageDto.getImageId())
        ));
        super.setCreatedAt(ZonedDateTime.now());
    }
}
