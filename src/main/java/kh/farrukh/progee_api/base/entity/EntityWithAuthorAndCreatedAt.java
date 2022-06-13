package kh.farrukh.progee_api.base.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.endpoints.user.AppUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

/**
 * It's a base class for entities that have an author and a creation date
 *
 * WARN: extends from EntityWithId
 */
@Getter
@Setter
@MappedSuperclass
public abstract class EntityWithAuthorAndCreatedAt extends EntityWithId {

    @ManyToOne
    private AppUser author;
    @JsonProperty("created_at")
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    public void setAuthorId(long authorId) {
        setAuthor(new AppUser(authorId));
    }
}
