package kh.farrukh.progee_api.base.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class EntityWithId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
}
