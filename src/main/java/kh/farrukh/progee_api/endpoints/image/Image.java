package kh.farrukh.progee_api.endpoints.image;

import kh.farrukh.progee_api.base.entity.EntityWithId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image extends EntityWithId {

    private String location;

    public Image(String location) {
        this.location = location;
    }

    public Image(long id, String location) {
        super.setId(id);
        this.location = location;
    }

}
