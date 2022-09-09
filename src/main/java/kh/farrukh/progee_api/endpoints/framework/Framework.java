package kh.farrukh.progee_api.endpoints.framework;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.endpoints.framework.payloads.FrameworkRequestDTO;
import kh.farrukh.progee_api.endpoints.language.LanguageRepository;
import kh.farrukh.progee_api.global.entity.EntityWithResourceState;
import kh.farrukh.progee_api.global.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.language.Language;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.exceptions.custom_exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static kh.farrukh.progee_api.global.entity.EntityWithId.GENERATOR_NAME;
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
    @JoinColumn(
            name = "image_id",
            foreignKey = @ForeignKey(name = "fk_image_id_of_framework")
    )
    private Image image;

    @ManyToOne
    @JoinColumn(
            name = "author_id",
            foreignKey = @ForeignKey(name = "fk_author_id_of_framework")
    )
    private AppUser author;

    @CreationTimestamp
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @ManyToOne
    @JoinColumn(
            name = "language_id",
            foreignKey = @ForeignKey(name = "fk_language_id_of_framework")
    )
    private Language language;

    // This is a constructor that takes a FrameworkDTO object and
    // sets the values of the current object to the values of
    // the given object.
    public Framework(FrameworkRequestDTO frameworkRequestDto, LanguageRepository languageRepository, ImageRepository imageRepository) {
        this.name = frameworkRequestDto.getName();
        this.description = frameworkRequestDto.getDescription();
        this.image = imageRepository.findById(frameworkRequestDto.getImageId()).orElseThrow(
                () -> new ResourceNotFoundException("Image", "id", frameworkRequestDto.getImageId())
        );
        this.language = languageRepository.findById(frameworkRequestDto.getLanguageId()).orElseThrow(
                () -> new ResourceNotFoundException("Language", "id", frameworkRequestDto.getLanguageId())
        );
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
