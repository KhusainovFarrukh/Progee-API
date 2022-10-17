package kh.farrukh.progee_api.image;

import kh.farrukh.progee_api.global.base_entity.EntityWithId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

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

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] content;

    @Column(unique = true)
    private String name;

    private String url;

    private Float size;

    public Image(long id) {
        super.setId(id);
    }

    public Image(byte[] content) {
        this.content = content;
    }

    public Image(long id, byte[] content) {
        super.setId(id);
        this.content = content;
    }

    public Image(String name, String url, Float size) {
        this.name = name;
        this.url = url;
        this.size = size;
    }
}
