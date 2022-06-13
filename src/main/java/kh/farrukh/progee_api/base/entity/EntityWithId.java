package kh.farrukh.progee_api.base.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class EntityWithId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
