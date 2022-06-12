package kh.farrukh.progee_api.base.entity;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class EntityWithResourceState extends EntityWithImage {

    @Enumerated(EnumType.STRING)
    private ResourceState state = ResourceState.WAITING;

}
