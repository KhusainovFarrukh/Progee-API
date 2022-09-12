package kh.farrukh.progee_api.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.progee_api.role.payloads.RoleRequestDTO;
import kh.farrukh.progee_api.global.base_entity.EntityWithId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.List;

import static kh.farrukh.progee_api.global.base_entity.EntityWithId.GENERATOR_NAME;
import static kh.farrukh.progee_api.global.utils.constants.DatabaseConstants.SEQUENCE_NAME_ROLE_ID;
import static kh.farrukh.progee_api.global.utils.constants.DatabaseConstants.TABLE_NAME_ROLE;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_ROLE_ID)
@Table(name = TABLE_NAME_ROLE,
        uniqueConstraints = @UniqueConstraint(name = "uk_role_title", columnNames = "title"))
public class Role extends EntityWithId {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @JsonProperty("is_default")
    private boolean isDefault = false;

    @Column(name = "permission_name")
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "fk_role_id_of_permissions")
    )
    private List<Permission> permissions;

    public Role(List<Permission> permissions) {
        this.title = "test";
        this.permissions = permissions;
    }
}
