package kh.farrukh.progee_api.endpoints.language;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.progee_api.base.entity.EntityWithResourceState;
import kh.farrukh.progee_api.base.entity.ResourceState;
import kh.farrukh.progee_api.endpoints.framework.Framework;
import kh.farrukh.progee_api.endpoints.image.Image;
import kh.farrukh.progee_api.endpoints.image.ImageRepository;
import kh.farrukh.progee_api.endpoints.review.Review;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import kh.farrukh.progee_api.exception.custom_exceptions.ResourceNotFoundException;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

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
@NamedEntityGraphs({
        @NamedEntityGraph(name = "language_with_frameworks", attributeNodes = @NamedAttributeNode("frameworks")),
        @NamedEntityGraph(name = "language_with_reviews", attributeNodes = @NamedAttributeNode("reviews"))
})
public class Language extends EntityWithResourceState {

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(
            name = "image_id",
            foreignKey = @ForeignKey(name = "fk_image_id_of_language")
    )
    private Image image;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "language", cascade = CascadeType.REMOVE)
    private List<Review> reviews;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "language", cascade = CascadeType.REMOVE)
    private List<Framework> frameworks;

    @ManyToOne
    @JoinColumn(
            name = "author_id",
            foreignKey = @ForeignKey(name = "fk_author_id_of_language")
    )
    private AppUser author;

    @CreationTimestamp
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

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
        this.image = imageRepository.findById(languageDto.getImageId()).orElseThrow(
                () -> new ResourceNotFoundException("Image", "id", languageDto.getImageId())
        );
    }
}
