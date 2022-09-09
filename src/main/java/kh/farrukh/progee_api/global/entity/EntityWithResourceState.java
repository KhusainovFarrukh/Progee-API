package kh.farrukh.progee_api.global.entity;

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
public abstract class EntityWithResourceState extends EntityWithId{

    @Enumerated(EnumType.STRING)
    private ResourceState state = ResourceState.WAITING;
}
