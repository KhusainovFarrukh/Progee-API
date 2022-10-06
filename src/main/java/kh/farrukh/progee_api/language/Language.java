package kh.farrukh.progee_api.language;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.framework.Framework;
import kh.farrukh.progee_api.global.base_entity.EntityWithResourceState;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.review.Review;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

import static kh.farrukh.progee_api.global.base_entity.EntityWithId.GENERATOR_NAME;
import static kh.farrukh.progee_api.language.LanguageConstants.SEQUENCE_NAME_LANGUAGE_ID;
import static kh.farrukh.progee_api.language.LanguageConstants.TABLE_NAME_LANGUAGE;

/**
 * It's a Entity class that represents a language
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @ToString.Exclude
    @OneToMany(mappedBy = "language", cascade = CascadeType.REMOVE)
    private List<Review> reviews;

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
    private ZonedDateTime createdAt;

    public Language(long id) {
        super.setId(id);
    }

    public Language(
            long id,
            String name,
            String description,
            Image image,
            List<Review> reviews,
            List<Framework> frameworks,
            AppUser author,
            ZonedDateTime createdAt
    ) {
        super.setId(id);
        this.name = name;
        this.description = description;
        this.image = image;
        this.reviews = reviews;
        this.frameworks = frameworks;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Language(String name) {
        this.name = name;
    }

    public Language(String name, ResourceState state) {
        this.name = name;
        super.setState(state);
    }

    public Language(AppUser author) {
        this.author = author;
    }
}
