package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.global.base_entity.EntityWithId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static kh.farrukh.progee_api.global.base_entity.EntityWithId.GENERATOR_NAME;
import static kh.farrukh.progee_api.image.ImageConstants.SEQUENCE_NAME_IMAGE_ID;
import static kh.farrukh.progee_api.image.ImageConstants.TABLE_NAME_IMAGE;

/**
 * It's a simple entity with a byte array field for storing images.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = TABLE_NAME_IMAGE)
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_IMAGE_ID)
public class Image extends EntityWithId {

    @Column(unique = true)
    private String name;

    private String url;

    private Float size;

    public Image(long id) {
        super.setId(id);
    }

    public Image(String name, String url, Float size) {
        this.name = name;
        this.url = url;
        this.size = size;
    }

    public Image(Long id, String name, String url, Float size) {
        super.setId(id);
        this.name = name;
        this.url = url;
        this.size = size;
    }
}
