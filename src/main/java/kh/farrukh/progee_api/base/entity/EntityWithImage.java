package kh.farrukh.progee_api.base.entity;

import kh.farrukh.progee_api.endpoints.image.Image;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * It's a base class for entities that have an image
 *
 * WARN: extends from EntityWithAuthorAndCreatedAt
 */
@Getter
@Setter
@MappedSuperclass
public abstract class EntityWithImage extends EntityWithAuthorAndCreatedAt {

    @ManyToOne
    private Image image;

    public void setImageId(long imageId) {
        this.image = new Image(imageId, null);
    }
}