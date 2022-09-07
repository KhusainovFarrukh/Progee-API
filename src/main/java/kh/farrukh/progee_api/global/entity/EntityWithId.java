package kh.farrukh.progee_api.global.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * This is a base class for all entities that have an id.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class EntityWithId {

    public static final String GENERATOR_NAME = "id_generator";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    private long id;
}