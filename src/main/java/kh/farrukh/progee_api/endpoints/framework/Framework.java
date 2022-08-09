package kh.farrukh.progee_api.endpoints.framework;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.base.entity.EntityWithResourceState;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static kh.farrukh.progee_api.base.entity.EntityWithId.GENERATOR_NAME;
import static kh.farrukh.progee_api.utils.constants.DatabaseConstants.SEQUENCE_NAME_FRAMEWORK_ID;
import static kh.farrukh.progee_api.utils.constants.DatabaseConstants.TABLE_NAME_FRAMEWORK;

/**
 * Framework is a simple entity
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "description", "state", "image", "language"})
@Entity
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_FRAMEWORK_ID)
@Table(
        name = TABLE_NAME_FRAMEWORK,
        uniqueConstraints = @UniqueConstraint(name = "uk_framework_name", columnNames = "name")
)
public class Framework extends EntityWithResourceState {

    private String name;
    private String description;

    @ManyToOne
    private Language language;

    // This is a constructor that takes a FrameworkDTO object and
    // sets the values of the current object to the values of
    // the given object.
    public Framework(FrameworkDTO frameworkDto, ImageRepository imageRepository) {
        this.name = frameworkDto.getName();
        this.description = frameworkDto.getDescription();
        super.setImage(imageRepository.findById(frameworkDto.getImageId()).orElseThrow(
                () -> new ResourceNotFoundException("Image", "id", frameworkDto.getImageId())
        ));
        super.setCreatedAt(ZonedDateTime.now());
    }

    public Framework(String name) {
        this.name = name;
    }

    public Framework(Language language) {
        this.language = language;
    }

    public Framework(String name, ResourceState state, Language language) {
        this.name = name;
        super.setState(state);
        this.language = language;
    }
}
