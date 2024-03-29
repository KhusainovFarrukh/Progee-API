package kh.farrukh.progee_api.framework;

import kh.farrukh.progee_api.app_user.AppUser;
import kh.farrukh.progee_api.global.base_entity.EntityWithResourceState;
import kh.farrukh.progee_api.global.resource_state.ResourceState;
import kh.farrukh.progee_api.image.Image;
import kh.farrukh.progee_api.language.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static kh.farrukh.progee_api.framework.FrameworkConstants.SEQUENCE_NAME_FRAMEWORK_ID;
import static kh.farrukh.progee_api.framework.FrameworkConstants.TABLE_NAME_FRAMEWORK;
import static kh.farrukh.progee_api.global.base_entity.EntityWithId.GENERATOR_NAME;

/**
 * It's a framework entity that has a name, description, image, author, creation date, and language
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private ZonedDateTime createdAt;

    @ManyToOne
    @JoinColumn(
            name = "language_id",
            foreignKey = @ForeignKey(name = "fk_language_id_of_framework")
    )
    private Language language;

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

    public Framework(
            long id,
            ResourceState state,
            String name,
            String description,
            Image image,
            AppUser author,
            ZonedDateTime createdAt,
            Language language
    ) {
        super.setId(id);
        super.setState(state);
        this.name = name;
        this.description = description;
        this.image = image;
        this.author = author;
        this.createdAt = createdAt;
        this.language = language;
    }

    public Framework(ResourceState state) {
        super.setState(state);
    }
}
