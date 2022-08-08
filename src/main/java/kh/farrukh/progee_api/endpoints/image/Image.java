package kh.farrukh.progee_api.endpoints.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kh.farrukh.progee_api.base.entity.EntityWithId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import static kh.farrukh.progee_api.utils.constants.Tables.TABLE_NAME_IMAGE;

/**
 * Image is a simple entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = TABLE_NAME_IMAGE)
public class Image extends EntityWithId {

    @JsonIgnore
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] content;

    public Image(byte[] content) {
        this.content = content;
    }

    public Image(long id, byte[] content) {
        super.setId(id);
        this.content = content;
    }
}
