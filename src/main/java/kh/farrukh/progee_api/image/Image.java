package kh.farrukh.progee_api.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kh.farrukh.progee_api.global.base_entity.EntityWithId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static kh.farrukh.progee_api.global.base_entity.EntityWithId.GENERATOR_NAME;
import static kh.farrukh.progee_api.global.utils.constants.DatabaseConstants.SEQUENCE_NAME_IMAGE_ID;
import static kh.farrukh.progee_api.global.utils.constants.DatabaseConstants.TABLE_NAME_IMAGE;

/**
 * Image is a simple entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = TABLE_NAME_IMAGE)
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_IMAGE_ID)
public class Image extends EntityWithId {

    @JsonIgnore
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] content;

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
}
