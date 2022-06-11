package kh.farrukh.progee_api.base.entity;

import kh.farrukh.progee_api.endpoints.image.Image;
import lombok.Data;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class EntityWithImage extends EntityWithAuthorAndCreatedAt {

    @ManyToOne
    private Image image;

    public void setImageId(long imageId) {
        this.image = new Image(imageId, "");
    }
}