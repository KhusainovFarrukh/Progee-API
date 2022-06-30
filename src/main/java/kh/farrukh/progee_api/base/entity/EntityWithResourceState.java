package kh.farrukh.progee_api.base.entity;

import kh.farrukh.progee_api.utils.user.CurrentUserUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 * It's a base class for entities that have a resource state
 * <p>
 * WARN: extends from EntityWithImage
 */
@Getter
@Setter
@MappedSuperclass
public abstract class EntityWithResourceState extends EntityWithImage {

    @Enumerated(EnumType.STRING)
    private ResourceState state = ResourceState.WAITING;

    public void setStateAccordingToRole(boolean isAdmin) {
        if (CurrentUserUtils.isAdmin()) {
            this.state = ResourceState.APPROVED;
        } else {
            this.state = ResourceState.WAITING;
        }
    }

}
